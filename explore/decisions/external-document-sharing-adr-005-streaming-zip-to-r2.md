# ADR-005: Stream multi-file ZIP archives to R2

**Status:** Accepted — conditional on BLK-004  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

Two or more clean files must be delivered as one archive without relying on local-disk staging. **Affects:** HLD Sections 6, 7, 9.

## Decision

The archive worker streams clean private source objects into a ZIP and streams the resulting archive to private R2 storage.

## Options Considered

### Option A: Stream ZIP to R2
- **Pros:** Avoids local disk accumulation; retains private object boundary.
- **Cons:** Requires robust streaming/error handling and size validation.
- **Selected because:** It matches the selected capacity safety posture.

### Option B: Build ZIP on local disk
- **Pros:** Simpler implementation and retry inspection.
- **Cons:** Creates disk-capacity, cleanup, and sensitive temporary-file risks.
- **Rejected because:** It violates the approved no-local-disk-first direction.

## Consequences

### Positive
- The host does not retain complete archive copies.
- A ready Share has a single download object.

### Negative
- Partial archive failure must be cleaned up and never exposed.
- Selected limits require boundary proof.

### Risks
- BLK-004 covers the capacity/configuration uncertainty.

## Related Decisions

- DEC-013 in the decision log; BLK-004.
- [HLD](../hlds/external-document-sharing-hld.md) §§6–7.
