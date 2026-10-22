# Technical Feasibility: SnapSendS3 external document sharing

**Project:** SnapSendS3  
**Created:** 2026-10-22  
**Last Updated:** 2026-10-22  
**Status:** Engineering Review

## 1. System Context

| Platform / dependency | Purpose | Version | Criticality | Constraint |
|---|---|---|---|---|
| Angular web app | Sender and recipient UI | TBD | High | Build/styling choices are not set. |
| Spring Boot API / worker | Share lifecycle, scan and archive orchestration | Java 21 | Critical | API and worker capacity for 10 GB is unvalidated. |
| PostgreSQL | Metadata and audit data | TBD | Critical | No schema or scale data exists. |
| Cloudflare R2 | Private object storage | S3-compatible | Critical | Presigned-transfer and lifecycle behaviour need a spike. |
| Keycloak | Sender authentication | TBD | Critical | Existing realm/client configuration is unknown. |
| ClamAV | Malware scan | TBD | Critical | Async invocation, timeout, and throughput are unknown. |
| Caddy | Demo HTTPS edge | TBD | High | Production topology is unconfirmed. |

Integrations are API-to-R2 (S3-compatible), API-to-Keycloak (OIDC/OAuth details TBD), and API/worker-to-ClamAV (protocol/TBD). Development is local only; staging, production, CI/CD, monitoring, rollback, and SLAs are not supplied.

## 2. Data Realities

| Data type | Source | Quality | Latency | Owner | Access |
|---|---|---|---|---|---|
| Sender identity | Keycloak | Unknown | Request-time | Sebastian Szekely | API token/session validation |
| Share/file metadata | PostgreSQL | New | Request-time | SnapSendS3 | API only |
| File bytes | R2 | New | Transfer-time | SnapSendS3 | Scoped S3 URLs |
| Scan outcome | ClamAV | New | Async | SnapSendS3 | Worker/API callback or poll |

Data gaps: no confirmed file mix, volume, scan time, recipient behavior, or retention/audit policy. UX must therefore show explicit upload, scanning, archive, expiry, and failure states rather than promise instant availability.

## 3. Technical Constraints

- **MUST:** only authenticated Keycloak users create shares; recipients remain anonymous and may supply a password only when configured.
- **MUST:** objects remain private; all public access is HTTPS and limited by expiry plus a short-lived download grant.
- **MUST:** clean ClamAV result precedes download availability; two or more files produce one ZIP archive.
- **MUST:** delete/expire share metadata and objects after six hours.
- **SHOULD:** support a 10 GB upload maximum, subject to R2, reverse-proxy, browser, scanner, and ZIP feasibility testing.
- Offline use is not required. Device/browser support, numeric performance targets, R2 limits, OIDC configuration, and production rate limits are open.

## 4. Technical Opportunities

| Opportunity | Benefit | Feasibility | Effort | Priority |
|---|---|---|---|---|
| Direct scoped R2 transfers | Avoid routing large bytes through API | Medium | Medium | High |
| Async scan and archive status | Clear, safe user progress model | High | Medium | High |
| Lifecycle cleanup | Limits retained data automatically | Medium | Small | High |
| Minimal audit events | Diagnoses sharing outcomes | High | Small | Medium |

AI, personalization, analytics platforms, and real-time channels are intentionally out of scope for this Fast Lane.

## 5. Technical Risks and Unknowns

| Item | Impact | Resolution | Owner |
|---|---|---|---|
| 10 GB path may exceed proxy, browser, scan, or archive capacity | High | Local spike through Caddy, R2-compatible store, ClamAV, and ZIP flow | Sebastian Szekely |
| R2 presigned uploads/downloads may not fit intended access flow | High | SDK spike using private bucket and scoped URLs | Sebastian Szekely |
| ClamAV integration may be slow or unavailable | High | Define timeout, quarantine, retry, and unavailable state | Sebastian Szekely |
| Six-hour cleanup may leave objects/archives behind | High | Test application cleanup and R2 lifecycle configuration | Sebastian Szekely |
| Compliance/audit obligations are unknown | High | Keep production promotion blocked pending policy input | Sebastian Szekely |

No legacy technical debt is known because no existing application repository was provided. Scalability thresholds are unknown; avoid claiming production scale.

## 6. Design Constraints and Guardrails

**Must:** never expose a raw R2 object URL; do not show a downloadable state before scan success; show expiry; show password prompt only when required; show archive preparation/failure; make all actions keyboard accessible.

**Should:** show upload progress and recoverable failure states; make status refresh explicit or safely polled; keep recipient page independent of authenticated sender session.

**Guardrails:** do not make optimistic availability claims; treat files and passwords as sensitive; use semantic HTML, visible focus, WCAG AA contrast, and responsive layouts. Performance targets, browser matrix, and page-size limits require measurement rather than invented targets.

## 7. Questions for Engineering

1. **Can the intended R2 SDK flow provide private direct uploads and one-time/short-lived downloads?** High — blocks storage design. Owner: Sebastian Szekely.
2. **Which Caddy request/body limits and timeout settings safely support the proposed 10 GB path?** High — blocks demo upload design. Owner: Sebastian Szekely.
3. **How will ClamAV be invoked and how are timeout, infected, and unavailable outcomes persisted?** High — blocks safe activation. Owner: Sebastian Szekely.
4. **Where will temporary ZIPs be generated and cleaned, and can the process stream without duplicating 10 GB locally?** High — blocks multi-file design. Owner: Sebastian Szekely.
5. **What Keycloak claims identify permitted senders?** Medium — needed for API authorization. Owner: Sebastian Szekely.
6. **Which audit events and retention apply beyond the six-hour file lifetime?** High — blocks production readiness. Owner: Sebastian Szekely.
