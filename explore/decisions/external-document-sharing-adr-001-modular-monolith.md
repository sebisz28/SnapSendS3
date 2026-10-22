# ADR-001: Modular monolith for SnapSendS3

**Status:** Accepted  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

The local demo needs sender/public APIs and three asynchronous worker responsibilities without an established platform topology. **Affects:** HLD Sections 5, 6, 10.

## Decision

Use one Spring Boot deployable with explicit modules for sender access, public access, lifecycle, workers, persistence, and integrations.

## Options Considered

### Option A: Modular monolith
- **Pros:** Lowest operational footprint; transactional lifecycle coordination; clear future extraction seams.
- **Cons:** One deployable and database require strict module discipline.
- **Selected because:** Fits Fast Lane local-demo scope without inventing infrastructure.

### Option B: Separate services and workers
- **Pros:** Independent scaling and deployment.
- **Cons:** Adds service discovery, contract, deployment, and operational work before feasibility is proven.
- **Rejected because:** The workload and production topology are unknown.

## Consequences

### Positive
- A single implementation path can prove end-to-end safety gates.
- Worker lifecycle can use shared PostgreSQL records.

### Negative
- Module boundaries must be enforced in code and review.
- Future scale may require extraction.

### Risks
- Shared deployment could couple failures; keep workers idempotent and isolate module interfaces.

## Related Decisions

- DEC-008 in the decision log.
- [HLD](../hlds/external-document-sharing-hld.md) §§5–6.
