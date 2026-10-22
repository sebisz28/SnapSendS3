# ADR-006: Dual expiry enforcement and cleanup

**Status:** Accepted  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

Share data has a six-hour retention requirement, and cleanup may fail or be delayed. **Affects:** HLD Sections 7–10.

## Decision

The API denies access at `expiresAt`; a retryable cleanup worker deletes application metadata/object references, and R2 lifecycle is configured as deletion defense in depth.

## Options Considered

### Option A: Application expiry plus cleanup worker plus R2 lifecycle
- **Pros:** Access ends even when deletion lags; two independent deletion mechanisms.
- **Cons:** Requires operational reconciliation.
- **Selected because:** It meets the retention safety requirement.

### Option B: R2 lifecycle only
- **Pros:** Minimal application code.
- **Cons:** Does not guarantee immediate application access denial or metadata cleanup.
- **Rejected because:** It cannot satisfy expiry independent of cleanup.

## Consequences

### Positive
- Expiry behavior is deterministic and testable with a controlled clock.
- Cleanup outcomes are visible and retryable.

### Negative
- Temporary metadata/object divergence is possible during retries.
- R2 lifecycle configuration must be maintained.

### Risks
- Failed cleanup is tracked in the risk register and must not restore access.

## Related Decisions

- DEC-011 in the decision log.
- [HLD](../hlds/external-document-sharing-hld.md) §§7, 9, 10.
