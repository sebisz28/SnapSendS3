# Technical Approach

## High-Level Strategy

Keep the work in the existing Angular 19 client. Add a dedicated public landing experience at `/`, keep the sender workspace at `/upload`, and continue rendering recipient shares when a `share` query parameter is present on either the legacy root URL or the upload URL. The signed-out landing CTA starts the existing OIDC authorization-code + PKCE flow directly with `/upload` as its redirect URI; a signed-in user goes directly to `/upload`. Theme the existing Keycloak login UI without adding app-side credential handling or changing callback/token logic.

Render the box with HTML/CSS faces and CSS 3D transforms, with a warm, restrained interior glow. Use a sticky scene within a longer landing-page scroll region. Derive one normalized progress value from the scene's scroll position, update it at most once per animation frame, and apply it to lid rotation, interior light, and subtle elevation. Keep the content and CTA in normal document flow after the scene. For reduced-motion users, show a static/fade state and avoid scroll-driven movement.

## Architecture Decisions

### Route and entry-point compatibility
- **Choice**: Use small path/query-based view selection in the current root component, without adding Angular Router. `/` without share/auth callback parameters renders the landing page; `/upload` renders the sender workspace; `?share=...` takes precedence and renders the recipient view regardless of pathname. The landing CTA starts sign-in with `/upload` as callback, or navigates directly there for an existing session.
- **Rationale**: The app is currently a single standalone component with no Router dependency. Caddy's SPA fallback serves `index.html` for unknown paths. The explicit upload path gives the CTA a separate destination while retaining previously issued root share links.
- **Alternatives considered**: Add Angular Router (unnecessary runtime dependency and route migration); replace the current root with the box page without route dispatch (would break sender/sign-in and recipient share links).

### Authentication behavior and login presentation
- **Choice**: Reuse `signIn()` / `finishSignIn()` and existing PKCE/state/token behavior. Allow the landing CTA to request only the internal `/upload` callback. Extend Keycloak 26's `keycloak.v2` theme with CSS; keep Keycloak's inherited login/recovery forms and validation.
- **Rationale**: The actual credential page is hosted by Keycloak. This sends signed-out users directly to the real login form and returns them to upload without creating a parallel auth surface.
- **Alternatives considered**: Keep the intermediate `/upload` sign-in button (extra step); build an app-owned login form (duplicates the identity boundary); replace Keycloak templates (more upgrade and accessibility risk than CSS theme inheritance).

### Scroll animation
- **Choice**: Use sticky positioning and a passive scroll listener scheduled through `requestAnimationFrame` to update a CSS custom progress value on the landing scene. Clamp progress to [0, 1]. Do not route scroll frames through Angular template state or change detection.
- **Rationale**: Ties the lid/light directly to scrolling, uses widely supported primitives, and avoids introducing an animation library.
- **Alternatives considered**: CSS scroll timelines alone (less consistent support); time-based entrance animation (not connected to scroll); per-frame Angular state updates (unnecessary change-detection work).

### Visual asset
- **Choice**: Build the box from layered CSS elements using perspective, gradients, soft highlights, and a restrained white/warm-gold glow; keep the effect decorative and hidden from assistive technology.
- **Rationale**: No brand asset exists in the repo and adding an image-generation or 3D dependency is unnecessary for the requested geometric visual.
- **Alternatives considered**: External image/asset (adds asset sourcing and scaling constraints); WebGL/3D framework (disproportionate runtime and accessibility complexity).

### Reduced motion and accessibility
- **Choice**: Detect `prefers-reduced-motion` with `matchMedia`, render a static/opened or short fade presentation, and skip scroll-driven transform updates. Keep the CTA in the document flow after the scene. Use a semantic button to start OIDC, visible keyboard focus, contrast-safe foregrounds, and decorative aria-hidden artwork.
- **Rationale**: Makes the landing flow usable without motion and keeps navigation independent of animation completion.

## Component Changes

### `apps/web/src/main.ts`
- [ ] Select landing, sender, or recipient content based on `location.pathname` and `share` query value, preserving OAuth callback handling.
- [ ] Add landing-page semantic content and CTA that starts sign-in with `/upload` callback.
- [ ] Connect sticky scene progress to CSS variables without triggering Angular change detection each frame; register/unregister listeners with component lifecycle.
- [ ] Preserve current file upload, share-link, recipient, token, and OIDC methods.

### `apps/web/src/styles.css`
- [ ] Add dark landing-page palette, responsive layout, CSS box geometry, perspective, reveal/light states, and reduced-motion behavior.
- [ ] Add polished hover, focus-visible, active/pressed, and disabled states for applicable controls.
- [ ] Ensure the sender and recipient screens retain their existing light theme and remain unchanged.

### `ops/keycloak/themes/snapsends3/login`
- [ ] Extend Keycloak's `keycloak.v2` theme with responsive dark styling while inheriting its credential, recovery, and error templates.

### `apps/web/src/index.html`
- [ ] Update page title/description only if required to identify the new landing experience accessibly and clearly.

## Data Model Changes

None. No API or persisted state changes.

## Integration Points

- Angular client routes by pathname/query in the browser.
- Signed-out CTA starts the existing Keycloak authorization endpoint with `/upload` as callback; the existing client redirect wildcard permits it.
- Keycloak renders the `snapsends3` theme. The realm JSON selects it for new imports; existing realms need the documented Admin Console selection because startup import skips existing realms.
- A successful PKCE callback at `/upload` returns to the sender workspace through Caddy's SPA fallback.
- Existing recipient URLs `/?share=...` continue to render the recipient experience; newly created shares from `/upload` use `/upload?share=...` and resolve through the same query dispatch.

## Configuration

No new runtime dependencies. Add a read-only Keycloak theme bind mount and select the theme in realm import settings. Existing realm activation is documented without reimporting realm data.

## Dev Hints

- Web app: `apps/web` (Angular 19 standalone component).
- Build script: `npm run build`.
- `package.json` declares `npm test`, but `angular.json` currently has no test target; test execution setup must be addressed or a documented manual/browser validation used.
- SPA route fallback is configured in `ops/Caddyfile` (`try_files {path} /index.html`).
