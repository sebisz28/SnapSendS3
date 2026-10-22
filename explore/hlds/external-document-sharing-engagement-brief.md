# Architecture Engagement Brief: SnapSendS3 External Document Sharing

**Explore type**: Fast Lane — confirmation depth  
**Gate B.1a**: Confirmed by Sebastian Szekely, 2026-10-22  
**Primary sources**: PRD, domain analysis, architecture context, technical feasibility, risk register.

## Scope

Design an architecture baseline for a local SnapSendS3 demo. The system accepts files from Keycloak-authenticated Senders and lets anonymous Recipients retrieve clean, unexpired Shares through public HTTPS links, optionally protected by a password.

## Quality priorities

1. Private R2 objects and HTTPS-only public access.
2. Fail-closed ClamAV scan gate.
3. Six-hour expiry and reliable cleanup.
4. Correct ZIP delivery for multi-file Shares.
5. Clear accessible state transitions.

## Constraints and caveats

Six hard constraints and three soft/open constraints are recorded in `architecture-context.md`. The 10 GB proposal, R2 scoped-transfer viability, scan-failure policy, exact Keycloak claims, deployment topology, and compliance remain unresolved. The seven approved risks are mandatory architecture constraints, not accepted risks.

## Stakeholders and authority

Sebastian Szekely is the local-demo Product Manager, Architect, Lead Engineer, and approval authority. Keycloak owns identity; R2 owns object bytes; SnapSendS3 owns Share access/lifecycle decisions; ClamAV and Caddy are runtime dependencies.

## Success criteria

Architecture must trace to R-001–R-010 and NFR-001–NFR-009, retain the domain terminology, and make unresolved feasibility work visible rather than conceal it.

## Hardening configuration

BASE categories: boundary integrity, cross-view consistency, failure-mode analysis, and contract completeness. Extended focus: threat model, auditability, lifecycle cleanup, and large-object feasibility. No regulatory artifact exists.

