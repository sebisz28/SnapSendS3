# SNS004 Implementation Inventory

## Unit Tests (Node built-in test runner; no added dependencies)

- [ ] Resolve recipient query before landing/sender path selection.
- [ ] Resolve `/upload` to sender view.
- [ ] Resolve root OIDC callback to sender view.
- [ ] Resolve root without query to landing view.
- [ ] Clamp scroll progress at both scene boundaries.
- [ ] Compute a midpoint scroll progress value correctly.

## Browser / Integration Checks

- [ ] `/` renders landing; `/upload` renders existing sender workspace.
- [ ] `/?share=token` and `/upload?share=token` both render recipient view.
- [ ] OIDC callback preserves existing pathname behavior.
- [ ] Mouse and keyboard CTA navigation reaches `/upload`.
- [ ] Scroll reveal states at start, midpoint, and end; responsive mobile/desktop.
- [ ] Reduced-motion presentation disables scroll transforms.
- [ ] Keyboard focus, contrast, screen-reader semantics, and no horizontal overflow.

## Pipeline/API Tests

- None: no API, server, or persistence changes; CATS/Karate are not applicable.

## Implementation Order

1. Add failing tests and a zero-dependency `node --test` script for route state and progress math.
2. Implement the small pure helper and route dispatch in Angular.
3. Add the landing structure, CSS box, CTA, and scroll lifecycle.
4. Run unit tests and production build; validate the browser routes and motion behavior.
