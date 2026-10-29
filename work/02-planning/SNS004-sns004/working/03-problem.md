# Problem Capture

## Problem Statement

The existing SnapSendS3 web app opens directly into a functional sender workspace, but its presentation does not provide the polished, modern first impression requested by the product direction. Users need a focused, dedicated introduction to the file-upload service that uses a scroll-driven box reveal to guide attention to the upload action. The destination must remain the app's existing sign-in and sender workspace so that the current authentication and upload behavior are preserved.

## Stakeholders

- Senders: need to understand the service and quickly reach file upload.
- Recipients: must retain the current share-link experience without disruption.
- Product/design owner: approves the visual direction and interaction quality.
- Web engineering: implements and validates the landing page within the existing Angular app.

## Affected Components

- [ ] Manager
- [ ] Server
- [x] App (`apps/web`)
- [ ] Cross-cutting

## Initial Questions

- What URL/path should host the dedicated landing page, and should it become the app root while retaining the current sender workspace at a separate path?
- The request calls the current destination the “existing login page”; the app currently presents sign-in in the sender workspace and redirects to the configured OIDC provider. Confirm that the existing sender workspace/sign-in entry point is the intended destination.
- Should scroll progression use native page scroll with a sticky scene, or a bounded staged scroll region?
- Is an authored CSS 3D box acceptable, or is a supplied brand illustration/asset expected?
- How should existing share URLs containing `?share=...` behave when the landing page is introduced?

## Existing Context

- `apps/web/src/main.ts`: Angular standalone root component currently handles sender workspace, recipient share flow, OIDC sign-in, and upload interactions.
- `apps/web/src/styles.css`: global visual system and responsive/reduced-motion rules.
- `apps/web/package.json`: Angular 19 app; no routing library or animation dependency is currently declared.
- User request: dedicated dark box experience, scroll-linked opening, restrained gold/white light, upload CTA to existing login, accessibility and responsive support.
