# Pipeline Test Requirements

## Smoke Tests

| Endpoint | Method | Scenario | Expected |
|---|---|---|---|
| `/api/v1/public/shares/{token}/download` | POST | Ready one-file share | 200 with a URL whose download response has `Content-Disposition: attachment; filename="{originalName}"`. |
| `/api/v1/public/shares/{token}/download` | POST | Ready multi-file share | 200 with a URL whose download response has `Content-Disposition: attachment; filename="snapsends3-share.zip"`. |

## Fuzz Tests

- **Reference Data**: Filename values containing quotes, CR, and LF characters.
- **Excluded Endpoints**: Upload and scan endpoints; SNS003 changes only delivery metadata.
- **Special Config**: Verify the sanitized filename cannot inject response headers.

## Integration Tests

| Scenario | Steps | Test Data | Expected |
|---|---|---|---|
| One-file recipient download | Create share, upload one PNG or PDF, finalize, wait until ready, request download URL, fetch URL. | Small file with known magic bytes and original filename. | Response is attachment, filename equals original name, bytes match the source file. |
| Multi-file recipient download | Create share, upload two files, finalize, wait until ready, request and fetch download URL. | Two small text files. | Response is attachment named `snapsends3-share.zip`; archive opens and contains both files. |
| Authorization regression | Request an expired, password-protected, and unready share. | Controlled share states. | Existing 410, 401, and 409 behavior remains unchanged. |
