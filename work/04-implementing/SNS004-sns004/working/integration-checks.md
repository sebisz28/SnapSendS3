# SNS004 Integration Checkpoints

## Route Selection Test

- **Angular integration**: `AppComponent.viewMode` calls the tested `resolveViewMode(location.pathname, location.search)` and the template selects recipient, landing, or sender content from that result.
- **Recipient API**: Only recipient mode receives a `recipientToken`; existing `loadRecipientDownload()` remains the path to the current public-share API.
- **Sender/OIDC**: `/upload` and OIDC callbacks select sender content; existing `finishSignIn()` continues to process code/state and uses the current pathname for redirects.
- **SPA hosting**: `ops/Caddyfile` already uses `try_files {path} /index.html`, so direct `/upload` requests load the Angular shell.
- **Backend/storage**: No API or persistence changes; those integration points remain untouched.
- **Validation**: Focused Node unit test and Angular production build passed. Direct browser path checks will be run after the landing structure and CTA are complete.
