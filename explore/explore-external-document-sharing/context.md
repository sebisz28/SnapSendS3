# Context: SnapSendS3 external document sharing

**Signal:** `signal/signals/20261022-external-document-sharing.md`  
**Phase:** SP1 — Context Capture & Signal Enrichment  
**Created:** 2026-10-22  
**Status:** Complete

---

## Problem Statement

Authenticated warehouse users may need to send operational files to external recipients without relying on personal sharing tools. The illustrative current route is email attachments followed by personal sharing for larger files; no production requester or measured workflow has been confirmed.

The local prototype must test a controlled share flow with a six-hour lifetime, malware-scan gate, optional password, and archive download for multi-file shares.

**Root cause:** no application-owned path has been confirmed for controlled external sharing.

**Impact:** users may use ungoverned workarounds; actual operational impact is unmeasured.

**Desired outcome:** an authenticated sender can create an externally reachable, scan-gated share that expires after six hours and archives multi-file downloads.

---

## Scope & Boundaries

### In Scope

**Core Platform**:

- ✅ Authenticated share creation with one or more files.
- ✅ Public recipient download through an expiring link and optional password.
- ✅ Share lifecycle, metadata, scanning, archive processing, and audit state.

**Pages & Features**:

- ✅ Angular sender upload/share flow.
- ✅ Angular recipient download flow.

**Infrastructure**:

- ✅ Spring Boot API, PostgreSQL metadata, private Cloudflare R2 objects, Caddy demo edge, and Keycloak sender identity.
- ✅ Asynchronous ClamAV scanning and Java ZIP worker.

### Out of Scope

- ❌ Production deployment or changes to an existing application repository — no such repository was supplied.
- ❌ Notifications, billing, analytics, branded portal, or identity-management work beyond Keycloak integration.
- ❌ Final legal or regulatory policy — remains an open validation item.

### Boundary Conditions

**Integration boundaries**:

- SnapSendS3 reads authenticated sender identity from Keycloak.
- SnapSendS3 reads and writes private file objects in Cloudflare R2 through S3-compatible APIs.
- SnapSendS3 invokes ClamAV asynchronously and uses Caddy only as the local HTTPS edge.

**Ownership boundaries**:

- SnapSendS3 owns share access decisions, lifecycle metadata, and audit records.
- R2 owns object bytes; objects are never public.
- ClamAV and Caddy are externally provided runtime components.

---

## Domain Model

### Core Entities

| Entity | Description | Key Attributes | Relationships |
|---|---|---|---|
| **Sender** | Existing authenticated creator. | `senderId`, `displayName` | Creates Shares. |
| **Share** | Expiring external-access grant. | `shareId`, `publicToken`, `expiresAt`, `passwordHash?`, `status` | Owns Share Files and Download Attempts. |
| **Share File** | File belonging to a Share. | `fileId`, `originalName`, `sizeBytes`, `objectKey`, `scanStatus` | Stored privately in R2. |
| **Download Attempt** | Auditable access attempt. | `attemptId`, `occurredAt`, `outcome`, `clientIp?` | Belongs to Share. |
| **Archive** | Derived multi-file download. | `format`, `status`, `objectKey?` | Optional per Share. |

### Entity Relationships

```text
[Sender] (1) ──< (many) [Share] ──< (many) [Share File]
                           └──< (many) [Download Attempt]
                           └── (0..1) [Archive]
```

---

## System Map

### Integration Architecture

| Component | Role | Technology | Integrates With | Data Flow |
|---|---|---|---|---|
| **Web app** | Sender and recipient flows | Angular / TypeScript | API | Requests shares and public metadata. |
| **API** | Access and lifecycle orchestration | Java 21 / Spring Boot | PostgreSQL, R2, Keycloak, ClamAV | Persists metadata and issues scoped transfer grants. |
| **PostgreSQL** | Metadata and audit state | PostgreSQL | API | Stores share lifecycle. |
| **Object store** | Private bytes | Cloudflare R2 | API / worker | Stores files and derived archives. |
| **Scanner** | Async malware validation | ClamAV | API / worker | Produces clean, infected, or failed result. |
| **Archive worker** | Multi-file ZIP generation | Spring Boot / Java ZIP | R2, PostgreSQL | Builds archive only for approved files. |
| **HTTPS edge** | Demo public route | Caddy | Web app / API | Terminates HTTPS. |
| **Identity provider** | Sender authentication | Keycloak | API | Supplies sender identity. |

### Data Flow Diagrams

```text
Sender → Angular → Spring Boot API → PostgreSQL
                           ├─ scoped transfer URL → private R2
                           ├─ scan request → ClamAV
                           └─ archive request → ZIP worker → private R2

Recipient → Caddy HTTPS edge → public share route → verification → short-lived download URL
```

---

## Technical Constraints

| Constraint | Source | Status | Impact | Notes |
|---|---|---:|---:|---|
| Java 21 / Spring Boot / PostgreSQL / Angular | Project decision | ✅ | High | Application baseline. |
| Private Cloudflare R2 | Project decision | ✅ | High | No public objects. |
| Keycloak sender authentication | Project decision | ✅ | High | Recipient is anonymous. |
| ClamAV scan gate | Project decision | ✅ | High | No download before clean scan. |
| Java ZIP worker | Project decision | ✅ | High | Required for 2+ files. |
| Six-hour retention | Product requirement | ✅ | High | Share and files expire. |
| Caddy HTTPS edge | Demo default | ⚠️ | Medium | Production fit unverified. |
| 10 GB maximum | Project assumption | ⚠️ | High | Validate all transfer and processing layers. |
| Wider audit/compliance policy | Not supplied | ❌ | High | Privacy and regional rules unknown. |

**Status Legend:** ✅ confirmed · ⚠️ assumed · ❌ open

---

## Stakeholder Map & RACI

### Stakeholders

| Stakeholder | Organization | Role | Interest | Influence | Engagement Strategy |
|---|---|---|---|---|---|
| **Sebastian Szekely** | Local dummy project | PM, Architect, Lead Engineer, sponsor | Secure feasible demo | High | Approves all local decisions. |
| **Future senders** | TBD | Authenticated users | Controlled sharing | Medium | Validate before production. |
| **Future recipients** | TBD | Anonymous download users | Reliable simple download | Medium | Validate before production. |

### RACI Matrix

| Activity | Sebastian | Future senders | Future recipients |
|---|---|---|---|
| Context, feasibility, architecture, PRD, and Govern readiness | A/R | C | C |
| Production workflow validation | A/R | C | C |

**Legend:** R = Responsible, A = Accountable, C = Consulted, I = Informed.

---

## Governance Framework

### Decision Authority

| Decision Type | Decision Maker | Consulted | Informed | Escalation Path |
|---|---|---|---|---|
| Architecture and security | Sebastian Szekely — Architect | None for demo | Future users | Pause and revise design. |
| Product scope | Sebastian Szekely — PM | None for demo | Future users | Defer item. |
| Delivery and resourcing | Sebastian Szekely — Lead Engineer | None | None | Reduce scope or stop. |
| Production promotion | Sebastian Szekely | Future users | None | Do not promote without validation. |

### Approval Gates

| Gate | Approver | Criteria | Fallback |
|---|---|---|---|
| Context baseline | Sebastian Szekely | Scope, constraints, assumptions recorded | Keep unknowns open. |
| Feasibility / architecture | Sebastian Szekely | Security boundary assessed | Do not enter Govern if infeasible. |
| PRD-ready | Sebastian Szekely | Requirements/risk traceability | Revise PRD. |
| Govern readiness | Sebastian Szekely | Artifacts complete | Remain in Explore. |

---

## Assumptions Catalogue

| # | Assumption | Open Question | Risk if Wrong | Owner | Closes In |
|---|---|---|---|---|---|
| 1 | Recipients need anonymous secure links. | Which real workflow confirms this? | No adoption value. | Sebastian Szekely | A1 |
| 2 | Any type up to 10 GB is appropriate. | Which types/sizes are acceptable? | Unsafe or impractical processing. | Sebastian Szekely | A5 |
| 3 | Carrier scenario represents a useful workflow. | Can a real party confirm it? | Non-representative scope. | Sebastian Szekely | A1 |
| 4 | Email/personal sharing is current workaround. | What is actually used today? | Incorrect value proposition. | Sebastian Szekely | A1 |
| 5 | R2 plus scoped URLs and HTTPS fit deployment. | Can target topology support this? | Infeasible external delivery. | Sebastian Szekely | A5 |

---

## Gaps Catalogue

| # | Gap | Why It Matters | Question for Stakeholder | Stakeholder |
|---|---|---|---|---|
| 1 | Real workflow | Confirms demand. | Who sends which files, how often, and why? | Sebastian Szekely |
| 2 | Recipient-control policy | Determines access design. | Is anonymous access with optional password sufficient? | Sebastian Szekely |
| 3 | Compliance policy | Determines logging and data handling. | Which privacy, audit, or regional rules apply? | Sebastian Szekely |
| 4 | Deployment topology | Determines exposure boundary. | Where will Caddy, API, Keycloak, and R2 credentials run? | Sebastian Szekely |

---

## Context Summary

### One-Page Baseline

**Problem:** users need controlled external sharing without personal tools.  
**Solution:** Spring Boot/Angular sharing with Keycloak senders, private R2, ClamAV, six-hour expiry, optional password, and Caddy HTTPS.  
**Scope:** upload/share and anonymous download flows; **Out:** production deployment, notifications, billing, and account management.  
**Actors:** Sebastian Szekely owns demo decisions; senders authenticate; recipients use a link and optional password.  
**Constraints:** private R2, six-hour retention, scan gate, ZIP for 2+ files, proposed 10 GB maximum.  
**Assumptions:** five open assumptions across demand, workflow, file policy, and deployment.  
**Next:** technical feasibility and architecture context.

---

## Enrichment Log

| Date | Trigger | Change |
|---|---|---|
| 2026-10-22 | SP1 completion | Initial context baseline created from confirmed discovery inputs. |
| 2026-10-22 | PRD approval | Linked final requirements to the existing problem statement; no scope change. |

---

**Last Updated:** 2026-10-22  
**Updated By:** Explore Agent
