param(
    [string] $BaseUrl = 'https://localhost:8443',
    [string] $SenderUsername = 'sender',
    [string] $SenderPassword = 'sender'
)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
$baseUri = [uri]::new($BaseUrl)
if ($baseUri.Scheme -ne 'https' -or -not $baseUri.IsLoopback) {
    throw 'BaseUrl must be an HTTPS loopback address; this verifier is for local Compose only.'
}
$BaseUrl = $baseUri.GetLeftPart([System.UriPartial]::Authority)

$handler = [System.Net.Http.HttpClientHandler]::new()
$handler.AllowAutoRedirect = $false
$handler.ServerCertificateCustomValidationCallback = [System.Net.Http.HttpClientHandler]::DangerousAcceptAnyServerCertificateValidator
$client = [System.Net.Http.HttpClient]::new($handler)
$client.Timeout = [TimeSpan]::FromSeconds(30)

function Get-ResponseBody($response) {
    return $response.Content.ReadAsStringAsync().GetAwaiter().GetResult()
}

function Get-Token {
    $state = [Guid]::NewGuid().ToString('N')
    $nonce = [Guid]::NewGuid().ToString('N')
    $redirectUri = "$BaseUrl/"
    $authUri = "$BaseUrl/auth/realms/snapsends3/protocol/openid-connect/auth?client_id=snapsends3-web&response_type=code&scope=openid&state=$state&nonce=$nonce&redirect_uri=$([uri]::EscapeDataString($redirectUri))"
    $login = $client.GetAsync($authUri).GetAwaiter().GetResult()
    if (-not $login.IsSuccessStatusCode) { throw "Keycloak login page returned $([int]$login.StatusCode)." }
    $html = Get-ResponseBody $login
    $formMatch = [regex]::Match($html, '(?is)<form\b[^>]*id=["'']kc-form-login["''][^>]*>.*?</form>')
    if (-not $formMatch.Success) { throw 'Keycloak login form was not found.' }
    $formHtml = $formMatch.Value
    $actionMatch = [regex]::Match($formHtml, '(?is)action=["'']([^"'']+)["'']')
    if (-not $actionMatch.Success) { throw 'Keycloak login action was not found.' }
    $action = [System.Net.WebUtility]::HtmlDecode($actionMatch.Groups[1].Value)
    $fields = [System.Collections.Generic.List[System.Collections.Generic.KeyValuePair[string,string]]]::new()
    foreach ($input in [regex]::Matches($formHtml, '(?is)<input\b[^>]*>')) {
        $tag = $input.Value
        $type = [regex]::Match($tag, '(?is)type=["'']([^"'']+)["'']')
        if (-not $type.Success -or $type.Groups[1].Value -ne 'hidden') { continue }
        $name = [regex]::Match($tag, '(?is)name=["'']([^"'']+)["'']')
        $value = [regex]::Match($tag, '(?is)value=["'']([^"'']*)["'']')
        if ($name.Success) {
            $fields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new(
                [System.Net.WebUtility]::HtmlDecode($name.Groups[1].Value),
                $(if ($value.Success) { [System.Net.WebUtility]::HtmlDecode($value.Groups[1].Value) } else { '' })
            ))
        }
    }
    $fields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('username', $SenderUsername))
    $fields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('password', $SenderPassword))
    $form = [System.Net.Http.FormUrlEncodedContent]::new($fields)
    $loginResult = $client.PostAsync([uri]::new([uri]$authUri, $action), $form).GetAwaiter().GetResult()
    if ($null -eq $loginResult.Headers.Location) { throw 'Keycloak did not redirect after the local demo login.' }
    $callback = [uri]::new([uri]$authUri, $loginResult.Headers.Location)
    $code = [System.Web.HttpUtility]::ParseQueryString($callback.Query).Get('code')
    if (-not $code) { throw 'Keycloak callback did not contain an authorization code.' }
    $tokenFields = [System.Collections.Generic.List[System.Collections.Generic.KeyValuePair[string,string]]]::new()
    $tokenFields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('grant_type', 'authorization_code'))
    $tokenFields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('client_id', 'snapsends3-web'))
    $tokenFields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('redirect_uri', $redirectUri))
    $tokenFields.Add([System.Collections.Generic.KeyValuePair[string,string]]::new('code', $code))
    $tokenBody = [System.Net.Http.FormUrlEncodedContent]::new($tokenFields)
    $tokenResponse = $client.PostAsync("$BaseUrl/auth/realms/snapsends3/protocol/openid-connect/token", $tokenBody).GetAwaiter().GetResult()
    if (-not $tokenResponse.IsSuccessStatusCode) { throw 'Keycloak token exchange failed.' }
    return ((Get-ResponseBody $tokenResponse | ConvertFrom-Json).access_token)
}

function Invoke-Api($method, $path, $token, $body = $null) {
    $request = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::new($method), "$BaseUrl$path")
    if ($token) { $request.Headers.Authorization = [System.Net.Http.Headers.AuthenticationHeaderValue]::new('Bearer', $token) }
    if ($null -ne $body) {
        $json = $body | ConvertTo-Json -Compress
        $request.Content = [System.Net.Http.StringContent]::new($json, [System.Text.Encoding]::UTF8, 'application/json')
    }
    $response = $client.SendAsync($request).GetAwaiter().GetResult()
    if (-not $response.IsSuccessStatusCode) { throw "$method $path returned $([int]$response.StatusCode)." }
    return (Get-ResponseBody $response | ConvertFrom-Json)
}

function Upload-TestFile($shareId, $token, $name, $contentType, [byte[]] $bytes) {
    $intent = Invoke-Api 'POST' "/api/v1/shares/$shareId/files" $token @{
        fileName = $name; sizeBytes = $bytes.Length; contentType = $contentType
    }
    $put = [System.Net.Http.HttpRequestMessage]::new([System.Net.Http.HttpMethod]::Put, $intent.uploadUrl)
    $put.Content = [System.Net.Http.ByteArrayContent]::new($bytes)
    $put.Content.Headers.ContentType = [System.Net.Http.Headers.MediaTypeHeaderValue]::Parse($contentType)
    $result = $client.SendAsync($put).GetAwaiter().GetResult()
    if (-not $result.IsSuccessStatusCode) { throw "Upload of $name returned $([int]$result.StatusCode)." }
}

function Wait-UntilReady($shareId, $token) {
    for ($attempt = 0; $attempt -lt 30; $attempt++) {
        $share = Invoke-Api 'GET' "/api/v1/shares/$shareId" $token
        if ($share.status -eq 'READY') { return }
        if ($share.status -eq 'REJECTED' -or $share.status -eq 'EXPIRED') { throw "Share ended in $($share.status)." }
        Start-Sleep -Seconds 2
    }
    throw 'Share did not become READY within 60 seconds.'
}

function Get-Download($token, [byte[]] $bytes = $null) {
    $response = Invoke-Api 'POST' "/api/v1/public/shares/$token/download" $null @{}
    $download = $client.GetAsync($response.downloadUrl).GetAwaiter().GetResult()
    if (-not $download.IsSuccessStatusCode) { throw "Download returned $([int]$download.StatusCode)." }
    $disposition = $download.Content.Headers.ContentDisposition
    if ($disposition.DispositionType -ne 'attachment') { throw "Download disposition was '$($disposition.DispositionType)'." }
    $downloadBytes = $download.Content.ReadAsByteArrayAsync().GetAwaiter().GetResult()
    if ($null -ne $bytes -and ($downloadBytes.Length -ne $bytes.Length -or [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData($downloadBytes)) -ne [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData($bytes)))) {
        throw 'Downloaded bytes did not match the uploaded source bytes.'
    }
    return [pscustomobject]@{ FileName = $disposition.FileNameStar ?? $disposition.FileName; Bytes = $downloadBytes }
}

try {
    $health = $client.GetAsync("$BaseUrl/api/health").GetAwaiter().GetResult()
    if (-not $health.IsSuccessStatusCode) { throw "API health returned $([int]$health.StatusCode)." }
    if ((Get-ResponseBody $health | ConvertFrom-Json).status -ne 'UP') { throw 'API health status was not UP.' }
    $token = Get-Token

    $png = [Convert]::FromBase64String('iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+/a6oAAAAASUVORK5CYII=')
    $singleShare = Invoke-Api 'POST' '/api/v1/shares' $token @{}
    Upload-TestFile $singleShare.id $token 'sns003-proof.png' 'image/png' $png
    $null = Invoke-Api 'POST' "/api/v1/shares/$($singleShare.id)/finalization" $token
    Wait-UntilReady $singleShare.id $token
    $single = Get-Download $singleShare.token $png
    if ($single.FileName -ne 'sns003-proof.png') { throw "Single-file download name was '$($single.FileName)'." }
    if ([Convert]::ToHexString($single.Bytes[0..7]) -ne '89504E470D0A1A0A') { throw 'Single-file download did not contain PNG magic bytes.' }
    $width = ([uint32]$single.Bytes[16] -shl 24) -bor ([uint32]$single.Bytes[17] -shl 16) -bor ([uint32]$single.Bytes[18] -shl 8) -bor [uint32]$single.Bytes[19]
    $height = ([uint32]$single.Bytes[20] -shl 24) -bor ([uint32]$single.Bytes[21] -shl 16) -bor ([uint32]$single.Bytes[22] -shl 8) -bor [uint32]$single.Bytes[23]
    if ($width -ne 1 -or $height -ne 1) { throw "PNG dimensions were ${width}x${height}, expected 1x1." }

    $first = [System.Text.Encoding]::UTF8.GetBytes('SNS003 first archive entry')
    $second = [System.Text.Encoding]::UTF8.GetBytes('SNS003 second archive entry')
    $multiShare = Invoke-Api 'POST' '/api/v1/shares' $token @{}
    Upload-TestFile $multiShare.id $token 'one.txt' 'text/plain' $first
    Upload-TestFile $multiShare.id $token 'two.txt' 'text/plain' $second
    $null = Invoke-Api 'POST' "/api/v1/shares/$($multiShare.id)/finalization" $token
    Wait-UntilReady $multiShare.id $token
    $archive = Get-Download $multiShare.token
    if ($archive.FileName -ne 'snapsends3-share.zip') { throw "Multi-file download name was '$($archive.FileName)'." }
    if ($archive.Bytes.Length -lt 4 -or [Convert]::ToHexString($archive.Bytes[0..1]) -ne '504B') { throw 'Multi-file download did not have ZIP magic bytes.' }
    $stream = [System.IO.MemoryStream]::new($archive.Bytes)
    $zip = [System.IO.Compression.ZipArchive]::new($stream, [System.IO.Compression.ZipArchiveMode]::Read)
    try {
        $entries = @($zip.Entries | Sort-Object FullName)
        $oneEntry = @($entries | Where-Object { $_.FullName -match '^\d{3}-one\.txt$' })
        $twoEntry = @($entries | Where-Object { $_.FullName -match '^\d{3}-two\.txt$' })
        if ($entries.Count -ne 2 -or $oneEntry.Count -ne 1 -or $twoEntry.Count -ne 1) { throw "ZIP entries were '$($entries.FullName -join ',')'." }
        foreach ($entry in $entries) {
            $reader = [System.IO.StreamReader]::new($entry.Open())
            try { $content = $reader.ReadToEnd() } finally { $reader.Dispose() }
            $expected = if ($entry.FullName.EndsWith('one.txt')) { 'SNS003 first archive entry' } else { 'SNS003 second archive entry' }
            if ($content -ne $expected) { throw "ZIP content for $($entry.FullName) did not match the uploaded bytes." }
        }
    } finally {
        $zip.Dispose()
        $stream.Dispose()
    }
    [pscustomobject]@{
        ApiHealth = 'UP'
        OneFile = 'PASS: attachment sns003-proof.png; PNG magic and SHA-256 source match'
        OneFileSha256 = [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData($single.Bytes))
        MultiFile = 'PASS: attachment snapsends3-share.zip; ZIP opens; both entry contents match uploads'
        MultiFileSha256 = [Convert]::ToHexString([System.Security.Cryptography.SHA256]::HashData($archive.Bytes))
        ZipEntries = ($entries.FullName -join ', ')
    }
} finally {
    $client.Dispose()
    $handler.Dispose()
}
