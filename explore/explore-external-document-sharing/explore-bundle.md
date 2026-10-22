# Explore Bundle: SnapSendS3 external document sharing

**Overview**  
This Fast Lane Explore validates a local-only SnapSendS3 capability for authenticated warehouse users to share files with recipients outside their network. It will establish the operational context, test the Java/Spring Boot and Cloudflare R2 feasibility assumptions, and produce an architecture analysis and PRD suitable for a later Govern phase.

## Header

| Field | Value |
|---|---|
| Status | Active |
| Created | 2026-10-22 |
| Timeline | Current session |
| Explore Type | Fast Lane |
| Steering Team | Sebastian Szekely — Product Manager, Architect, and Lead Engineer (local dummy-project exception) |

## Signal Information

| Field | Value |
|---|---|
| Signal Title | External document sharing |
| Problem Statement | Authenticated dava.flow users may need to send files securely to recipients outside their local network. This remains an assumption pending a real requester or operational evidence. |
| Tech Stack | Java 21, Spring Boot, Angular/TypeScript, PostgreSQL, Maven, Flyway, Docker Compose, private Cloudflare R2 via its S3-compatible API. |
| Key Page Types / Features | Authenticated upload and share creation; public recipient download; expiry, optional password protection, and archive download for multi-file shares. |
| Key Requirements | Six-hour retention; malware scanning; two or more files download as one archive; proposed 10 GB upload maximum; private storage and HTTPS-only public access. |

## Solution Profile — Domains in Scope

| Domain | In Scope | Description |
|---|---|---|
| UX / UI | Yes (confirmed) | Authenticated upload/share flow and recipient download flow. |
| Backend Services | Yes (confirmed) | Share lifecycle, authorization, expiry, file processing, and public download orchestration. |
| Data Layer | Yes (confirmed) | Share metadata, file metadata, expiry, scan state, and audit data in PostgreSQL. |
| Integrations | Yes (confirmed) | S3-compatible object storage and malware-scanning capability feasibility. |
| Infrastructure | Yes (confirmed) | Public HTTPS entry point and externally reachable download path. |
| Security / Compliance | Yes (confirmed) | Sender authentication, recipient controls, password option, short-lived access, retention, and scanning. |
| Mobile Development | No | No mobile requirement has been identified. |
| File Processing | Yes (confirmed) | Malware scan lifecycle and single archive output for multi-file shares. |

## Planned Activities

### Phase 1: Context (current session)

| Code | Activity | Owner | Dependencies | Output | What it closes |
|---|---|---|---|---|---|
| A1 | Context documentation | Sebastian Szekely — PM | None | `context.md` | Q1, Q2, AS-1, AO-1 |

### Phase 2: Feasibility & Architecture (current session)

| Code | Activity | Owner | Dependencies | Output | What it closes |
|---|---|---|---|---|---|
| A5 | Technical feasibility | Sebastian Szekely — Lead Engineer | A1 | Feasibility findings in `architecture-context.md` | Q3, Q4, AT-1, AT-2, AT-3 |
| A6 | Architecture context | Sebastian Szekely — Architect | A1, A5 | Architecture analysis in `architecture-context.md` | Q5, Q6, AT-2, AO-2 |

### Phase 3: Specification (current session)

| Code | Activity | Owner | Dependencies | Output | What it closes |
|---|---|---|---|---|---|
| A11 | PRD generation | Sebastian Szekely — PM | A1, A5, A9 | `explore/prds/external-document-sharing-prd.md` | Consolidates validated requirements |

## Open Questions

### Scope & Requirements

- Q1: Which warehouse users and external recipient groups need the capability, and what concrete workflow validates it? — resolves in: A1
- Q2: What file sizes, share frequency, retention exceptions, and link controls are actually needed? — resolves in: A1
- Q3: What password, download-limit, and audit requirements apply to an external recipient? — resolves in: A5

### Technical Feasibility

- Q4: Can the existing local/target deployment provide public HTTPS access while keeping object storage private? — resolves in: A5
- Q5: How should malware scanning gate download availability and failure handling? — resolves in: A6
- Q6: Which archive format and generation strategy safely supports multi-file shares within the proposed 10 GB limit? — resolves in: A6

## Active Assumptions

| Code | Assumption | Risk if wrong | Validate in |
|---|---|---|---|
| **Stakeholder assumptions** |  |  |  |
| AS-1 | External carriers and other recipients need secure links rather than the current illustrative email/personal-sharing workflow. | The feature solves no real operational problem. | A1 |
| **Technical assumptions** |  |  |  |
| AT-1 | Any file type can be accepted safely up to 10 GB. | Unsafe, unsupported, or costly uploads may require a narrower policy. | A5 |
| AT-2 | A private Cloudflare R2 bucket and short-lived S3-compatible URLs can support the target download flow. | The planned storage design may not meet security or delivery needs. | A5 |
| AT-3 | The target deployment can expose a secure public HTTPS route without exposing private services or objects. | External recipients cannot reach the share safely. | A5 |
| **Operational assumptions** |  |  |  |
| AO-1 | A six-hour retention period works for recipients. | Recipients may not download in time or data may be retained too long. | A1 |
| AO-2 | Multi-file shares can always be delivered as one archive. | Archive generation may fail or be impractical for large files. | A6 |

## Risks

| Code | Risk | Impact | Likelihood | Mitigation |
|---|---|---|---|---|
| R1 | The capability has no corroborated requester or workflow. | High | Medium | Validate a real workflow and adoption need in A1 before Govern. |
| R2 | Public HTTPS, private storage, or short-lived download access cannot meet the intended security boundary. | High | Medium | Validate feasibility in A5 and architecture analysis in A6. |
| R3 | Malware scanning or archive creation delays delivery or cannot handle large uploads. | High | Medium | Define and assess the processing lifecycle in A6. |

## Expected Outputs

- `explore/explore-external-document-sharing/explore-bundle.md` — approved discovery plan.
- `explore/explore-external-document-sharing/discovery.md` — live artifact index and open-question register.
- `explore/explore-external-document-sharing/context.md` — validated workflow and requirement context.
- `explore/explore-external-document-sharing/architecture-context.md` — feasibility findings and architecture analysis.
- `explore/prds/external-document-sharing-prd.md` — PRD ready for a later Govern phase.

## Constraints

| Type | Constraint |
|---|---|
| Budget | Local dummy project; no separate budget is allocated. |
| Timeline | Complete the Fast Lane exploration in the current session. |
| Resources | Sebastian Szekely performs the PM, Architect, and Lead Engineer roles under an explicit local-project exception. |
| Technical | Java 21, Spring Boot, Angular/TypeScript, PostgreSQL, Maven, and private Cloudflare R2 are the documented baseline. |
| Compliance | Sender authentication, six-hour retention, malware scanning, HTTPS-only public access, and recipient controls are required; wider regulatory needs remain unverified. |
| Operational | Two or more files must be delivered as one archive; the upload maximum is proposed at 10 GB. |

## Checkpoints

| Milestone | Criteria |
|---|---|
| Context complete | A1 produces `context.md`, resolves or explicitly carries Q1 and Q2, and validates the local dummy-project scope. |
| Feasibility & architecture complete | A5 and A6 produce `architecture-context.md` covering Q3–Q6 and AT-1–AT-3. |
| Specification complete | A11 produces `external-document-sharing-prd.md` with every unresolved assumption labelled for Govern. |

## Document History

| Date | Version | Author | Changes |
|---|---|---|---|
| 2026-10-22 | 1.0 | Explore Agent | Initial Fast Lane Explore Bundle created from the routed signal. |
| 2026-10-22 | 1.1 | Explore Agent | Normalized architecture-context activity identifier from A9 to A6. |

## Related Artifacts

- Signal: `signal/signals/20261022-external-document-sharing.md`
- Discovery Index: `explore/explore-external-document-sharing/discovery.md`
- PRD: `explore/prds/external-document-sharing-prd.md` (planned)
