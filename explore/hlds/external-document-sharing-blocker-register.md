# Blocker Register: HLD · SnapSendS3 External Document Sharing

**Engagement start**: 2026-10-22  
**Last updated**: 2026-10-22  
**Open blockers**: 4  
**Resolved blockers**: 0

## Summary

| Severity | Open | Resolved |
|---|---:|---:|
| BLOCKER | 3 | 0 |
| SIGNIFICANT | 1 | 0 |
| MINOR | 0 | 0 |

## Open Blockers

### BLK-001 — R2 scoped-transfer contract

**Category**: Missing contract · **Severity**: BLOCKER · **Status**: Open  
**Impact**: Private direct upload/download cannot be safely implemented without endpoint, credentials and grant-expiry semantics.  
**Owner**: Sebastian Szekely · **Resolution**: provision demo bucket/credentials and prove scoped upload/download spike.  
**Related**: DEC-003, T1, PRD Q-002.

### BLK-002 — Keycloak sender-authorization contract

**Category**: Missing contract · **Severity**: BLOCKER · **Status**: Open  
**Impact**: Sender authorization cannot be implemented until realm/client, issuer/audience and claim mapping are verified.  
**Owner**: Sebastian Szekely · **Resolution**: verify that the proposed `roles` claim contains `dava.sender` in local Keycloak configuration.  
**Related**: DEC-003, PRD Q-006.

### BLK-003 — ClamAV invocation and failure contract

**Category**: Implementation gap · **Severity**: BLOCKER · **Status**: Open  
**Impact**: The scan gate cannot safely transition to Ready without a proven invocation interface and terminal behavior.  
**Owner**: Sebastian Szekely · **Resolution**: prove the proposed 60-second timeout, three retries, INFECTED/FAILED states and fail-closed state machine by integration test.  
**Related**: DEC-003, T2, PRD Q-003.

### BLK-004 — Caddy public-route capacity configuration

**Category**: Unresolved dependency · **Severity**: SIGNIFICANT · **Status**: Open  
**Impact**: The 100 MB/file and 500 MB/Share path may fail due to body limits/timeouts, but architecture can proceed with it as a tracked constraint.  
**Owner**: Sebastian Szekely · **Resolution**: define local route, TLS, body limit and timeout configuration; validate boundary and over-limit cases in the size spike.  
**Related**: T1, PRD Q-001.

## Resolved Blockers

None.
