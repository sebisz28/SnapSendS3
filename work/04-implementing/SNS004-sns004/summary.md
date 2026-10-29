# SNS004 Implementation Summary

## Metadata

**Task ID:** SNS004  
**Title:** sns004  
**Implemented By:** Sebastian.Szekely@endava.com + Codex  
**Date:** 2026-10-29  
**Status:** Follow-up implementation committed and pushed; PR creation is pending GitHub app reauthentication. Task remains in `04-implementing`.

## Implementation

The landing page's **Upload your files** action now starts the existing authorization-code + PKCE sign-in directly. Its callback is the same-origin `/upload` route, which returns the signed-in user to the existing sender workspace. Users with an active token continue directly to `/upload`. Recipient URL precedence and existing OIDC validation/token handling remain intact.

Added a SnapSendS3 Keycloak 26 login theme extending `keycloak.v2`. It uses a charcoal backdrop, restrained blue controls, warm focus highlights, refined spacing, and responsive/reduced-motion styles while inheriting Keycloak's sign-in and recovery templates. The theme is mounted read-only and selected for fresh realm imports. Existing realms skip startup imports; `ops/keycloak/README.md` documents safe activation through Realm Settings → Themes without overwriting realm data.

The box artwork now keeps its blue ribbon on the exterior only. The white and warm-gold interior glow is clipped to the box opening so it does not spill across the faces. The previously merged landing experience retains its responsive scroll-driven reveal, reduced-motion handling, and accessible CTA styling.

## Changed Files

- `apps/web/src/main.ts`, `apps/web/src/landing-state.mjs`, and `apps/web/src/landing-state.d.mts` — direct upload sign-in callback handling.
- `apps/web/src/styles.css` — CTA loading, hover, pressed, and disabled feedback.
- `apps/web/test/landing-state.test.mjs`, `apps/web/test/keycloak-theme.test.mjs`, and `apps/web/test/box-artwork.test.mjs` — callback, Keycloak wiring, and interior artwork coverage.
- `ops/keycloak/themes/snapsends3/login/` — custom Keycloak theme and styles.
- `ops/keycloak/snapsends3-realm.json`, `compose.yaml`, and `ops/keycloak/README.md` — fresh import selection, read-only theme mount, and existing-realm activation steps.
- SNS004 task and planning notes — clarified direct-login behavior, updated implementation inventory, integration evidence, and this summary.

## Validation

- `npm test` — passed, 5 tests.
- `npm run build` — passed; Angular production bundle generated.
- `docker compose config --quiet` — passed.
- `git diff --check` — passed.
- Isolated Keycloak 26.0.8 import — passed; login HTML linked the SnapSendS3 stylesheet and preserved the username, password, show-password, and Sign In controls. The smoke container was removed; no credentials were submitted.

## Verification Gaps

- No real credential submission or complete OIDC round trip was performed.
- Existing Keycloak realms need the documented manual theme selection because Keycloak skips an already-imported realm.
- Password-recovery page styling, keyboard-only CTA activation, reduced-motion emulation, and the full mobile/desktop accessibility review remain to verify.
- The pre-existing local change in `apps/web/angular.json` was preserved and is not part of this task change.

## References

- [Task definition](task.md)
- [Implementation plan](plan.md)
- [Integration checks](working/integration-checks.md)
- [Implementation inventory](working/implementation-inventory.md)
- [Merged landing experience PR #5](https://github.com/sebisz28/SnapSendS3/pull/5)
- [Create the follow-up PR](https://github.com/sebisz28/SnapSendS3/pull/new/impl/sns004-branded-login)
