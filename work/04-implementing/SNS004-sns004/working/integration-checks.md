# SNS004 Integration Checkpoints

## Route Selection Test

- **Angular integration**: `AppComponent.viewMode` calls the tested `resolveViewMode(location.pathname, location.search)` and the template selects recipient, landing, or sender content from that result.
- **Recipient API**: Only recipient mode receives a `recipientToken`; existing `loadRecipientDownload()` remains the path to the current public-share API.
- **Sender/OIDC**: `/upload` and OIDC callbacks select sender content; existing `finishSignIn()` continues to process code/state and uses the current pathname for redirects.
- **SPA hosting**: `ops/Caddyfile` already uses `try_files {path} /index.html`, so direct `/upload` requests load the Angular shell.
- **Backend/storage**: No API or persistence changes; those integration points remain untouched.
- **Landing animation**: The sticky scene reads scroll position in a passive, requestAnimationFrame-throttled handler outside Angular change detection; progress is clamped and bound to lid rotation, interior light, and a small lift.
- **Reduced motion**: The CSS presents an open static box, hides the scroll cue, and the motion-preference listener pins reveal progress to its completed state.
- **CTA and recipient browser checks**: A local production-bundle preview with SPA fallback confirmed `/upload` renders the existing sender workspace and “Sign in” control. `/?share=legacy-token` renders the current recipient view. CTA destination is the same `/upload` route.
- **Visual review**: Reviewed the landing at the browser's phone-sized viewport and confirmed the intro and box no longer overlap; pressing Page Down progressively opens the lid. Focus and interactive link semantics are native.
- **Validation**: `npm test` passed (2 tests), `npm run build` passed, and `git diff --check` passed. `ng serve` is unavailable because the checkout lacks the Angular dev-server builder; the production bundle was reviewed through a local SPA-fallback preview instead.
