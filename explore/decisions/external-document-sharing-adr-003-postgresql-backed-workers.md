# ADR-003: PostgreSQL-backed scheduled workers

**Status:** Accepted  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

Scan, archive, and cleanup work must survive API restarts without adding a broker for the local demo. **Affects:** HLD Sections 5–8, 10.

## Decision

Persist job records in PostgreSQL and process them with scheduled Spring Boot workers using idempotent terminal state transitions.

## Options Considered

### Option A: PostgreSQL job records and scheduled workers
- **Pros:** Uses the committed database; durable and inspectable; no new runtime service.
- **Cons:** Requires polling, locking, and retry design.
- **Selected because:** It is proportionate to the demo.

### Option B: In-memory asynchronous execution
- **Pros:** Minimal code initially.
- **Cons:** Loses work on restart and cannot provide durable audit/retry behavior.
- **Rejected because:** It conflicts with scan and cleanup safety requirements.

## Consequences

### Positive
- Lifecycle outcomes and attempts are persistent and observable.
- A broker can be introduced later behind worker contracts.

### Negative
- Governing implementation must define contention and stale-job recovery.
- Long polling may become a bottleneck at production scale.

### Risks
- Duplicate execution is possible; idempotency keys and terminal states mitigate it.

## Related Decisions

- DEC-010 in the decision log.
- [HLD](../hlds/external-document-sharing-hld.md) §§6–8.
