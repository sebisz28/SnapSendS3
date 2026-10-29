# Pipeline Test Requirements

## Smoke Tests

No API endpoints are added or modified.

| Endpoint | Method | Scenario | Expected |
|----------|--------|----------|----------|
| `/` | GET | Open landing page without query parameters | Angular shell returns successfully and shows the box experience. |
| `/upload` | GET | Open sender entry directly or use landing CTA | SPA fallback returns Angular shell and existing sender/sign-in workspace renders. |
| `/?share=<existing-token>` | GET | Open a previously issued recipient URL | Existing recipient flow renders and continues to use its current API behavior. |
| `/upload?share=<token>` | GET | Open a newly created recipient URL | Existing recipient flow renders on the upload path. |
| `/upload?code=...&state=...` | GET | Return from OIDC sign-in | Existing PKCE callback behavior completes and returns to `/upload`. |

## Fuzz Tests

- **Reference Data**: None; no API or data parsing changes are planned.
- **Excluded Endpoints**: All backend endpoints; unchanged by this task.
- **Special Config**: Exercise malformed/empty `share` and unrelated query parameters to ensure the landing/sender experience does not crash and existing recipient behavior remains safe.

## Integration Tests

| Scenario | Steps | Test Data | Expected |
|----------|-------|-----------|----------|
| Landing to sender entry | Open `/`, activate “Upload your files” by mouse and keyboard. | No auth state required. | Browser navigates to `/upload`; current workspace and sign-in action appear. |
| Existing OIDC round trip | Start sign-in from `/upload`, complete provider login, return to callback. | Valid configured Keycloak account and callback. | Existing PKCE state check and token handling are unchanged; user returns to sender workspace. |
| Legacy recipient link | Open an existing `/?share=...` URL. | Valid share token. | Recipient state is selected before landing state and current recipient behavior remains available. |
| New recipient link | Create a share from `/upload`, then open its generated URL. | Existing authenticated sender and test file. | Generated URL uses `/upload?share=...`; recipient flow loads successfully. |
| Scroll reveal and CTA | Scroll landing scene through start, middle, and end on desktop and mobile; activate CTA. | Standard and narrow viewport. | Lid and interior light progress with scroll; CTA is usable and reaches `/upload`. |
| Reduced motion and keyboard | Enable reduced motion; navigate with keyboard and visible focus. | Browser reduced-motion preference and keyboard only. | No scroll-driven motion; CTA remains available with visible focus and clear semantics. |

## Visual/Accessibility Validation

- Verify mobile portrait, mobile landscape, tablet, and desktop widths; no horizontal overflow or clipped CTA.
- Verify scroll response is stable with touch, wheel, Page Down, and keyboard navigation.
- Verify screen reader semantics: single primary heading, decorative box excluded, CTA announced as a link, and no essential information conveyed only by animation or color.
- Verify contrast of text and controls on the dark background, focus visibility, and hover/pressed/disabled feedback.
- Verify the scene remains understandable if scripting or animation is unavailable; CTA still has an accessible route.

## Harness Constraint

`apps/web/package.json` exposes `npm test`, but the current `apps/web/angular.json` has no `test` target and the app has no visible browser-test setup. Planning should not claim automated component/E2E coverage until an existing harness is confirmed. At minimum, run the production Angular build and perform the documented route, viewport, reduced-motion, keyboard, and authentication walkthrough. If automated browser coverage is required, add it as a scoped test-harness decision before implementation rather than adding an unapproved runtime dependency.
