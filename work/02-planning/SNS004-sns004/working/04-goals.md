# Goals and Constraints

## Measurable Goals

- [ ] Provide a dedicated, responsive landing page featuring a centered white-and-blue box on a charcoal-to-black canvas; verify at desktop and mobile viewport widths.
- [ ] Make box opening and interior light progress smoothly with scroll; verify start, midpoint, and end states on desktop and mobile.
- [ ] Present a clear “Upload your files” action at the end and navigate to the existing sender sign-in experience without changing OIDC behavior.
- [ ] Preserve recipient share-link handling and the existing sender upload flow.
- [ ] Provide keyboard-operable controls, visible focus, adequate contrast, meaningful semantics, and a reduced-motion static/fade presentation.
- [ ] Keep the visual page uncluttered and use restrained depth, shine, and warm light consistent with the request.

## Constraints

- **Timeline**: No deadline or milestone was provided.
- **Resources**: No staffing or budget constraint was provided.
- **Technical**: Existing client is a standalone Angular 19 application (`apps/web`) with a single root component, native CSS, and no Angular router or animation package. Avoid introducing dependencies unless required and approved.
- **Technical**: Existing OIDC authorization-code + PKCE implementation and recipient share query parameter behavior must remain intact.
- **Compliance**: No additional compliance requirements were provided; retain existing privacy and accessibility expectations.

## Non-Goals

- Replacing the identity provider or changing sign-in, token storage, session, or API behavior.
- Redesigning the upload workspace, recipient share flow, or backend.
- Copying Apple branding, interface patterns from a specific Apple product, or Greek mythology iconography; the mythological reference is limited to a subtle luminous finish.
- Introducing unrelated features, a new authentication flow, or a new animation dependency without demonstrated need.

## Assumptions

| Assumption | Risk | Validation |
|------------|------|------------|
| The current sender workspace is the intended destination for “Upload your files”; it exposes the existing sign-in action and then the upload flow. | High | Confirm in app behavior and preserve its current entry path during implementation. Flag the discrepancy in the plan. |
| The current root path can host the landing page while sender workspace and recipient-share behavior remain reachable through explicit route/query handling. | High | Inspect deployment routing and existing share-link generation; choose paths and deep-link behavior that work with static hosting. |
| A CSS-rendered box can satisfy the requested 3D visual without external imagery or runtime dependencies. | Medium | Validate the planned composition during implementation against desktop/mobile layouts. |
| Native scroll-linked progress can be implemented without introducing jank or blocking access to the CTA. | Medium | Verify browser support/fallback and use motion preference handling; test touch scrolling and keyboard navigation. |
