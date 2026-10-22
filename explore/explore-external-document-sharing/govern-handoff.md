# SnapSendS3 Conditional Govern Handoff

**Status:** Approved — conditional handoff  
**Date:** 2026-10-22  
**Approval authority and owner:** Sebastian Szekely

## Purpose

This handoff transfers the approved SnapSendS3 local-demo baseline into Govern planning. It does not authorize implementation until the mandatory feasibility entry criteria below are met.

## Govern Baseline

- [PRD](../prds/external-document-sharing-prd.md) — approved.
- [Design sketch](../hlds/external-document-sharing-design-sketch.md) — authorized conditional architecture baseline; it substitutes for a full HLD by sponsor direction.
- [Decision log](../hlds/external-document-sharing-decision-log.md) — selected implementation approaches.
- [Blocker register](../hlds/external-document-sharing-blocker-register.md) — mandatory feasibility blockers.
- [Test strategy](test-strategy.md) — approved automated test and quality-gate baseline.
- [Risk register](risks.md) — seven open risks; none is accepted or closed.

## Explicit Deferrals

| Deferred item | Reason | Owner | Effect on Govern |
|---|---|---|---|
| Full HLD and ADR set | Sponsor directed the design sketch to be the conditional baseline. | Sebastian Szekely | Planning must cite the design sketch, decision log, and blockers; any material design change requires a new decision record. |
| Epics and delivery-task backlog | Sponsor limited Explore output to the test strategy and handoff. | Sebastian Szekely | Govern must create and select work through `dft task` before execution. |
| DevOps strategy | Sponsor excluded it from this handoff. | Sebastian Szekely | No deployment/promotion plan is approved; local-demo execution only. |
| UX research, wireframes, usability plan, and dedicated accessibility specification | Not requested in the Fast Lane scope. | Sebastian Szekely | Govern retains the PRD flows and automated accessibility requirement; unresolved UX detail must be decided during planning. |

## Mandatory Entry Criteria

| ID | Required evidence before implementation commitment | Owner |
|---|---|---|
| BLK-001 | R2 spike proves a private bucket and scoped, short-lived upload/download transfers. | Sebastian Szekely |
| BLK-002 | Keycloak test/configuration proves the `roles` claim supplies the `dava.sender` authorization convention, or records the approved replacement mapping. | Sebastian Szekely |
| BLK-003 | ClamAV spike proves the 60-second timeout, three transient retries, and fail-closed `INFECTED`/`FAILED` outcomes. | Sebastian Szekely |
| BLK-004 | Capacity spike proves or revises the 100 MB per-file and 500 MB per-Share limits through upload, scanning, ZIP creation, and cleanup. | Sebastian Szekely |

## Guardrails for Govern

1. Keep the R2 bucket private; issue only short-lived, object-specific transfers after backend authorization.
2. Keep anonymous recipients login-free; only the optional password and six-hour expiry govern access.
3. Deliver two or more files as one ZIP; do not expose individual files as a fallback.
4. Fail closed while files are pending, infected, or scanner-failed.
5. Enforce expiry in the application independently of cleanup; retain idempotent cleanup and R2 lifecycle defense.
6. Treat 100 MB/file, 500 MB/Share, and 80% alert/100% reject quota behavior as test-gated demo assumptions, not production commitments.

## Handoff Decision

Sebastian Szekely accepted the listed deferrals on 2026-10-22. Govern may create planning work and feasibility spikes. It must not declare the local demo ready, or begin feature implementation commitment, until BLK-001 through BLK-004 have evidence recorded and the remaining open decisions are reflected in the selected work.
