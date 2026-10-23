# Goals and Constraints

## Measurable Goals

- [ ] A one-file share downloads one valid file with the uploaded filename; verify its content signature matches the uploaded file and Windows opens it normally.
- [ ] A multi-file share downloads a valid `snapsends3-share.zip`; verify the archive opens and contains every uploaded file.
- [ ] Recipient responses use `Content-Disposition: attachment`; verify the browser saves the file rather than rendering it inline.

## Constraints

- **Timeline**: No deadline was provided; address the regression in SNS003.
- **Resources**: Use the existing Java, Angular, Caddy, LocalStack, and Compose components.
- **Technical**: Keep the local public origin as `https://localhost:8443`; do not require a public domain or external storage account.
- **Compliance**: Preserve scan-before-download, expiry, recipient password handling, and private-object storage.

## Non-Goals

- Public deployment: DNS, Cloudflare R2, and production TLS are outside this local download fix.
- Archive format changes: ZIP remains the format for multi-file shares.
- File-content transformation: original one-file share content remains unmodified.

## Assumptions

| Assumption | Risk | Validation |
|---|---|---|
| The selected object-store SDK supports a signed `response-content-disposition` override. | Medium | Unit test URL generation and inspect the live response header. |
| LocalStack may not apply the response override itself. | Medium | Verify Caddy forwards the signed query value as `Content-Disposition`. |
| The database stores the original upload name for each share file. | Low | Inspect `ShareFile` and service tests. |
