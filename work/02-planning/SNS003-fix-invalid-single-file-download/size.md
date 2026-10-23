# Task Sizing

## Complexity Dimensions

### Technical Complexity

- **Analysis Scope**: Existing API service, storage adapter, Caddy proxy, and local Compose verification.
- **Estimated File Count**: 2–4 regression-test and verification files.
- **Cross-References**: 4 architecture and decision documents.
- **Integration Points**: Browser, Caddy, LocalStack, and the S3 presigner.

## Effort Estimation

### Multi-Axis Scoring

| Axis | Score (0-3) | Rationale |
|---|---:|---|
| Scope / Surface Area | 1 | Work adds coverage around existing service, storage, and proxy behavior. |
| Coupling / Interfaces | 0 | No interface change is planned. |
| Novelty / Uncertainty | 1 | Signed response headers and proxy behavior are established patterns but LocalStack parity must be verified. |
| Dependencies | 1 | Existing LocalStack and Caddy environment is required. |
| Testing & Verification | 2 | Unit tests plus browser-visible integration verification are required. |
| Risk / Blast Radius | 2 | Recipient delivery is a core path and headers must remain safe. |

### Total Complexity Score: 7/18

### Size Estimate

- **Shirt Size**: S
- **Time Estimate**: 3–5 days
- **Confidence**: Medium

## Dependencies and Risk Factors

- LocalStack must be available through the Caddy proxy for end-to-end verification.
- Response header parity with Cloudflare R2 remains the main integration uncertainty.
- The work is easy to roll back because it changes delivery metadata, not stored file content or database schema.
