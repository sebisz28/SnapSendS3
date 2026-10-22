---
domain: SnapSendS3 External Document Sharing
slug: external-document-sharing
source: explore/explore-external-document-sharing/domain-analysis.md
glossary_version: 2026-10-22
validated_by: Sebastian Szekely
explore_type: Fast Lane
evidence_label: OBS
---

# PRD · SnapSendS3 External Document Sharing

**Status**: Approved  
**Priority**: Medium  
**Stakeholder Appetite**: Low (sponsor-directed)  
**Created / Last Updated**: 2026-10-22

**Sources**: [Signal](../../signal/signals/20261022-external-document-sharing.md) · [Context](../explore-external-document-sharing/context.md) · [Technical feasibility](../explore-external-document-sharing/technical-feasibility.md) · [Architecture context](../explore-external-document-sharing/architecture-context.md) · [Domain analysis](../explore-external-document-sharing/domain-analysis.md)

## 1. Problem Statement

Authenticated warehouse users need to send operational files to external people outside their network without relying on personal sharing tools. The current email/personal-service scenario is illustrative, not validated. SnapSendS3 must provide a controlled, temporary route that prevents delivery of files until malware scanning has succeeded.

## 2. Goals

1. Demonstrate clean single-file delivery in 100% of the agreed automated happy-path tests.
2. Demonstrate that an anonymous Recipient can retrieve every clean, unexpired test Share via HTTPS, supplying a password only when configured.
3. Demonstrate zero download grants for unclean test files and 100% expiry enforcement in controlled-clock tests.

## 3. Success Metrics & KPIs

| Metric | Baseline | Target / measurement | Timeline |
|---|---|---|---|
| Clean single-file delivery | Not measured | 100% happy-path integration tests pass | Before demo acceptance |
| Scan gate | Not measured | 0 grants for pending, infected, or failed files | Before demo acceptance |
| Multi-file packaging | Not measured | 100% of test Shares with 2+ files yield one clean ZIP | Before demo acceptance |
| Retention | Not measured | 100% clock-controlled tests deny access at six hours and record cleanup outcome | Before demo acceptance |
| Usage guardrail | Not measured | Configured threshold action is covered by service-level test | Before demo acceptance |

**Primary KPI**: safe end-to-end delivery of a clean, unexpired Share.  
**Secondary KPIs**: scan-gate correctness, retention correctness, and guardrail enforcement.

## 4. Target Users

- **Sender**: Keycloak-authenticated user who needs to send operational files externally.
- **Recipient**: Anonymous customer, supplier, or carrier who needs a simple download route without a login.
- **System actors**: Scan worker, Archive worker, and Cleanup worker.

No persona artifact was selected for Fast Lane; the carrier scenario remains illustrative.

## 5. User Flows & Scenarios

**Primary:** Sender signs in → creates Share → uploads private files → finalizes → files scan → ZIP builds if needed → Recipient opens public HTTPS link → supplies password if configured → receives a short-lived download grant.

**Alternative / error flows:** pending, failed, or infected scans make the Share unavailable; archive failure makes a multi-file Share unavailable; expired links disclose no R2 URL; wrong password creates a safe failed attempt only.

## 6. Functional Requirements

| ID | Requirement | User story | Acceptance criteria | Priority |
|---|---|---|---|
| R-001 | Authenticated Share creation | As a Sender, I want to create a temporary Share so I can deliver files externally. | Only Keycloak-authenticated Sender may create; server sets unique opaque token and `expiresAt = createdAt + 6h`. | High |
| R-002 | Private upload | As a Sender, I want to upload files privately so recipients cannot bypass Share access rules. | One or more files upload using scoped R2 access; zero-file finalization fails; configured size rule is enforced server-side. | High |
| R-003 | Malware scan gate | As a Sender, I want unsafe files withheld so external delivery is scan-gated. | No download grant while any file is pending, infected, or scan-failed; outcome persists. | High |
| R-004 | Multi-file ZIP | As a Recipient, I want one ZIP for a multi-file Share so I can retrieve it simply. | Two or more clean files produce one ZIP; no individual-file fallback before/after archive failure. | High |
| R-005 | Anonymous public download | As a Recipient, I want to download a clean Share without an account. | Ready, unexpired Share only may receive a short-lived grant; recipient never sees raw/permanent R2 location. | High |
| R-006 | Optional password | As a Sender, I want an optional password so I can add a second link-access check. | Secure hash only; password required only when configured; secret/grant never enters audit data. | High |
| R-007 | Expiry cleanup | As a Sender, I want Shares to expire automatically so files are not retained indefinitely. | Access denied at expiry independently of cleanup; deletion of files/archive/metadata is idempotent and retryable. | High |
| R-008 | R2 usage guardrail | As a demo operator, I want a configurable usage guardrail so free-tier usage is controlled. | Threshold/action are configuration; new Share is warned or rejected at threshold; no assumption that 10 GB-month is a per-file free limit. | Medium |
| R-009 | Minimal access audit | As a demo operator, I want safe access outcomes recorded so delivery behavior can be diagnosed. | Allowed, password-failed, not-ready, and expired outcomes are recorded without secrets. | Medium |
| R-010 | Sender status | As a Sender, I want accurate Share status so I know when a Recipient can download. | Uploading, Scanning, Ready, Rejected and Expired states are visible; ready is never shown early. | Medium |

## 7. Non-Functional Requirements

| ID | Requirement | Related | Acceptance criteria |
|---|---|---|---|
| NFR-001 | HTTPS-only public route via Caddy | R-005 | HTTP redirects/refuses and local TLS route is verified. |
| NFR-002 | Private R2 and short-lived grants | R-002, R-005 | Unauthenticated object read fails; grants expire within configured TTL. |
| NFR-003 | Fail closed on scan failure | R-003 | Pending, failed, infected produces no grant. |
| NFR-004 | Expiry independent of cleanup | R-007 | Controlled-clock test denies access after expiry. |
| NFR-005 | Prove configured size path | R-002, R-004 | Documented spike passes transfer, scan, ZIP and cleanup at selected limit. |
| NFR-006 | Accessible flows | R-005, R-010 | Keyboard walkthrough and automated WCAG-AA-oriented check pass. |
| NFR-007 | Observable workers | R-003, R-004, R-007 | Structured state/worker/cleanup events exclude secrets. |
| NFR-008 | Secret-safe logging | R-006, R-009 | Logs/audit contain no passwords, R2 credentials, grants, or file content. |
| NFR-009 | Automated test coverage | R-001–R-010 | Unit, integration, and relevant E2E paths cover lifecycle/security gates. |

No production SLA, RTO/RPO, browser matrix, or numeric latency target has been supplied.

### Quality Gates

**Definition of Ready:** each requirement has source traceability and testable acceptance criteria; its state/failure behavior is defined; configuration/secrets are externalized; and every external dependency has a test plan.

**Definition of Done:** all applicable acceptance criteria and NFRs pass; no public R2 object or scan-gate bypass exists; unit/integration/E2E tests pass; the selected maximum-size spike is recorded; expiry/cleanup retry behavior is tested; secrets are absent from logs and source; and setup/limitations are documented.

## 8. Constraints

- **[CONFIRMED]** Java 21/Spring Boot, Angular/TypeScript, PostgreSQL, Keycloak, private Cloudflare R2, asynchronous ClamAV, Java ZIP worker, and Caddy HTTPS edge comprise the local-demo baseline.
- **[CONFIRMED]** Recipients do not log in; recipient identity validation, download limits, and revocation controls are out of scope.
- **[CONFIRMED]** Two or more files are delivered as ZIP; a single file is delivered as its original payload.
- **[ASSUMPTION]** Demo spike limits are 100 MB per file and 500 MB total per Share; upload, ZIP boundary, and over-limit validation are required before implementation.
- **[ASSUMPTION]** Demo guardrail alerts at 80% and rejects uploads at 100% of configured storage/operation budgets; absolute quota values remain open.
- **[ASSUMPTION]** Production privacy, audit, regional and deployment requirements are not supplied and must be resolved before production promotion.

## 9. Out of Scope

Production deployment, notifications, billing, analytics, branded portal, recipient accounts, recipient identity checks, download limits, revocation, and identity-management work beyond Keycloak integration.

## 10. Open Questions

| ID | Question | Owner | Priority | Target resolution |
|---|---|---|---|---|
| Q-001 | Do 100 MB/file and 500 MB/Share pass upload, scan, and ZIP boundary tests? | Sebastian Szekely | High | Before implementation commitment |
| Q-002 | Can private R2 transfers deliver scoped upload/download as designed? | Sebastian Szekely | High | Before storage implementation |
| Q-003 | Does the 60-second timeout, three-retry, INFECTED/FAILED ClamAV contract work in the selected runtime? | Sebastian Szekely | High | Before scan-worker implementation |
| Q-004 | Can ZIP process 500 MB total safely? | Sebastian Szekely | High | Before archive-worker implementation |
| Q-005 | What absolute storage/operation budgets back the 80% alert / 100% reject guardrail? | Sebastian Szekely | Medium | Before demo configuration |
| Q-006 | Does the actual Keycloak realm/client emit `dava.sender` in the `roles` claim? | Sebastian Szekely | Medium | Before authentication integration |
| Q-007 | Which privacy/audit/regional requirements apply? | Sebastian Szekely | High | Before production promotion |
| Q-008 | Is the carrier scenario representative? | Sebastian Szekely | Medium | Before production promotion |

## 11. Technical Architecture

Angular hosts sender and public-recipient experiences. Spring Boot owns Share access decisions, lifecycle transitions and grant issuance; PostgreSQL stores Share, file, Archive and Download Attempt metadata. R2 stores only private originals and archives. ClamAV and ZIP processing run asynchronously. Caddy terminates demo HTTPS; Keycloak authenticates Senders only.

`Sender → Angular → API → PostgreSQL/R2`; `API/worker → ClamAV`; `ZIP worker → R2`; `Recipient → Caddy → public API → short-lived R2 grant`.

## 12. Risks & Mitigation

| Risk | Likelihood | Impact | Mitigation | Owner |
|---|---|---|---|---|
| 100 MB/file or 500 MB/Share path exceeds a component limit | Medium | High | Enforce both limits; run boundary/over-limit spike before commitment. | Sebastian Szekely |
| Cleanup leaves objects beyond six hours | Medium | High | Idempotent retrying cleanup and outcome records. | Sebastian Szekely |
| Scanner outage | Medium | Medium | Fail closed; define retry and expiry behavior. | Sebastian Szekely |
| Token leak/guessing | Low | High | High-entropy opaque token, HTTPS, optional password, short expiry. | Sebastian Szekely |

## 13. Dependencies

**Upstream**: R2 bucket/credentials, Keycloak realm/client, ClamAV runtime, Caddy configuration, PostgreSQL, resolved max-size/guardrail values.  
**Downstream**: architecture solutioning, ADRs, delivery epics, test strategy and production readiness.  
**External**: Cloudflare R2 S3 API, Keycloak OIDC, ClamAV and Caddy.

## 14. Epic Extraction

| Proposed epic | Requirements |
|---|---|
| Share lifecycle and authenticated Sender API | R-001, R-002, R-010 |
| Private storage and public-download boundary | R-002, R-005, R-006, R-008 |
| Scan and archive processing | R-003, R-004 |
| Retention, audit and operational safety | R-007, R-009 |
| Angular sender and recipient experiences | R-001, R-005, R-006, R-010 |

## 15. Implementation Notes

1. Prove scoped R2 transfer, scan outcome persistence, ZIP generation, selected size limit and cleanup.
2. Build authenticated Share lifecycle and private storage integration.
3. Build public recipient route, password check, observability and security/expiry tests.
4. Validate with representative users before production promotion.

## 16. Approval & Sign-Off

| Role | Name | Date | Status |
|---|---|---|---|
| Product Manager | Sebastian Szekely | 2026-10-22 | Approved |
| Tech Lead | Sebastian Szekely | 2026-10-22 | Approved |
| UX Lead | Sebastian Szekely | 2026-10-22 | Approved |
| Stakeholder | Sebastian Szekely | 2026-10-22 | Approved |

## 17. Revision History

| Version | Date | Author | Change |
|---|---|---|---|
| 1.0 | 2026-10-22 | Explore Agent | Initial Fast Lane PRD; all four approval groups accepted. |

## 18. Related Documentation

- `explore/explore-external-document-sharing/context.md`
- `explore/explore-external-document-sharing/domain-analysis.md`
- `explore/domain/external-document-sharing-domain.md`
- `explore/explore-external-document-sharing/technical-feasibility.md`
- `explore/explore-external-document-sharing/architecture-context.md`
- `explore/explore-external-document-sharing/risks.md`
- `explore/explore-external-document-sharing/discovery.md`
- `explore/explore-external-document-sharing/test-strategy.md`

