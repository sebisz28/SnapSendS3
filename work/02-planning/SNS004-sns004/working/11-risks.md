# Risks and Dependencies

## Risk Assessment

| Risk | Category | Likelihood | Impact | Mitigation |
|------|----------|------------|--------|------------|
| Changing the root view could break existing recipient links. | UX / compatibility | Medium | High | Select recipient view whenever `share` exists, independent of path; verify legacy `/?share=...` and new `/upload?share=...`. |
| OIDC callback or redirect URI behavior could change if the sender pathname changes. | Security / integration | Low | High | Keep existing OIDC methods and start authentication from `/upload`; verify provider callback and PKCE state handling. |
| Scroll-linked transforms may stutter or feel awkward on small screens or touch devices. | Performance / UX | Medium | Medium | Limit work to one rAF update per frame, avoid Angular change detection, test representative mobile/desktop sizes and touch scrolling, retain static fallback. |
| Sticky scene height or CTA location may be difficult to use on short viewports or zoomed layouts. | UX / accessibility | Medium | Medium | Keep CTA in normal flow after the scene, avoid requiring animation completion, test narrow/short viewport and browser zoom. |
| CSS artwork may not remain visually convincing across viewport ratios. | UX | Medium | Low | Use bounded responsive box sizing, preserve perspective/aspect, validate desktop/mobile variants and maintain a readable static fallback. |
| Current test script is not executable as configured because Angular CLI has no test target. | Delivery / quality | High | Medium | Require production build plus documented browser and accessibility walkthrough; decide on automated test-harness work before adding dependencies. |
| Keycloak redirect allow-list may not accept `/upload` even if current root is allowed. | Dependency / security | Low | Medium | Inspect configured client redirect URIs during implementation; update only deployment configuration needed to preserve existing PKCE flow. |
| Data model, database, backend, and storage changes | Data model / operations | Not applicable | Not applicable | No changes planned in these categories. |

## Dependencies

### Blocking (must complete first)

- None identified. The web client and SPA fallback already exist.

### Dependent (depends on this task)

- None identified.

### Related (shared context)

- [x] SNS001: Parent capability/epic formation context; Angular sender and recipient experiences are in scope.
- [x] SNS003: Completed download behavior work; no direct implementation dependency.

## Validation Against Research

- [x] No conflict with backend/storage ADRs; no API or persistence changes are planned.
- [x] Dependencies align with current Angular client and Caddy fallback.
- [x] The root share URL and “existing login page” terminology gaps are recorded and resolved in the route approach.

### Conflicts/Gaps Found

| Issue | Description | Resolution |
|-------|-------------|------------|
| Separate “login page” not present | The existing UI has an inline sender sign-in action in the sender workspace. | CTA opens `/upload`, the existing sender/sign-in entry. |
| Test target missing | `npm test` is listed in package scripts but the Angular configuration has no test builder target. | Include build and manual browser validation now; scope any future automated harness separately if needed. |
| Redirect URI allow-list | Actual local/imported Keycloak redirect URI configuration may be environment-specific. | Check deployment configuration during implementation and preserve origin and existing OIDC flow. |
