package com.snapsends3.share;

import com.snapsends3.openapi.api.PublicSharesApi;
import com.snapsends3.openapi.model.PublicDownloadResponse;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class PublicShareController implements PublicSharesApi {
    private final ShareService shares;

    @Override
    public ResponseEntity<PublicDownloadResponse> createPublicDownload(String token) {
        ShareService.PublicDownload download = shares.createPublicDownload(token);
        return ResponseEntity.ok(new PublicDownloadResponse()
                .downloadUrl(download.downloadUrl())
                .expiresAt(download.expiresAt().atOffset(ZoneOffset.UTC)));
    }
}
