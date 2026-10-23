# Repositories

## SnapSendS3

**Status:** Application scaffold lives in this warehouse under `apps/api` and `apps/web`.

SnapSendS3 is the dava.flow web application for warehouse operations and secure external document sharing. It will use Java 21 with Spring Boot, an Angular/TypeScript frontend, PostgreSQL, and private Cloudflare R2 object storage via the S3-compatible API.

The API uses Maven (`apps/api/pom.xml`) and the Angular frontend uses npm (`apps/web/package.json`). `docker compose up --build` starts PostgreSQL, LocalStack S3, ClamAV, Keycloak, the API, and a Caddy HTTPS edge at `https://localhost:8443`. The imported local Keycloak demo Sender is `sender` / `sender`; its access token contains the `dava.sender` role.

## Public deployment

The local address cannot be shared outside the machine running Compose. To publish the service, point a DNS A/AAAA record for a public hostname at the server, copy `.env.public.example` to `.env`, and replace every placeholder with real values. Configure a Cloudflare R2 or S3 bucket and credentials; `SNAPSENDS3_R2_PUBLIC_ENDPOINT` must be the same public origin as the browser URL so signed downloads travel through Caddy.

Run the public stack on a server with inbound TCP port 443 open:

```powershell
docker compose -f compose.yaml -f compose.public.yaml up --build -d
```

Caddy obtains a publicly trusted certificate for `SNAPSENDS3_PUBLIC_HOST`. Keycloak imports the public redirect URL from `SNAPSENDS3_PUBLIC_ORIGIN` when its database is first created. After deployment, share links use `https://share.example.com/?share=...` automatically because the frontend builds them from the public browser origin.

### Local HTTPS trust

Caddy uses its local development certificate. After the stack has started, trust its root certificate in your Windows user certificate store to remove the browser's **Not secure** indicator:

```powershell
$certificate = Join-Path $env:TEMP 'snapsends3-local-root.crt'
podman cp "$(docker compose ps -q caddy):/data/caddy/pki/authorities/local/root.crt" $certificate
certutil -user -addstore Root $certificate
```

Restart the browser after running the command.
