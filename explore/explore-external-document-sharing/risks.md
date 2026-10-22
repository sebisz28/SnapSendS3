# Risk Register: SnapSendS3 external document sharing

**Signal**: `signal/signals/20261022-external-document-sharing.md`  
**Phase**: SP4 — Risk Assessment  
**Created**: 2026-10-22  
**Status**: Active

## 1. Risk Summary

| Category | Total | Critical | High | Medium | Low | Mitigated |
|---|---:|---:|---:|---:|---:|---:|
| Technical | 2 | 0 | 0 | 2 | 0 | 0 |
| Business | 1 | 0 | 0 | 1 | 0 | 0 |
| Delivery | 1 | 0 | 0 | 1 | 0 | 0 |
| Operational | 1 | 0 | 0 | 1 | 0 | 0 |
| Security | 1 | 0 | 0 | 1 | 0 | 0 |
| Compliance | 1 | 0 | 0 | 1 | 0 | 0 |
| **Total** | **7** | **0** | **0** | **7** | **0** | **0** |

**Aggregate score**: 74 / 175. **Overall level**: Medium. No risk is accepted.

## 2. Risk Assessment Matrix

Impact: Critical=5, High=4, Medium=3, Low=2. Likelihood: Very High=5, High=4, Medium=3, Low=2, Very Low=1. Score = impact × likelihood; 8–14 is Medium and requires active mitigation, 15–19 is High, 20–25 Critical.

## 3. Technical Risks

### T1 — Configured maximum-size path is infeasible

**Status**: Open · **Impact/Likelihood/Score**: High (4) / Medium (3) / **12**  
**Description and trigger**: The proposed 100 MB/file and 500 MB/Share path may exceed Caddy, browser, R2, ClamAV, ZIP worker, disk, memory or timeout limits. A failed boundary or over-limit spike is the trigger.  
**Consequences**: unsafe resource pressure or a material scope reduction.  
**Mitigation**: enforce both limits during upload and ZIP creation; run boundary/over-limit tests; set the configured maximum only to the proven result.  
**Contingency**: lower the maximum and revise the PRD/configuration before build.  
**Owner**: Sebastian Szekely · **Review**: before implementation commitment.  
**Related**: PRD Q-001, R-002, R-004, NFR-005; depends on D1 and O1.

### T2 — Scanner unavailability creates ambiguous availability

**Status**: Open · **Impact/Likelihood/Score**: High (4) / Medium (3) / **12**  
**Description and trigger**: ClamAV may time out, fail or be unavailable; repeated missing results trigger the risk. The proposed contract is a 60-second timeout and three transient retries.  
**Mitigation**: fail closed; persist scan state; retry transient errors at most three times; mark detections INFECTED and timeout/unavailability/retry exhaustion FAILED.  
**Contingency**: keep Share unavailable and clean it up at expiry.  
**Owner**: Sebastian Szekely · **Review**: before scan-worker implementation.  
**Related**: PRD Q-003, R-003, NFR-003; affects O1.

## 4. Business Risks

### B1 — Illustrative workflow does not represent demand

**Status**: Open · **Impact/Likelihood/Score**: Medium (3) / Medium (3) / **9**  
**Description and trigger**: The carrier workflow, volume and email/personal-sharing workaround are unvalidated. Absence of a representative user confirmation triggers the risk.  
**Mitigation**: validate with an intended Sender and Recipient before production promotion.  
**Contingency**: keep the result a local demo and re-scope based on evidence.  
**Owner**: Sebastian Szekely · **Review**: before production promotion.  
**Related**: PRD Q-008 and Signal assumptions.

## 5. Delivery Risks

### D1 — Local dependency configuration delays delivery

**Status**: Open · **Impact/Likelihood/Score**: Medium (3) / Medium (3) / **9**  
**Description and trigger**: R2 credentials, Keycloak claims, Caddy routing and ClamAV runtime are not configured. Any unavailable integration blocks dependent work.  
**Mitigation**: time-box small integration spikes and document environment configuration.  
**Contingency**: use a local S3-compatible test path only for development while preserving private-object semantics.  
**Owner**: Sebastian Szekely · **Review**: before storage/authentication implementation.  
**Related**: PRD Q-002, Q-006; affects T1 and T2.

## 6. Operational Risks

### O1 — Cleanup leaves private objects after six-hour retention

**Status**: Open · **Impact/Likelihood/Score**: High (4) / Medium (3) / **12**  
**Description and trigger**: Worker or R2 deletion can fail, leaving originals or ZIP beyond expiry. Cleanup failure record or object found after expiry triggers the risk.  
**Mitigation**: idempotent deletion, retry schedule, failure records, and R2 lifecycle validation.  
**Contingency**: quarantine the cleanup failure for manual resolution; public access remains denied.  
**Owner**: Sebastian Szekely · **Review**: before demo acceptance.  
**Related**: PRD R-007, NFR-004; depends on D1.

## 7. Security Risks

### S1 — Public-link leak exposes an approved Share

**Status**: Open · **Impact/Likelihood/Score**: High (4) / Low (2) / **8**  
**Description and trigger**: A Recipient forwards or exposes a public link. Unusual/unknown access evidence triggers review.  
**Mitigation**: high-entropy opaque token, HTTPS, optional password, six-hour expiry, private R2 and short-lived grants.  
**Contingency**: access naturally ends at expiry; no revocation control is in scope.  
**Owner**: Sebastian Szekely · **Review**: before public-route implementation.  
**Related**: PRD R-005/R-006 and Q-007.

## 8. Compliance Risks

### C1 — Production obligations are unknown

**Status**: Open · **Impact/Likelihood/Score**: High (4) / Medium (3) / **12**  
**Description and trigger**: Privacy, audit or regional obligations could require design changes. A production promotion request before policy review triggers the risk.  
**Mitigation**: classify the work as local demo only; obtain compliance review before promotion.  
**Contingency**: block production promotion until obligations and controls are confirmed.  
**Owner**: Sebastian Szekely · **Review**: before production promotion.  
**Related**: PRD Q-007 and NFR-008.

## 9. Risk Dependencies

```text
D1 (configuration) → T1 (maximum-size path) → O1 (cleanup/capacity)
D1 (configuration) → T2 (scan behavior)
T2 (scan outcome) → O1 (retained objects)
C1 (compliance) → S1 (link controls) and production architecture constraints
B1 (demand) → whether production promotion is justified
```

## 10. Mitigation Timeline

| Stage | Risks | Action | Owner | Status |
|---|---|---|---|---|
| Before implementation commitment | T1, D1 | R2/Caddy/Keycloak/ClamAV integration and size-path spikes. | Sebastian Szekely | Planned |
| Before worker build | T2, O1 | Decide scan failure/retry and cleanup behavior; test it. | Sebastian Szekely | Planned |
| Before demo acceptance | T1, O1, S1 | E2E security, expiry, archive and cleanup tests. | Sebastian Szekely | Planned |
| Before production promotion | B1, C1 | Workflow and compliance validation. | Sebastian Szekely | Planned |

## 11. Risk Monitoring

Review active medium risks before each architecture/implementation decision; review any new High/Critical risk immediately. Track: unresolved risk count (target 0 High/Critical), max-size spike result, scan failure rate in test, cleanup retry outcome, and public-route security-test result.

## 12. Retired Risks

None. No risks have been mitigated, closed, or accepted.

## 13. Lessons Learned

- The free R2 allowance is measured in GB-month, so it is not a 10 GB per-upload limit.
- A six-hour lifecycle reduces storage exposure but does not remove the need for reliable cleanup and access checks.
- Fast Lane can document a local-demo design without claiming production readiness.

## Related Artifacts

- Signal: `signal/signals/20261022-external-document-sharing.md`
- Context: `explore/explore-external-document-sharing/context.md`
- Domain analysis: `explore/explore-external-document-sharing/domain-analysis.md`
- PRD: `explore/prds/external-document-sharing-prd.md`
- Architecture context: `explore/explore-external-document-sharing/architecture-context.md`
- ADRs/HLD: not yet created; this register must be enriched when they exist.

## Enrichment Log

| Date | Trigger | Change |
|---|---|---|
| 2026-10-22 | Solution Design risk assessment | Initial approved register created; no risks accepted. |

**Last Updated**: 2026-10-22  
**Updated By**: Explore Agent  
**Next Review**: Before architecture solutioning begins.
