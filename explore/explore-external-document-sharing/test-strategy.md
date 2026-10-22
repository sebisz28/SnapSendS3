# Test Strategy: SnapSendS3 External Document Sharing

| Version | Date | Author | Changes |
|---|---|---|---|
| 0.1 | 2026-10-22 | Explore Agent + Sebastian Szekely | Initial conditional Fast Lane strategy |

> **Status: Conditional.** This strategy is grounded in the approved HLD. BLK-001 through BLK-004 must pass before implementation/demo acceptance.

## 1. Overview

**Purpose:** automate verification of the Share lifecycle, private delivery, scan gate, ZIP handling, expiry, and public-link safety. `[OBS: PRD]`  
**In scope:** R-001–R-010, NFR-001–NFR-009, security/accessibility/compatibility. **Out:** production SLA/compliance certification/load claims.  
**Principles:** pyramid first; automate every testable behavior; push tests down; no duplicated coverage; test code equals production quality; targets are floors.  
**Risk posture:** Standard commercial; access, scan, R2, ZIP, and cleanup retain High test depth. `[OBS: steering decisions]`

## 2. Testing Pyramid

| Layer | Target | Scope | Duration |
|---|---:|---|---:|
| Unit | 70–80% floor | domain/lifecycle/validators/UI components | <2 min |
| Integration | 20–30% floor | PostgreSQL, APIs, R2/Keycloak/clamd/Caddy adapters | <10 min |
| Contract | 0–5% | OpenAPI schema validation | <2 min |
| E2E | 5–10% | three critical journeys | <30 min |

Red flags: unit <60%, integration >35%, E2E >15%, or pipeline >30 minutes. `[INF: testing pyramid]`

## 3. Unit Testing

JUnit 5 + AssertJ + Mockito + JaCoCo test the Share state machine, expiry calculation, password verifier, 100 MB/file and 500 MB/Share validators, guardrail, scan retry/FAILED mapping, archive decision, cleanup retry, audit redaction, and Angular component state. Angular uses Jest + Angular Testing Library. No DB/network/filesystem. Use AAA/Given-When-Then, factories, independent tests, and `should_expected_when_condition` names.

Coverage floors: 90% line, 85% branch, 95% public-function coverage; lifecycle/access/scan/cleanup modules: 95% line and 90% branch. `[OBS: approved quality gate]`

## 4. Integration Testing

Use Spring Boot Test, Testcontainers PostgreSQL, REST Assured, WireMock/controlled adapters, and S3-compatible emulator.

| Boundary | Required cases |
|---|---|
| SnapSendS3 ↔ PostgreSQL | transition persistence, optimistic/concurrent update, job retry, audit redaction |
| SnapSendS3 ↔ R2 | private read denial, object-specific scoped upload/download, expired grant; real-R2 spike resolves BLK-001 |
| SnapSendS3 ↔ Keycloak | accepted/rejected JWT and `roles:dava.sender`; real mapping resolves BLK-002 |
| Scan worker ↔ clamd | clean, INFECTED, timeout at 60 seconds, three retries, FAILED; real integration resolves BLK-003 |
| Caddy ↔ public API | HTTPS, body/time limits, 100/500 MB boundary and over-limit cases; resolves BLK-004 |
| Archive/cleanup ↔ R2 | clean-only streaming ZIP; expiry denial, deletion/retry/lifecycle behavior |

Every boundary has happy, error, and edge tests. `[OBS: PRD/design sketch]`

## 5. Contract Testing

Use OpenAPI schema validation with Spectral/OpenAPI diff for Sender and public Share APIs. Pact/CDC is not used because SnapSendS3 is a modular monolith. External contracts remain integration spikes, not assumed schemas. `[OBS: architect selection]`

## 6. E2E Testing

Playwright runs Chromium, Firefox, and WebKit for:

1. clean single-file Share from authenticated Sender to anonymous Recipient;
2. password-protected multi-file Share to one ZIP download;
3. pending, FAILED, or INFECTED public Share that receives no grant/object URL.

Use explicit condition waits, not sleeps; flaky target <1%; quarantine then fix within one sprint. Expiry, retries, limits, and external contracts stay at lower deterministic layers.

## 7. Non-Functional Testing

**Security:** dependency/container scan and OWASP ZAP baseline against local public route; authorization, object privacy, token/password/grant/log inspection.  
**Accessibility:** axe-core in Playwright plus keyboard walkthrough; semantic state/error assertions.  
**Compatibility:** approved E2E journeys on Chromium/Firefox/WebKit and responsive layouts.  
**Performance:** deferred—no SLA is defined; the required 100/500 MB feasibility spike is capacity validation, not load testing. `[OBS: scope decision]`

## 8. Test Data & Environments

Use only version-controlled synthetic fixtures; no personal, warehouse, customer, carrier, or file content from production. Factories/builders for unit tests; seed/rollback or truncation for integration; dedicated synthetic accounts and cleanup scripts for E2E.

| Environment | Tests | Infrastructure |
|---|---|---|
| Local | unit/narrow integration | Maven/npm, Testcontainers/emulator |
| CI (TBD platform) | unit, integration, OpenAPI | ephemeral containers |
| Local demo/QA | E2E, ZAP, accessibility, compatibility, required spikes | local configured runtime |

No production test environment is in scope.

## 9. CI/CD Integration

1. Push/PR: format, lint, unit, changed-file coverage, dependency scan.  
2. PR: integration and OpenAPI validation.  
3. Demo build: Angular/Spring build and secret/configuration check.  
4. Demo validation: E2E, axe, multi-browser, ZAP baseline.  
5. Feasibility gate: BLK-001–004 spikes must pass before demo acceptance.

Failure blocks merge/build/demo acceptance respectively. CI provider is intentionally platform-neutral. Quarantined flaky tests never silently pass and must have a fix ticket.

## 10. Metrics & Success Criteria

| Metric | Target |
|---|---|
| Unit/integration/E2E pass rate | 100% |
| Coverage | 90% line / 85% branch floors; high-risk 95%/90% |
| Boundary coverage | 100% happy + error + edge |
| Critical journey coverage | 100% of three journeys |
| Unit/integration/E2E suite time | <2 / <10 / <30 min |
| Flaky ratio | <1% |
| Security vulnerabilities | zero critical/high before demo acceptance |

Deployment frequency, lead time, change-failure rate, and MTTR begin at N/A; production metrics await production scope.

## 11. Risks & Mitigations

| Risk | Mitigation |
|---|---|
| External contract unavailable | controlled adapter tests plus required real integration spike |
| Flaky browser tests | explicit waits, isolated data, quarantine/fix policy |
| Unsafe realistic data | synthetic lifecycle fixtures only |
| Coverage hides boundary defects | mandatory integration happy/error/edge coverage |
| No HLD | strategy remains conditional; design-sketch references are reviewed if architecture changes |

## 12. References

- PRD: `explore/prds/external-document-sharing-prd.md`
- HLD: `explore/hlds/external-document-sharing-hld.md`
- Boundary map: `explore/hlds/external-document-sharing-boundary-map.md`
- Risk register: `explore/explore-external-document-sharing/risks.md`
- Tooling: `explore/tooling.md`
