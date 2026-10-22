---
slug: external-document-sharing
explore_type: Fast Lane
created: 2026-10-22
status: VALIDATED
steps_completed: [ingest-existing-docs, extract-drivers]
source_documents: 2
evidence_coverage: { OBS: 17, INF: 2, ASM: 6 }
---

# Architecture Context: SnapSendS3 external document sharing

## 1. Existing Architecture Baseline

**Greenfield — no existing application, deployment manifests, diagrams, APIs, or runbooks were supplied.** `[OBS: discovery intake]`

The target local demo uses Angular/TypeScript, Java 21/Spring Boot, PostgreSQL, private Cloudflare R2, Keycloak, ClamAV, a Java ZIP worker, and Caddy. `[OBS: context.md]` No technical debt or prior ADRs are known. `[OBS: discovery intake]`

## 2. Landscape Assessment

Skipped for Fast Lane. No deployment environment, SLA, production topology, CI/CD, monitoring, browser matrix, or regulatory policy has been supplied. `[OBS: technical-feasibility.md]`

## 3. Architecture Drivers

| Rank | Driver | Type | Impact | Uncertainty | Tension With | Source | Evidence |
|---:|---|---|---|---|---|---|---|
| 1 | Keep file objects private while enabling external download. | Security | High | Medium | Anonymous recipient access | context.md | OBS |
| 2 | Do not make a file downloadable before ClamAV reports clean. | Functional/security | High | Medium | Fast perceived availability | context.md | OBS |
| 3 | Remove share data and objects after six hours. | Lifecycle | High | Medium | Recipient completion window | context.md | OBS |
| 4 | Support the proposed 10 GB upload ceiling. | Performance | High | High | Scan/archive capacity | technical-feasibility.md | ASM |
| 5 | Download 2+ files as a single ZIP archive. | Functional | High | Medium | Temporary storage and processing | context.md | OBS |
| 6 | Permit anonymous recipient access with optional password. | Security/usability | High | Low | Abuse protection | context.md | OBS |
| 7 | Integrate Keycloak, R2, ClamAV, and Caddy. | Platform | High | Medium | Local-only deployment details | context.md | OBS |

**Functional priorities:** MUST: private external sharing, clean-scan gate, expiry cleanup, multi-file archive, Keycloak sender identity. SHOULD: proposed 10 GB support. COULD: future audit/analytics expansion. `[OBS/ASM: context.md, technical-feasibility.md]`

**Quality attributes:** security requires HTTPS-only public access and no public R2 objects; accessibility requires keyboard-accessible flows and visible status; measurable performance, availability, and scale targets remain open. `[OBS: technical-feasibility.md]`

**PoC candidates:** R2 scoped-transfer flow, end-to-end 10 GB processing, ClamAV timeout/failure handling, ZIP generation without unsafe local duplication, and six-hour cleanup. `[ASM: technical-feasibility.md]`

## 4. Domain Model Sketch

Skipped for Fast Lane; the context baseline records Sender, Share, Share File, Download Attempt, and Archive. Detailed bounded contexts and aggregate state machines are deferred to solutioning. `[OBS: context.md]`

## 5. Constraints Register

| # | Constraint | Type | Classification | Source | Evidence |
|---:|---|---|---|---|---|
| 1 | Java 21, Spring Boot, Angular, PostgreSQL | Technology | Hard | context.md | OBS |
| 2 | Private Cloudflare R2 objects | Platform/security | Hard | context.md | OBS |
| 3 | Keycloak-authenticated senders; anonymous recipients | Security | Hard | context.md | OBS |
| 4 | ClamAV clean result before download | Security | Hard | context.md | OBS |
| 5 | Six-hour retention | Lifecycle | Hard | context.md | OBS |
| 6 | ZIP for 2+ files | Functional | Hard | context.md | OBS |
| 7 | Caddy as demo edge | Platform | Soft | context.md | OBS |
| 8 | 10 GB upload limit | Performance | Soft | technical-feasibility.md | ASM |
| 9 | Same-session local demo exploration | Delivery | Hard | explore-bundle.md | OBS |

## 6. Open Questions

| # | Question | Owner | Priority | Impact if Unresolved | Source Step |
|---:|---|---|---|---|---|
| 1 | Can private R2 transfers meet the scoped access flow? | Sebastian Szekely | High | Storage design blocked | Feasibility |
| 2 | How are ClamAV failures/timeouts persisted and shown? | Sebastian Szekely | High | Unsafe or ambiguous availability | Feasibility |
| 3 | Can ZIP generation safely handle proposed 10 GB shares? | Sebastian Szekely | High | Multi-file requirement blocked | Feasibility |
| 4 | Where will Caddy, API, Keycloak, and credentials run? | Sebastian Szekely | High | Public HTTPS boundary unknown | Context |
| 5 | What privacy/audit obligations apply in production? | Sebastian Szekely | High | Production promotion blocked | Context |

## Solutioning Readiness Summary

- [x] Greenfield baseline and source inventory
- [x] Ranked architecture drivers
- [x] Constraints register
- [ ] Detailed domain model deferred by Fast Lane
- [ ] Deployment, volume, and compliance inputs remain open

Solutioning must address private object access, scan gating, expiry cleanup, archive generation, and the 10 GB feasibility spikes before any Govern handoff. `[INF: ranked drivers]`
