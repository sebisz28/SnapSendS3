# ADR-002: Direct scoped R2 transfers

**Status:** Accepted — conditional on BLK-001  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

Recipients need private object delivery without the API becoming a byte proxy. **Affects:** HLD Sections 5, 7, 9, 11.

## Decision

The backend authorizes access and issues short-lived, object-specific R2 upload/download transfers; the bucket remains private.

## Options Considered

### Option A: Direct scoped R2 transfers
- **Pros:** Avoids application byte relay; preserves private bucket policy; fits 100 MB/500 MB demo limits.
- **Cons:** Requires exact R2 SDK/presign semantics and expiry proof.
- **Selected because:** It is the lowest-overhead private-transfer model.

### Option B: API-proxied transfer
- **Pros:** Central policy enforcement and no client-to-R2 path.
- **Cons:** Adds application bandwidth, timeout, and memory/streaming burden.
- **Rejected because:** It weakens the capacity posture without a proven need.

## Consequences

### Positive
- Object access remains temporary and backend-mediated.
- The API does not expose permanent object locations.

### Negative
- R2 contract validation is a hard implementation prerequisite.
- Transfer TTL and client error behavior must be tested.

### Risks
- BLK-001 may invalidate the approach; retain API proxy as a fallback to assess, not an approved default.

## Related Decisions

- DEC-009 in the decision log; BLK-001.
- [HLD](../hlds/external-document-sharing-hld.md) §§5, 7, 11.
