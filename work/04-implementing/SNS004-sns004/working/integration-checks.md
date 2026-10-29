# SNS004 Integration Checkpoints

## Route Selection Test

- **Angular integration**: `AppComponent.viewMode` calls the tested `resolveViewMode(location.pathname, location.search)` and the template selects recipient, landing, or sender content from that result.
- **Recipient API**: Only recipient mode receives a `recipientToken`; existing `loadRecipientDownload()` remains the path to the current public-share API.
- **Sender/OIDC**: `/upload` and OIDC callbacks select sender content; existing `finishSignIn()` continues to process code/state. The landing CTA requests `/upload` as its same-origin PKCE callback; sign-in initiated inside `/upload` retains the existing pathname callback.
- **SPA hosting**: `ops/Caddyfile` already uses `try_files {path} /index.html`, so direct `/upload` requests load the Angular shell.
- **Backend/storage**: No API or persistence changes; those integration points remain untouched.
- **Landing animation**: The sticky scene reads scroll position in a passive, requestAnimationFrame-throttled handler outside Angular change detection; progress is clamped and bound to lid rotation, interior light, and a small lift.
- **Reduced motion**: The CSS presents an open static box, hides the scroll cue, and the motion-preference listener pins reveal progress to its completed state.
- **CTA and recipient browser checks**: A local production-bundle preview with SPA fallback confirmed `/upload` renders the existing sender workspace and “Sign in” control. `/?share=legacy-token` renders the current recipient view. The landing CTA starts the existing PKCE flow with `/upload` as callback (unit-tested; browser auth round trip not exercised).
- **Keycloak theme**: `snapsends3` extends `keycloak.v2`, is mounted read-only, and is selected for fresh realm imports. Existing realms require the documented Admin Console theme selection because startup import skips realms already present.
- **Sender workspace presentation**: `/upload` keeps the existing file input, optional password field, sign-in state, and share creation controls inside a responsive, view-scoped colorful layout; recipient styling remains separate.
- **Fresh-realm Keycloak smoke check**: An isolated Keycloak 26.0.8 import successfully loaded `snapsends3`; the authorization page linked `login/snapsends3/css/snapsends3.css` and exposed the expected username, password, show-password, and Sign In controls. The temporary smoke container was removed after verification. No credentials were submitted.
- **Visual review**: Reviewed the landing at the browser's phone-sized viewport and confirmed the intro and box no longer overlap; pressing Page Down progressively opens the lid. Focus and interactive link semantics are native.
- **Validation**: `npm test` passed (2 tests), `npm run build` passed, and `git diff --check` passed. `ng serve` is unavailable because the checkout lacks the Angular dev-server builder; the production bundle was reviewed through a local SPA-fallback preview instead.
