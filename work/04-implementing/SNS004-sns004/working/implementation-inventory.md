# SNS004 Implementation Inventory

## Unit Tests (Node built-in test runner; no added dependencies)

- [x] Resolve recipient query before landing/sender path selection.
- [x] Resolve `/upload` to sender view.
- [x] Resolve root OIDC callback to sender view.
- [x] Resolve root without query to landing view.
- [x] Clamp scroll progress at both scene boundaries.
- [x] Compute a midpoint scroll progress value correctly.
- [x] Build a same-origin `/upload` OIDC redirect URI for the landing CTA.
- [x] Verify Keycloak realm theme selection, parent theme, stylesheet registration, selectors, and read-only Compose mount.
- [x] Keep the box interior light within the opening and remove the blue ribbon from the opening and lid.

## Browser / Integration Checks

- [x] `/` renders landing; `/upload` renders existing sender workspace and sign-in control.
- [x] `/?share=token` renders the recipient view; unit coverage confirms share-query precedence on `/upload` as well.
- [ ] OIDC callback preserves the end-to-end authentication round trip (redirect URI unit-tested; real credentials/auth round trip remains untested).
- [ ] Landing CTA begins the Keycloak flow with `/upload` callback (unit-tested; browser navigation and keyboard activation remain to verify).
- [x] Fresh-realm Keycloak credential page renders with the custom theme; browser accessibility tree retains username, password, show-password, and Sign In controls.
- [ ] Keycloak password-recovery page renders with the custom theme (inherited form is unchanged; runtime recovery-page check remains).
- [ ] Scroll reveal states at start, midpoint, and end across mobile and desktop (phone-sized start/opening checked; desktop/end coverage remains).
- [ ] Reduced-motion presentation disables scroll transforms (implementation is present; runtime preference emulation remains to verify).
- [ ] Keyboard focus, contrast, screen-reader semantics, and no horizontal overflow (semantics inspected; full keyboard/contrast/overflow audit remains).

## Pipeline/API Tests

- None: no API, server, or persistence changes; CATS/Karate are not applicable.

## Implementation Order

1. Add failing tests and a zero-dependency `node --test` script for route state and progress math.
2. Implement the small pure helper and route dispatch in Angular.
3. Add the landing structure, CSS box, CTA, and scroll lifecycle.
4. Run unit tests and production build; validate the browser routes, Keycloak login page, and motion behavior.
