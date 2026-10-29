# SNS004 Implementation Inventory

## Unit Tests (Node built-in test runner; no added dependencies)

- [x] Resolve recipient query before landing/sender path selection.
- [x] Resolve `/upload` to sender view.
- [x] Resolve root OIDC callback to sender view.
- [x] Resolve root without query to landing view.
- [x] Clamp scroll progress at both scene boundaries.
- [x] Compute a midpoint scroll progress value correctly.

## Browser / Integration Checks

- [x] `/` renders landing; `/upload` renders existing sender workspace and sign-in control.
- [x] `/?share=token` renders the recipient view; unit coverage confirms share-query precedence on `/upload` as well.
- [ ] OIDC callback preserves the end-to-end authentication round trip (route selection is unit-tested; real auth was not exercised).
- [ ] Mouse and keyboard CTA navigation reaches `/upload` (mouse click was browser-checked; keyboard activation remains to verify).
- [ ] Scroll reveal states at start, midpoint, and end across mobile and desktop (phone-sized start/opening checked; desktop/end coverage remains).
- [ ] Reduced-motion presentation disables scroll transforms (implementation is present; runtime preference emulation remains to verify).
- [ ] Keyboard focus, contrast, screen-reader semantics, and no horizontal overflow (semantics inspected; full keyboard/contrast/overflow audit remains).

## Pipeline/API Tests

- None: no API, server, or persistence changes; CATS/Karate are not applicable.

## Implementation Order

1. Add failing tests and a zero-dependency `node --test` script for route state and progress math.
2. Implement the small pure helper and route dispatch in Angular.
3. Add the landing structure, CSS box, CTA, and scroll lifecycle.
4. Run unit tests and production build; validate the browser routes and motion behavior.
