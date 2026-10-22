# Boundary Map: SnapSendS3 External Document Sharing

**Status**: Validated — ARCH-B1b confirmed  
**Architect**: Sebastian Szekely  
**Date**: 2026-10-22

## 1. Truth Hierarchy

| Artifact | Classification | Rationale | Evidence |
|---|---|---|---|
| Approved PRD | Canonical | Requirements, scope and acceptance baseline. | [OBS: PRD] |
| Locked domain model / domain analysis | Canonical | Terms, entities, lifecycle and invariants. | [OBS: domain analysis] |
| Architecture context | Canonical | Ranked drivers and constraints. | [OBS: architecture context] |
| Risk register | Canonical for risk treatment | Open risks are mandatory design constraints. | [OBS: risk approval] |
| Context baseline | Directional | Contains recorded assumptions. | [OBS: context] |
| Technical feasibility | Directional | Identifies feasibility work, not results. | [OBS: feasibility] |
| Vendor documentation | Directional if used | Cannot override product ownership. | [ASM: no attached contract] |

## 2. Ownership Matrix

| Domain / component | Owns | Consumes | Produces | Does not own | Evidence |
|---|---|---|---|---|---|
| SnapSendS3 Share lifecycle | Share metadata, expiry, password check, access decision, audit outcome | Keycloak subject, worker outcomes | Public Share state and download authorization | Keycloak users, R2 policy, recipient identity | [OBS: PRD/domain] |
| Upload orchestration | Upload authorization and size validation | Sender identity, limits | Scoped upload permission, finalized Share File | Object bytes or public delivery | [OBS: PRD] |
| Scan worker | Scan-state transitions | Private Share File, ClamAV result | Clean, INFECTED, FAILED outcome | ClamAV detection logic | [OBS: domain/Q-003] |
| Archive worker | Archive metadata/status | Clean Share Files | Private ZIP archive | Original file ownership | [OBS: domain] |
| Cleanup worker | Deletion outcome | Expired metadata/object keys | Retryable cleanup state | Share access decision | [OBS: domain/risk] |
| Keycloak | Sender identity/authentication | Realm/client config | Subject and roles claim | SnapSendS3 authorization rules | [OBS: context] |
| Cloudflare R2 | Object bytes/object enforcement | S3 scoped request | Private originals/archives | Share access decision | [OBS: context] |
| ClamAV | Malware verdict | File stream/path | Scan result | Share lifecycle | [OBS: context] |
| Caddy | HTTPS edge routing | Public request | TLS-terminated route | Application authorization | [OBS: context] |

## 3. Upstream Dependencies

| Dependency | Source | Type | Status | Blocker | Evidence |
|---|---|---|---|---|---|
| Sender issuer/audience and `roles:dava.sender` mapping | Keycloak | OIDC/JWT | Needs creation | BLK-002 | [ASM: proposed role] |
| Private object-specific upload/download grants | Cloudflare R2 | S3-compatible API | Provisional | BLK-001 | [ASM: spike required] |
| 60-second timeout, three retries, INFECTED/FAILED contract | ClamAV | Service/protocol | Needs creation | BLK-003 | [ASM: proposed contract] |
| TLS, body limits, timeouts for 100 MB/file, 500 MB/Share | Caddy | Reverse proxy | Provisional | BLK-004 | [ASM: spike required] |
| Share metadata persistence | PostgreSQL | Internal data | Needs creation | none | [OBS: baseline] |
| ZIP packaging | Java standard library | In-process | Available; capacity unproven | T1 | [OBS/ASM: baseline/risk] |

## 4. Platform Conventions

| Convention | Recommendation | Reason | Evidence |
|---|---|---|---|
| Service style | Do not inherit an absent internal style; justify a minimal local-demo design in sketch. | No adjacent HLD exists. | [OBS] |
| API/versioning | Define minimum API surface from PRD only. | No organizational standard supplied. | [ASM] |
| Migrations | Select a Spring-compatible tool in implementation design. | No standard supplied. | [ASM] |
| Observability | Structured, secret-safe lifecycle events. | Required by NFR-007/008. | [OBS: PRD] |
| Deployment | Caddy local-demo edge only; no production topology claim. | Production topology is unknown. | [OBS: context] |

## 5. Open Questions

| ID | Question | Impact | Blocking | Resolution |
|---|---|---|---|---|
| Q-001 | Do 100 MB/file and 500 MB/Share pass upload, scan and ZIP boundary tests? | Capacity and archive design | Yes | BLK-004/T1 spike. |
| Q-002 | Can R2 scoped transfers meet private access design? | Storage boundary | Yes | BLK-001 spike. |
| Q-003 | Does proposed ClamAV failure contract work? | Scan safety | Yes | BLK-003 integration test. |
| Q-004 | Does Keycloak emit `roles:dava.sender`? | Sender authorization | Yes | BLK-002 configuration check. |
| Q-005 | What absolute budgets back 80% alert/100% reject? | Cost guardrail | No | Configure before demo. |
| Q-006 | What production compliance obligations apply? | Production readiness | No for demo / Yes for production | Compliance review. |
| Q-007 | Is the carrier use case representative? | Product fit | No for demo / Yes for production | User validation. |
| Q-008 | What production topology/SLA/monitoring applies? | Production architecture | No for demo / Yes for production | Production design. |

## Validation Summary

All available input artifacts are classified; nine ownership boundaries are explicit; six dependencies have a contract status; four unverified external contracts are linked to blockers; no adjacent platform convention is invented; and eight open questions are retained with impact and resolution path. Every statement is marked `[OBS]` or `[ASM]`.
