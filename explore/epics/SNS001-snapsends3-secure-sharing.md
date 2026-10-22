---
id: SNS001
title: SnapSendS3 Secure External Sharing
status: ready-for-govern-conditional
source_prd: explore/prds/external-document-sharing-prd.md
---

# SNS001 — SnapSendS3 Secure External Sharing

## Target Future State

Authenticated Senders create private, six-hour Shares; anonymous Recipients receive only clean, authorized download grants. Two or more clean files arrive as one ZIP.

## Scope

Delivers PRD R-001–R-010 and NFR-001–NFR-009 through the HLD modular-monolith design. It excludes production rollout, recipient identity/revocation/limits, and unvalidated external-contract assumptions.

## Acceptance Criteria

- Sender authorization, limits, private upload, scan gating, archive, public password/expiry checks, cleanup, audit, and status satisfy the mapped PRD requirements.
- All test-strategy gates pass, including 100 MB/file and 500 MB/Share boundaries.
- BLK-001 through BLK-004 have documented passing evidence before demo acceptance.

## Epochs

### Epoch 1 — Feasibility foundation

Prove R2 scoped transfers, Keycloak role mapping, clamd terminal behavior, and Caddy/ZIP capacity. No feature release occurs if any blocker fails.

### Epoch 2 — Secure Share lifecycle

Implement Spring Boot lifecycle/persistence modules, authenticated Sender APIs, size/usage guards, and private upload authorization.

### Epoch 3 — Scan, archive, and retention workers

Implement durable jobs, clamd scan gate, streaming ZIP archive, expiry denial, cleanup retry, lifecycle defense, and secret-safe audit outcomes.

### Epoch 4 — Recipient experience and demo quality

Implement Angular Sender/public Recipient flows, optional password experience, accessibility, E2E/security validation, and Caddy demo configuration.

## Dependencies and Risks

The four blockers in `explore/hlds/external-document-sharing-blocker-register.md` are implementation gates. Seven open risks remain governed by `explore/explore-external-document-sharing/risks.md`.

## References

- [PRD](../prds/external-document-sharing-prd.md)
- [HLD](../hlds/external-document-sharing-hld.md)
- [Test strategy](../explore-external-document-sharing/test-strategy.md)
- [DevOps strategy](../explore-external-document-sharing/devops-strategy.md)
