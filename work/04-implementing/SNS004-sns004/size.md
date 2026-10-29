# Task Sizing

## Complexity Dimensions

### Technical Complexity

- **Analysis Scope**: Angular web client, Keycloak login theme, Compose wiring, realm import configuration, and deployment activation guidance.
- **File Count**: Approximately 7–10 implementation/configuration files, plus tests and documentation.
- **Cross-References**: Existing sender workspace, OIDC callback and PKCE handling, recipient share query, Caddy SPA fallback, Keycloak theme inheritance, and redirect allow-list.
- **Integration Points**: Browser scroll APIs, existing OIDC provider and login theme, Compose mount, sender/recipient flows; no API contract changes.

## Effort Estimation

### Multi-Axis Scoring

| Axis | Score (0-3) | Rationale |
|------|------------|----------|
| Scope / Surface Area | 2 | Changes span the Angular client, Keycloak theme, Compose mount, and realm import configuration. |
| Coupling / Interfaces | 2 | The CTA uses the existing OIDC callback; theme availability depends on Keycloak deployment and realm configuration. |
| Novelty / Uncertainty | 2 | Adds scroll-linked artwork and a Keycloak 26 theme while preserving provider forms and auth behavior. |
| Dependencies | 1 | No new runtime package; Keycloak 26 image and existing callback allow-list are already present. |
| Testing & Verification | 2 | Node unit coverage plus manual Keycloak/viewport/accessibility checks are needed; no browser E2E runner exists. |
| Risk / Blast Radius | 2 | Login presentation changes affect all realm sign-in pages, while inherited forms and unchanged OIDC logic limit behavioral risk. |

### Total Complexity Score: 11/18

### Size Estimate

- **Shirt Size**: M
- **Time Estimate**: 4–6 days
- **Confidence**: Medium; visual refinement and current test-harness gap can affect effort.

## Dependencies and Risk Factors

- No blocking task or backend dependency identified.
- Verify `/upload` remains permitted as an OIDC redirect URI in the configured Keycloak client.
- Existing realms skip startup import; the login theme must be selected in Realm Settings without re-importing user data.
- Validate legacy and new recipient URL paths before accepting the route change.
- `npm test` is not currently wired to an Angular test target; browser walkthrough is required unless a harness is added.
