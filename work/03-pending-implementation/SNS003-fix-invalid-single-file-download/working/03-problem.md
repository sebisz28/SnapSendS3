# Problem Capture

## Problem Statement

For a share containing one file, the recipient download endpoint returns the original object bytes. A prior local download was labelled as `snapsends3-share.zip`, so Windows treated a source file as an archive and rejected it. The codebase now contains the intended name-selection and attachment-header behavior, but it lacks complete regression and browser-visible verification. The desired result is evidence that a one-file share downloads the original file and a multi-file share remains a valid ZIP in the local `https://localhost:8443` flow.

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
