# Design Sketch: SnapSendS3 External Document Sharing

**Status**: Direction Approved — ARCH-B1 conditional pass  
**Iteration**: 1  
**Sources**: approved PRD, locked domain model, validated boundary map, architecture context, risk register.

## 1. Service Boundary

SnapSendS3 is a modular Spring Boot monolith for the local demo. It owns Share lifecycle, sender/public access decisions, lifecycle metadata and worker orchestration. This follows the validated ownership matrix. `[OBS: boundary map, DEC-008]`

It does not own Keycloak identities, R2 bucket policy/object bytes, ClamAV detection, Caddy edge configuration, recipient identity, or production compliance policy. `[OBS: boundary map]`

## 2. Module Map

| Module | Responsibility | Owns / produces | Consumes | Negative boundary | Evidence |
|---|---|---|---|---|---|
| Sender API | Authenticated creation/finalization | Share draft, upload authorization | Keycloak subject, limits | Does not transfer file bytes through API. | [OBS/ASM: PRD, BLK-001] |
| Public Share API | Link/password/grant decision | Access outcome, download authorization | Share state, password | Does not authenticate recipients or expose permanent URL. | [OBS: PRD] |
| Lifecycle module | State transitions | Share/File readiness/expiry | Worker outcomes | Does not scan or zip files. | [OBS: domain] |
| Scan worker | Durable scan processing | Scan attempt and result | Share File, clamd | Does not authorize download. | [OBS/ASM: domain, BLK-003] |
| Archive worker | ZIP creation | Archive state/object | Clean Share Files | Does not archive unsafe files or use local-disk-first strategy. | [OBS: domain, DEC-013] |
| Cleanup worker | Expired data removal | Cleanup attempt/result | Expired objects | Does not extend expiry or restore access. | [OBS: domain] |
| Usage guardrail | Budget decision | alert/reject outcome | configured budgets, usage data | Does not redefine R2 billing/account policy. | [ASM: Q-005] |
| Persistence module | Metadata/audit/job records | durable application state | module changes | Does not own R2 bytes or Keycloak identities. | [OBS: context] |

## 3. Runtime Triggers

Authenticated Sender API calls create/finalize Shares; finalized uploads create scan work; clean multi-file results create archive work; Recipient public-link calls request access; scheduler triggers expiry cleanup; and pre-create flow triggers usage evaluation. `[OBS: PRD/domain]`

## 4. Data Model & Sources

| Entity | Key fields | Authoritative source | Evidence |
|---|---|---|---|
| Share | ID, token, sender subject, expiry, password hash, status | PostgreSQL / SnapSendS3 | [OBS] |
| Share File | name, type, size, object key, scan state | PostgreSQL metadata; R2 bytes | [OBS] |
| Archive | object key, size, status | PostgreSQL metadata; R2 bytes | [OBS] |
| Download Attempt | time, outcome, policy-permitted client data | PostgreSQL | [OBS] |
| Upload/scan/cleanup job | binding, attempts, state | PostgreSQL | [INF: DEC-010] |
| Usage snapshot | estimated usage, threshold decision | SnapSendS3/configured source | [ASM: Q-005] |

Keycloak is authoritative for Sender identity/roles; R2 for bytes; clamd for detection verdict. `[OBS/ASM: boundary map]`

## 5. Event Map

| Event | Owner | Consumer | Schema | Evidence |
|---|---|---|---|---|
| ShareFinalized | Lifecycle | Scan worker | Needs creation | [INF] |
| FileScanCompleted | Scan worker | Lifecycle | Needs creation | [INF] |
| ArchiveRequested | Lifecycle | Archive worker | Needs creation | [INF] |
| ArchiveReady / ArchiveFailed | Archive worker | Lifecycle | Needs creation | [INF] |
| ShareExpired | Lifecycle/scheduler | Cleanup worker | Needs creation | [INF] |
| CleanupCompleted / CleanupFailed | Cleanup worker | Lifecycle/audit | Needs creation | [INF] |

Events are durable internal milestones backed by PostgreSQL job records, not an external broker. `[OBS: DEC-010]`

## 6. API & Integration Contracts

| Contract | Direction | Purpose | Status | Evidence |
|---|---|---|---|---|
| Create/finalize Share API | Sender → SnapSendS3 | Create lifecycle state | Needs creation | [OBS] |
| Scoped upload/download grants | SnapSendS3 ↔ R2 | Private byte transfer | Provisional, BLK-001 | [ASM] |
| Public Share/password/grant API | Recipient → SnapSendS3 | Link access decision | Needs creation | [OBS] |
| OIDC/JWT validation | SnapSendS3 ↔ Keycloak | Sender authentication/role | Provisional, BLK-002 | [ASM] |
| clamd scan invocation | Scan worker → ClamAV | Detection result | Needs creation, BLK-003 | [ASM] |
| Caddy HTTPS route | Caddy → SnapSendS3 | Public ingress | Provisional, BLK-004 | [ASM] |

## 7. Quality Attribute Constraints

| Attribute | Architectural response | Trade-off | Evidence |
|---|---|---|---|
| Private access | API checks state/password then creates object-specific short-lived grant. | Depends on R2 spike. | [OBS/ASM: NFR-002, BLK-001] |
| Scan safety | Fail closed; clamd timeout 60s, max 3 retries, then FAILED. | Availability waits for safe verdict. | [OBS/ASM: NFR-003, Q-003] |
| Retention | `expiresAt` access denial plus worker cleanup and R2 lifecycle defense. | Two mechanisms to maintain. | [OBS: NFR-004, DEC-011] |
| Capacity | Enforce 100 MB/file and 500 MB/Share on upload and archive; validate boundaries. | Selected low demo cap replaces former 10 GB assumption. | [OBS/ASM: DEC-006, T1] |
| Archive | Stream clean sources into ZIP and upload to R2. | More implementation complexity than local disk. | [OBS: DEC-013] |
| Observability | Secret-safe structured lifecycle events. | Reduced payload diagnostics. | [OBS: NFR-007/008] |

## 8. Architectural Decision Points

| Decision | Options considered | Selected | NFR impact | Evidence |
|---|---|---|---|---|
| Application shape | modular monolith / split workers / microservices | modular monolith | simpler operations; modular code boundaries required | [OBS: DEC-008] |
| Transfer path | direct scoped R2 / API proxy | direct scoped R2 | protects API capacity; R2 contract required | [OBS: DEC-009] |
| Durable work | PostgreSQL jobs / in-memory async / broker | PostgreSQL jobs | survives restart without new infra | [OBS: DEC-010] |
| Cleanup | dual cleanup / app only / R2 only | dual cleanup | stronger retention enforcement | [OBS: DEC-011] |
| Scanning | clamd / local process / vendor | clamd | streaming model; contract required | [OBS: DEC-012] |
| Archive | R2 streaming ZIP / local disk ZIP | R2 streaming ZIP | protects local resources | [OBS: DEC-013] |

## 9. Open Questions

| Question | Impact | Blocking | Owner / resolution |
|---|---|---|---|
| R2 scoped-transfer spike | transfer design | Yes | Sebastian Szekely / BLK-001 |
| Keycloak role claim mapping | Sender authorization | Yes | Sebastian Szekely / BLK-002 |
| clamd contract validation | scan gate | Yes | Sebastian Szekely / BLK-003 |
| Caddy capacity spike | selected demo limits | Yes | Sebastian Szekely / BLK-004 |
| absolute R2 budgets | guardrail | No for design | Sebastian Szekely / configure pre-demo |
| compliance/product-fit/production topology | production promotion | No for demo | Sebastian Szekely / later validation |

## 10. Assumptions Register

| Assumption | Evidence | Risk if wrong |
|---|---|---|
| Private R2 presigned transfer fits selected access model. | ASSUMED | use API proxy/another storage pattern. |
| Modular monolith fits local-demo load. | INFERRED | future decomposition needed. |
| PostgreSQL jobs provide enough worker durability. | INFERRED | add broker/scheduler. |
| R2 lifecycle supports cleanup defense in depth. | ASSUMED | strengthen application cleanup. |
| Streaming ZIP avoids unsafe resource use at selected limits. | ASSUMED | change archive strategy/limit. |
| clamd can process selected payloads. | ASSUMED | change scan integration/limit. |

## 11. Evaluation Criteria Log

| Iteration | Feedback classification | Outcome |
|---:|---|---|
| 1 | No evaluation reset | Architect confirmed boundaries, modules, data, events, quality constraints, and selected all presented recommendations. |
| 2 | Context injection | Added test-gated limits, scanning contract, Keycloak role proposal, and R2 guardrail behavior. |

## 12. Validation Summary

One service boundary, eight non-overlapping modules, six milestone events, six API/integration contracts, six selected decisions, six test-gated assumptions, and four implementation blockers are explicit. No pattern library was available; no convention was fabricated.
