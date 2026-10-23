# Task Sizing

## Complexity Dimensions

### Technical Complexity

- **Analysis Scope**: API service, storage adapter, Caddy proxy, and local Compose verification.
- **Estimated File Count**: 4–6 implementation and test files.
- **Cross-References**: 4 architecture and decision documents.
- **Integration Points**: Browser, Caddy, LocalStack, and the S3 presigner.

## Effort Estimation

### Multi-Axis Scoring

| Axis | Score (0-3) | Rationale |
|---|---:|---|
| Scope / Surface Area | 2 | Changes span service, storage adapter, proxy configuration, and tests. |
| Coupling / Interfaces | 1 | The storage adapter carries a filename parameter; no public API schema changes. |
| Novelty / Uncertainty | 1 | Signed response headers and proxy behavior are established patterns but LocalStack parity must be verified. |
| Dependencies | 1 | Existing LocalStack and Caddy environment is required. |
| Testing & Verification | 2 | Unit tests plus browser-visible integration verification are required. |
| Risk / Blast Radius | 2 | Recipient delivery is a core path and headers must remain safe. |

### Total Complexity Score: 9/18

### Size Estimate

- **Shirt Size**: M
- **Time Estimate**: 1–2 weeks
- **Confidence**: Medium

## Dependencies and Risk Factors

- LocalStack must be available through the Caddy proxy for end-to-end verification.
- Response header parity with Cloudflare R2 remains the main integration uncertainty.
- The work is easy to roll back because it changes delivery metadata, not stored file content or database schema.
