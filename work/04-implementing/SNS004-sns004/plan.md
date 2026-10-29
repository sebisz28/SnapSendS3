# SNS004 — Modern Scroll-Driven Landing Experience

## Overview

The current SnapSendS3 web client opens directly into a functional sender workspace, but it lacks the polished first impression requested for the product. This task adds a dedicated scroll-driven landing experience with a refined white-and-blue box reveal, a colorful visual refresh for the `/upload` sender workspace, and a SnapSendS3 Keycloak theme. The landing CTA starts the existing OIDC sign-in directly and returns the signed-in user to `/upload`; existing authentication and recipient-share behavior remain intact.

The web client is a standalone Angular 19 application. It currently has no Angular Router and uses a single root component for the sender and recipient experiences. Caddy already falls back to `index.html` for application paths.

## Task Definition Reference

- [Task requirements](task.md)
- Stakeholders: senders, recipients, product/design owner, web engineering.
- Goals: focused modern landing page, responsive scroll reveal, easily found CTA, direct handoff to existing OIDC sign-in, cohesive Keycloak login appearance, preserved authentication and recipient links.
- Constraints: existing OIDC behavior must remain unchanged; no new animation/runtime dependency without need; support reduced motion and keyboard navigation.
- Non-goals: backend/storage changes, a new login flow or app-owned credential form, recipient workspace redesign, Apple product/brand imitation, unrelated functionality.

## References and Related Work

- [Approved external document sharing PRD](../../../explore/prds/external-document-sharing-prd.md), especially NFR-006 accessible flows.
- [External document sharing HLD](../../../explore/hlds/external-document-sharing-hld.md), Angular sender/recipient experience and future-route context.
- [ADR-001: modular monolith](../../../explore/decisions/external-document-sharing-adr-001-modular-monolith.md).
- [ADR-002: direct scoped R2 transfers](../../../explore/decisions/external-document-sharing-adr-002-direct-scoped-r2-transfers.md); upload/download boundary remains unchanged.
- [Repository and deployment notes](../../../docs/repositories.md).
- [Current Angular root component](../../../apps/web/src/main.ts), [styles](../../../apps/web/src/styles.css), [package manifest](../../../apps/web/package.json), [Angular config](../../../apps/web/angular.json), and [Caddy SPA fallback](../../../ops/Caddyfile).
- [Keycloak realm client config](../../../ops/keycloak/snapsends3-realm.json) permits origin-relative redirect paths with a wildcard.
- Related capability context: [SNS001 plan](../../04-implementing/SNS001-epic-formation-for-snapsends3-external-document-sh/plan.md). [SNS003](../../06-completed/SNS003-fix-invalid-single-file-download/summary.md) is complete and has no direct implementation dependency.

## Technical Decisions

### 1. Preserve existing paths and query behavior

Use path/query-based view selection in the existing root component without adding Angular Router:

- `/` with no `share` query or OIDC callback renders the new landing page.
- `/upload` renders the existing sender workspace with refreshed presentation and unchanged upload behavior.
- The signed-out landing CTA starts the existing PKCE authorization request with `/upload` as its callback. A valid session goes straight to `/upload`.
- Keycloak renders its existing credential and recovery forms with the `snapsends3` login theme.
- A `share` query parameter takes precedence on either path, preserving legacy `/?share=...` URLs and allowing new `/upload?share=...` links.
- OIDC callback values continue through existing `finishSignIn()` logic. Sign-in initiated from `/upload` keeps the current pathname-based redirect URI and returns to `/upload`.
- The “Upload your files” control is a semantic button: signed-in users go to `/upload`; signed-out users start PKCE sign-in with `/upload` as callback.

The app previously had no separate login route: its sign-in action lived in the sender workspace and then redirected to Keycloak. The clarified behavior starts the same PKCE flow from the landing CTA, requests `/upload` as the redirect URI, and styles the real Keycloak login form. Direct visits to `/upload` work through Caddy's existing SPA fallback, and the checked-in Keycloak client configuration allows origin-relative redirect paths with a wildcard. Do not add app-side credential handling or alter OIDC validation or token behavior.

### 2. Tie the visual reveal to scroll

Use a sticky scene inside a longer landing page. Build the box faces from semantic-safe decorative HTML/CSS elements, with subtle gradients, perspective, highlights, and restrained soft-white/warm-gold interior light. Compute normalized scene progress from browser scroll position, clamp it to `[0, 1]`, and schedule at most one visual update per animation frame. Apply progress through CSS custom properties to the lid rotation, light intensity, and subtle elevation. Do not route every frame through Angular template state or change detection.

Keep the CTA in normal page flow after the scene so it remains reachable independently of animation timing. On `prefers-reduced-motion: reduce`, skip scroll-driven transforms and present a static or brief fade state. Ensure the CTA remains usable if the scroll enhancement does not initialize.

### 3. Keep dependencies and business logic unchanged

Use existing Angular, browser scroll APIs, CSS transforms, and `matchMedia`; add no runtime dependencies. Do not change API, persistence, private storage, share lifecycle, OIDC PKCE, token storage, or upload behavior. Scope the charcoal landing style and colorful sender style to their own views; leave the recipient presentation unchanged.

### 4. Style the existing Keycloak login page

Extend Keycloak 26's `keycloak.v2` login theme with a local stylesheet, keeping its inherited templates, password recovery, error handling, semantics, and authentication endpoints. Mount the theme read-only in Compose and select it for the imported `snapsends3` realm. Startup import skips an existing realm, so document the safe Realm Settings → Themes activation for existing installations; do not delete or reimport user data to activate a visual theme.

## Architecture

```mermaid
flowchart LR
  User[Sender / Recipient] --> Browser[Browser URL and scroll state]
  Browser --> Caddy[Caddy HTTPS and SPA fallback]
  subgraph App[Task scope: apps/web Angular client]
    Dispatch[Path and query view selection]
    Landing[Landing: CSS box and scroll scene]
    Sender[Existing sender workspace and OIDC PKCE]
    Recipient[Existing recipient share view]
    Dispatch -->|/ no share or callback| Landing
    Dispatch -->|/upload| Sender
    Dispatch -->|share query on any path| Recipient
    Landing -->|Signed in: /upload| Sender
    Landing -->|Signed out: PKCE redirect URI /upload| Keycloak
  end
  Caddy --> Dispatch
  Sender <-->|Existing OIDC auth code + PKCE| Keycloak[Keycloak]
  Sender -->|Existing API calls| API[Spring API]
  Recipient -->|Existing API calls| API
  API --> Storage[Private object storage]
  Browser -->|Scroll progress| Landing
```

No API, database, or storage changes are required. Caddy's configured `try_files {path} /index.html` fallback already supports `/upload`.

## Component Changes

### `apps/web/src/main.ts`

- Add path/query-aware rendering: landing, sender workspace, recipient view.
- Give `share` precedence so existing and new recipient URLs resolve on either path.
- Add the landing content and `/upload` CTA.
- Refresh the sender workspace presentation while preserving its existing controls and actions.
- Start the existing `signIn()` flow from the CTA with a same-origin `/upload` callback; route signed-in users directly to `/upload`.
- Connect scroll progress to a CSS property using a passive listener and `requestAnimationFrame`; ensure listener and media-query subscriptions are cleaned up with component lifecycle.
- Preserve existing `signIn()`, `finishSignIn()`, recipient loading, sender upload, and share URL handling.

### `apps/web/src/styles.css`

- Add isolated dark landing styles, responsive box faces, perspective, warm interior glow, scroll reveal states, CTA styles, and clear hover/active/focus states.
- Add a scoped colorful sender workspace with responsive hero artwork, glass-like surfaces, and polished form/control states.
- Add a responsive colorful sender workspace layout with a decorative delivery visual and refined upload card styling.
- Respect reduced-motion preference and preserve the existing sender/recipient appearance.

### `ops/keycloak/themes/snapsends3/login`
- Extend the Keycloak 26 `keycloak.v2` login theme using CSS only; preserve inherited authentication and recovery templates.
- Mount the theme read-only in Compose and select it in the imported realm configuration.

### `ops/keycloak/snapsends3-realm.json` and `compose.yaml`
- Select the `snapsends3` login theme for fresh realm imports and mount the custom theme into Keycloak.
- Existing realms skip startup import. Enable the theme from Realm Settings → Themes; do not overwrite realm data.

### `apps/web/src/index.html`

- Update title/description only if needed to identify the landing experience clearly.

## Test Inventory

### Build and route smoke checks

| Scenario | Expected result |
|---|---|
| `GET /` | App shell loads and landing page is rendered. |
| `GET /upload` | Caddy SPA fallback serves app shell and existing sender workspace renders. |
| `GET /?share=<token>` | Existing recipient view renders; landing does not override the share route. |
| `GET /upload?share=<token>` | Existing recipient view renders on the upload path. |
| OIDC callback at `/upload?code=...&state=...` | Existing PKCE callback completes and user returns to sender workspace; configured Keycloak redirect wildcard permits the path. |

### Browser validation

- Activate CTA with pointer and keyboard; confirm signed-out users reach the Keycloak login form and return to `/upload`, while signed-in users go directly to `/upload`.
- Scroll through start, midpoint, and end on mobile and desktop; verify lid and glow correspond to scroll position.
- Enable reduced motion; verify no scroll-driven transform and CTA remains immediately usable.
- Validate touch, wheel, Page Down, short viewport, zoom, keyboard focus, screen-reader semantics, contrast, and absence of horizontal overflow.
- Verify old root share links, newly generated `/upload?share=...` links, sender sign-in, and upload behavior.
- Verify the Keycloak theme renders and existing-realm activation leaves user data untouched.
- Review the `/upload` sender layout at desktop and mobile widths; confirm file selection, optional password, and share controls remain present and usable.
- Confirm artwork is decorative to assistive technology, primary heading and CTA are meaningful, and important information is not encoded only in color or animation.

### Harness limitation

`npm test` uses Node's built-in runner for route selection, redirect URI, scroll progress, Keycloak theme wiring, box artwork, and sender workspace structure. No browser E2E runner is present; verify the real Keycloak login page and callback manually. Run `npm run build` and complete route, visual, accessibility, and authentication walkthroughs.

## Acceptance Criteria

1. `/` without recipient/auth callback parameters displays a dedicated charcoal-to-black landing page with centered white-and-blue box.
2. Scrolling progressively opens the box and reveals restrained white and warm-gold light, mapped to scroll position and usable at mobile and desktop widths.
3. The “Upload your files” action starts the existing PKCE sign-in directly, or routes a signed-in user to `/upload`; the callback returns the user to the sender workspace.
4. Keycloak's credential and recovery forms use the SnapSendS3 theme and retain their existing validation and authentication behavior.
5. `/upload` uses a colorful, polished sender layout while preserving the existing file, password, sign-in, and share workflows.
6. Existing `/?share=...` links and new `/upload?share=...` links both render the current recipient experience.
7. If scroll enhancement fails or reduced motion is preferred, the page remains usable and CTA remains reachable; reduced-motion users see a static or fade-based presentation.
8. Keyboard focus is visible; control semantics, contrast, and decorative artwork treatment are accessible; hover and pressed states give clear feedback.
9. Layout works at mobile, tablet, and desktop sizes without horizontal overflow or inaccessible CTA.
10. Existing upload, share, recipient API, and download behavior is unchanged.

## Risks and Dependencies

| Risk | Likelihood / Impact | Mitigation |
|---|---|---|
| Root view change breaks old recipient links | Medium / High | Give `share` query precedence independent of path; test existing root and new upload-path share URLs. |
| Scroll effect stutters on mobile | Medium / Medium | Limit to one rAF update; avoid Angular change detection; test touch and varied viewport sizes. |
| CTA is hidden or awkward on short/zoomed viewports | Medium / Medium | Keep CTA in normal document flow and independent of animation completion. |
| Automated tests cannot run via current `npm test` script | High / Medium | Production build and explicit browser walkthrough; scope a harness separately if needed. |

Blocking dependencies: none identified. No backend, storage, or data-model dependencies.

## Implementation Strategy

### Phase 1: Route and layout foundation

1. Add route/query view selection for `/`, `/upload`, and `share` precedence.
2. Add landing structure and scoped charcoal styling; refresh the sender workspace presentation while preserving its workflows and leaving the recipient view unchanged.
3. Verify direct `/upload`, old root share links, new upload-path share links, and OIDC callback handling.

### Phase 2: Core box reveal and CTA

1. Build CSS box faces, perspective, shine, and interior light.
2. Implement clamped scroll progress with passive listener and rAF updates.
3. Start the existing PKCE flow from the landing CTA and request `/upload` as callback; route signed-in users directly to the sender workspace.
4. Add reduced-motion fallback and accessible semantics.

### Phase 3: Keycloak theme, integration, and polish

1. Extend Keycloak's `keycloak.v2` theme with CSS while keeping inherited forms and validation.
2. Mount the theme read-only in Compose, select it for fresh imports, and document existing-realm activation.
3. Validate responsive layouts, reduced motion, OIDC callback to `/upload`, and recipient URL flows.
4. Run production build and record manual browser/accessibility verification.

## Scope Tags

- Complexity: moderate
- Component: app
- Type: enhancement
- Priority: medium (no urgency supplied)
- Risk: medium

## Open Questions

- The credential page is served by Keycloak. The clarified CTA starts OIDC directly and the custom theme styles that provider-owned page.
- The checked-in Keycloak realm configuration allows origin-relative callback paths via a wildcard; separately managed deployments should retain equivalent redirect coverage.
