# Decision Log: HLD · SnapSendS3 External Document Sharing

**Engagement start**: 2026-10-22  
**Last updated**: 2026-10-22  
**Total decisions**: 15

## Decision Index

| ID | Date | Step | Decision | Driver | Status | Evidence |
|---|---|---|---|---|---|---|
| DEC-001 | 2026-10-22 | B.1a | Proceed with conditional Fast Lane architecture | Architect | Active | VALIDATED |
| DEC-002 | 2026-10-22 | B.1.2 | Approve artifact truth hierarchy | Architect | Active | VALIDATED |
| DEC-003 | 2026-10-22 | B.1.2 | Approve ownership boundaries | Architect | Active | VALIDATED |
| DEC-004 | 2026-10-22 | B.1.2 | Classify external integration contracts | Architect | Active | VALIDATED |
| DEC-005 | 2026-10-22 | B.1.2 | Do not infer platform conventions | Architect | Active | VALIDATED |
| DEC-006 | 2026-10-22 | B.1.2 | Set proposed local-demo operating constraints | Architect | Active | VALIDATED |
| DEC-007 | 2026-10-22 | B.1.2 | Validate boundary map | Architect | Active | VALIDATED |
| DEC-008 | 2026-10-22 | B.1.3 | Use modular monolith | Architect | Active | VALIDATED |
| DEC-009 | 2026-10-22 | B.1.3 | Use direct scoped R2 transfers | Architect | Active | VALIDATED |
| DEC-010 | 2026-10-22 | B.1.3 | Use PostgreSQL-backed job records | Architect | Active | VALIDATED |
| DEC-011 | 2026-10-22 | B.1.3 | Use dual expiry cleanup | Architect | Active | VALIDATED |
| DEC-012 | 2026-10-22 | B.1.3 | Integrate with clamd | Architect | Active | VALIDATED |
| DEC-013 | 2026-10-22 | B.1.3 | Stream ZIP construction to R2 | Architect | Active | VALIDATED |
| DEC-014 | 2026-10-22 | B.1 | Conditionally approve design direction | Architect | Active | VALIDATED |
| DEC-015 | 2026-10-22 | B.2–B.4 | Promote HLD and six ADRs under autonomous completion authority | Architect | Active | VALIDATED |

## Decision Details

### DEC-001 — Proceed with conditional Fast Lane architecture

**What was decided**: Architecture work proceeds with the approved PRD and seven open risks as mandatory design constraints.  
**Why**: The user explicitly confirmed conditional architecture readiness.  
**Alternatives rejected**: stop until every feasibility spike completes (rejected for local-demo progress); accept/close risks without evidence (rejected).  
**Impact**: Every architecture artifact must retain the risks and no production-readiness claim is allowed.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-008 to DEC-013 — Select recommended design-sketch options

**What was decided**: Use a modular monolith, direct scoped R2 transfers, PostgreSQL-backed durable jobs, application cleanup plus R2 lifecycle, `clamd` streaming, and streaming ZIP upload to R2.  
**Why**: The architect selected all recommended options for the local demo.  
**Alternatives rejected**: separate/microservice topology; API-proxied file transfer; in-memory jobs/message broker; single cleanup mechanism; process/vendor scanning; local-disk archive construction.  
**Impact**: HLD must use these choices and preserve their validation caveats.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.  
**Caveat**: DEC-009 and DEC-012 cannot be implemented until BLK-001 and BLK-003 are resolved; DEC-008–013 do not close feasibility risks.

### DEC-014 — Conditionally approve design direction

**What was decided**: The Fast Lane design sketch is accepted as the architecture baseline, subject to unresolved external contracts and feasibility spikes.  
**Why**: The architect confirmed ARCH-B1c.  
**Alternatives rejected**: treating design direction as final production architecture; silently closing blockers.  
**Impact**: Proceed under Fast Lane adaptation; retain BLK-001 through BLK-004 and all open questions.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-007 — Validate boundary map

**What was decided**: The truth hierarchy, ownership boundaries, dependency classifications and open-question handling in the boundary map are accepted.  
**Why**: The architect confirmed ARCH-B1b.  
**Alternatives rejected**: deferring ownership validation; treating open external contracts as confirmed.  
**Impact**: The design sketch may use these boundaries but must retain BLK-001 through BLK-004.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-006 — Set proposed local-demo operating constraints

**What was decided**: Use 100 MB per file and 500 MB per Share for the feasibility spike; use a 60-second ClamAV timeout, three transient retries, INFECTED/FAILED fail-closed states; propose `roles:dava.sender`; alert at 80% and reject at 100% of configured R2 budgets.  
**Why**: The architect supplied these settings while retaining validation requirements.  
**Alternatives rejected**: retaining the unbounded 10 GB proposal; treating unproven settings as production guarantees.  
**Impact**: Update PRD/risk/blocker records and validate all settings before implementation.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-005 — Do not infer platform conventions

**What was decided**: No internal platform convention is inherited because no adjacent HLD was supplied; only the confirmed local-demo baseline is used.  
**Why**: The architect confirmed the convention baseline.  
**Alternatives rejected**: assuming microservices, a deployment topology, or organizational standards without evidence.  
**Impact**: The design sketch must justify its chosen style and leave production conventions open.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-004 — Classify external integration contracts

**What was decided**: R2, Keycloak and ClamAV contracts are implementation blockers; Caddy capacity configuration is significant but does not stop architecture.  
**Why**: The architect confirmed the dependency classifications and severities.  
**Alternatives rejected**: treating all dependencies as confirmed; marking Caddy capacity as a blocker before a configuration/spike attempt.  
**Impact**: No implementation begins before BLK-001 through BLK-003 are resolved.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-003 — Approve ownership boundaries

**What was decided**: SnapSendS3 owns Share lifecycle/access decisions and worker orchestration; Keycloak owns sender identity, R2 owns bytes, ClamAV owns verdicts, and Caddy owns HTTPS edge routing.  
**Why**: The architect confirmed the ownership matrix.  
**Alternatives rejected**: placing object-access policy or user identity ownership in SnapSendS3; assigning recipient identity management to the product.  
**Impact**: Integration contracts are required for each external boundary.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

### DEC-002 — Approve artifact truth hierarchy

**What was decided**: The PRD, locked domain model, architecture context, and risk register are authoritative for their stated scopes; context and feasibility are directional.  
**Why**: The architect confirmed the artifact inventory and classifications.  
**Alternatives rejected**: treating all documents as equally authoritative; elevating unprovided vendor/compliance material to canonical status.  
**Impact**: Boundary and design artifacts must defer to the canonical sources and label inferences.  
**Driver / evidence**: Sebastian Szekely, Architect — [VALIDATED].  
**Status**: Active.

## Reversals

None.

### DEC-015 — Promote HLD and ADRs

**What was decided**: The approved design sketch is consolidated into the canonical HLD and ADR-001 through ADR-006, all Accepted for conditional local-demo planning.  
**Why**: Sebastian Szekely authorized autonomous Explore completion and recommended choices.  
**Impact**: BLK-001 through BLK-004 remain implementation gates; the ADRs do not remove their evidence requirements.  
**Status**: Active.

