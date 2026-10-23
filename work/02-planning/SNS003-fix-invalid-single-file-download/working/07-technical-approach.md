# Technical Approach

## High-Level Strategy

Keep the existing direct, signed object-storage download model. Verify the existing filename selection, presigned attachment disposition, and Caddy response-header mapping with targeted automated and local integration tests.

## Architecture Decisions

### Preserve the one-file source object

- **Choice**: A one-file share remains the uploaded object; it is not wrapped in a ZIP.
- **Rationale**: This matches the Share completion lifecycle and avoids needless byte transformation.
- **Alternatives Considered**: Always create a ZIP. Rejected because it changes the documented behavior and adds archive work for one file.

### Resolve the delivery filename in the service layer

- **Choice**: `ShareService.createPublicDownload` obtains `ShareFile` records after authorizing a ready share. It passes the original filename when exactly one record exists, otherwise `snapsends3-share.zip`.
- **Rationale**: The service has both the ready-object key and share shape, keeping the storage adapter unaware of domain rules.

### Enforce attachment behavior through the signed URL and local proxy

- **Choice**: `R2ObjectStorage.createDownloadUrl` signs `response-content-disposition=attachment; filename=...`, with CR/LF and quote characters neutralized. Caddy maps that signed query field to the response `Content-Disposition` header for `/snapsends3/*`.
- **Rationale**: R2 honors the signed S3 response override; LocalStack may not, so the proxy preserves local parity without exposing storage directly.
- **Alternatives Considered**: API byte proxy. Rejected by ADR-002.

## Component Changes

### API / storage

- [ ] Add a multi-file ZIP-name case to `ShareServiceTest` alongside the existing one-file case.
- [ ] Add `R2ObjectStorageTest` to assert attachment disposition and CR/LF/quote sanitization in the signed GET request.
- [ ] Verify the existing signed URL query construction and Caddy header injection through the local stack.

### Caddy

- [ ] Verify the existing `/snapsends3/*` response-header mapping for `response-content-disposition`.
- [ ] Verify the browser-visible endpoint returns the intended attachment filename.

### Web application

- [ ] Keep the recipient link as a normal anchor to the short-lived URL; response headers govern saving and the downloaded filename.

## Data Model Changes

None. `ShareFile.originalName`, `Share.archiveObjectKey`, and scan state already contain the required data.

## Integration Points

`PublicShareController` requests a public download from `ShareService`; the service authorizes the recipient and asks `ObjectStorage` for a signed URL; Caddy proxies the browser request to LocalStack while restoring the signed attachment header.

## Configuration

No new configuration. Local verification runs through `https://localhost:8443` and the existing `SNAPSENDS3_R2_PUBLIC_ENDPOINT` default.

## Dev Hints

- Run `mvn test` from `apps/api` for service regression tests.
- Run `docker compose up --build -d` after API or Caddy changes.
- Use a fresh one-file share after deployment; older links may have expired or have been downloaded with an earlier response header.
