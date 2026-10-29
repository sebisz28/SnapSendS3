# Technical Approach

## High-Level Strategy

Keep the work in the existing Angular 19 client. Add a dedicated public landing experience at `/`, keep the existing sender workspace and sign-in entry at `/upload`, and continue rendering recipient shares when a `share` query parameter is present on either the legacy root URL or the new upload URL. Keep the existing OIDC code intact and ensure callback query parameters still reach its current completion logic.

Render the box with HTML/CSS faces and CSS 3D transforms, with a warm, restrained interior glow. Use a sticky scene within a longer landing-page scroll region. Derive one normalized progress value from the scene's scroll position, update it at most once per animation frame, and apply it to lid rotation, interior light, and subtle elevation. Keep the content and CTA in normal document flow after the scene. For reduced-motion users, show a static/fade state and avoid scroll-driven movement.

## Architecture Decisions

### Route and entry-point compatibility
- **Choice**: Use small path/query-based view selection in the current root component, without adding Angular Router. `/` without share/auth callback parameters renders the new landing page; `/upload` renders the existing sender workspace; `?share=...` takes precedence and renders the current recipient view regardless of pathname. The CTA navigates to `/upload`.
- **Rationale**: The app is currently a single standalone component with no Router dependency. Caddy's SPA fallback serves `index.html` for unknown paths. The explicit upload path gives the CTA a separate destination while retaining previously issued root share links.
- **Alternatives considered**: Add Angular Router (unnecessary runtime dependency and route migration); replace the current root with the box page without route dispatch (would break sender/sign-in and recipient share links).

### Authentication behavior
- **Choice**: Reuse existing sender workspace and `signIn()` / `finishSignIn()` behavior unchanged. Keep redirect URI as the current pathname; initiate and complete sign-in at `/upload`.
- **Rationale**: Meets the navigation request without introducing an authentication flow or altering PKCE/session behavior.
- **Alternatives considered**: Add a login screen/route (explicitly out of scope); make the CTA start OIDC directly (would change current interaction behavior).

### Scroll animation
- **Choice**: Use sticky positioning and a passive scroll listener scheduled through `requestAnimationFrame` to update a CSS custom progress value on the landing scene. Clamp progress to [0, 1]. Do not route scroll frames through Angular template state or change detection.
- **Rationale**: Ties the lid/light directly to scrolling, uses widely supported primitives, and avoids introducing an animation library.
- **Alternatives considered**: CSS scroll timelines alone (less consistent support); time-based entrance animation (not connected to scroll); per-frame Angular state updates (unnecessary change-detection work).

### Visual asset
- **Choice**: Build the box from layered CSS elements using perspective, gradients, soft highlights, and a restrained white/warm-gold glow; keep the effect decorative and hidden from assistive technology.
- **Rationale**: No brand asset exists in the repo and adding an image-generation or 3D dependency is unnecessary for the requested geometric visual.
- **Alternatives considered**: External image/asset (adds asset sourcing and scaling constraints); WebGL/3D framework (disproportionate runtime and accessibility complexity).

### Reduced motion and accessibility
- **Choice**: Detect `prefers-reduced-motion` with `matchMedia`, render a static/opened or short fade presentation, and skip scroll-driven transform updates. Keep the CTA in the document flow after the scene. Use semantic headings, a real link for route navigation, visible keyboard focus, contrast-safe foregrounds, and decorative aria-hidden artwork.
- **Rationale**: Makes the landing flow usable without motion and keeps navigation independent of animation completion.

## Component Changes

### `apps/web/src/main.ts`
- [ ] Select landing, sender, or recipient content based on `location.pathname` and `share` query value, preserving OAuth callback handling.
- [ ] Add landing-page semantic content and CTA to `/upload`.
- [ ] Connect sticky scene progress to CSS variables without triggering Angular change detection each frame; register/unregister listeners with component lifecycle.
- [ ] Preserve current file upload, share-link, recipient, token, and OIDC methods.

### `apps/web/src/styles.css`
- [ ] Add dark landing-page palette, responsive layout, CSS box geometry, perspective, reveal/light states, and reduced-motion behavior.
- [ ] Add polished hover, focus-visible, active/pressed, and disabled states for applicable controls.
- [ ] Ensure the sender and recipient screens retain their existing light theme and remain unchanged.

### `apps/web/src/index.html`
- [ ] Update page title/description only if required to identify the new landing experience accessibly and clearly.

## Data Model Changes

None. No API or persisted state changes.

## Integration Points

- Angular client routes by pathname/query in the browser.
- CTA navigates to `/upload`, served through existing Caddy `try_files {path} /index.html` fallback.
- Existing sender sign-in continues using the current OIDC provider and pathname-based redirect URI.
- Existing recipient URLs `/?share=...` continue to render the recipient experience; newly created shares from `/upload` use `/upload?share=...` and resolve through the same query dispatch.

## Configuration

No new runtime dependencies or configuration. Use the browser's reduced-motion preference and viewport/scroll APIs.

## Dev Hints

- Web app: `apps/web` (Angular 19 standalone component).
- Build script: `npm run build`.
- `package.json` declares `npm test`, but `angular.json` currently has no test target; test execution setup must be addressed or a documented manual/browser validation used.
- SPA route fallback is configured in `ops/Caddyfile` (`try_files {path} /index.html`).
