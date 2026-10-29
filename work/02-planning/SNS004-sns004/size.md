# Task Sizing

## Complexity Dimensions

### Technical Complexity

- **Analysis Scope**: One web client module and deployment fallback/configuration context.
- **File Count**: Approximately 2–4 implementation files, plus focused documentation/tests.
- **Cross-References**: Existing sender workspace, OIDC callback, recipient share query, Caddy SPA fallback, and Keycloak redirect allow-list.
- **Integration Points**: Browser URL/scroll APIs, existing OIDC provider, existing sender/recipient flows; no API contract changes.

## Effort Estimation

### Multi-Axis Scoring

| Axis | Score (0-3) | Rationale |
|------|------------|----------|
| Scope / Surface Area | 1 | Changes are limited to the Angular web client and a small amount of route/deployment verification. |
| Coupling / Interfaces | 1 | Adds internal route/query dispatch while preserving existing public URL and auth contracts. |
| Novelty / Uncertainty | 1 | CSS 3D and scroll-linked progress are established browser patterns; responsive motion tuning is new to this app. |
| Dependencies | 1 | No blocking dependency; Keycloak callback allow-list requires environment verification. |
| Testing & Verification | 2 | Current app lacks a configured Angular test target, and route/auth/mobile/reduced-motion behavior requires browser-level validation. |
| Risk / Blast Radius | 1 | Root entry changes, but query/path dispatch can preserve existing flows and rollback is limited to the web client. |

### Total Complexity Score: 7/18

### Size Estimate

- **Shirt Size**: S
- **Time Estimate**: 3–5 days
- **Confidence**: Medium; visual refinement and current test-harness gap can affect effort.

## Dependencies and Risk Factors

- No blocking task or backend dependency identified.
- Verify `/upload` is permitted as an OIDC redirect URI in the configured Keycloak client.
- Validate legacy and new recipient URL paths before accepting the route change.
- `npm test` is not currently wired to an Angular test target; browser walkthrough is required unless a harness is added.
