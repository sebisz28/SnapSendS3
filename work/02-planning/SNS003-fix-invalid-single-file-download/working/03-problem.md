# Problem Capture

## Problem Statement

For a share containing one file, the recipient download endpoint returns the original object bytes. The current client and proxy label that response as `snapsends3-share.zip`, so Windows treats a PNG, PDF, or other original file as a ZIP archive and reports that the archive is invalid. The desired result is for a single-file share to download the original file using its original filename and media type; multi-file shares remain a valid ZIP archive. This prevents recipients from receiving unusable downloads in the local `https://localhost:8443` flow.

## Stakeholders

- Sender: uploads documents and creates the share link.
- Recipient: downloads the approved document through the public share link.
- SnapSendS3 operator: runs and verifies the local Compose stack.

## Affected Components

- [x] Server: share download URL creation and storage response headers.
- [x] App: recipient download link behavior.
- [x] Cross-cutting: Caddy proxy forwarding of signed-response headers.

## Initial Questions

- [ ] Does the signed object-store URL preserve the original filename for a single-file share?
- [ ] Does Caddy forward the signed `Content-Disposition` value without overriding it?
- [ ] Does a multi-file share still produce a valid ZIP named `snapsends3-share.zip`?
- [ ] Does the browser save the response instead of rendering it inline?

## Existing Context

- User report: Windows rejected `snapsends3-share.zip` because its bytes were an uploaded image rather than a ZIP archive.
- `ShareCompletionService` keeps a single uploaded object unarchived and archives only multi-file shares.
- `ShareService` and `R2ObjectStorage` generate the recipient download URL.
- `ops/Caddyfile` proxies browser-visible storage URLs through the local host.
