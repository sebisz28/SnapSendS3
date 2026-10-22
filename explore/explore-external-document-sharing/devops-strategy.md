# DevOps Strategy: SnapSendS3

**Status:** Approved for conditional local-demo planning  
**Profile:** Standard, cloud-agnostic modular monolith  
**Date:** 2026-10-22

## 1. Overview & Context

One Spring Boot modular monolith and Angular application use PostgreSQL, Keycloak, private R2, clamd, and Caddy. [OBS: HLD] The strategy supports a local demo; production cloud, CI provider, container runtime, and compliance posture are TBD. [OBS: PRD]

## 2. CI/CD Pipeline Design

| Stage | Trigger | Maximum | Output |
|---|---|---:|---|
| Lint/type/format | push | 1 min | clean source |
| Unit/coverage | push | 2 min | JaCoCo/Jest report |
| Integration/OpenAPI | PR | 10 min | boundary verification |
| Build/SBOM | merge | 5 min | immutable JAR/static bundle or container |
| Demo validation | demo environment | 30 min | E2E, axe, ZAP, browser report |
| Smoke | deploy | 5 min | health and critical-flow outcome |

Build once and inject environment configuration at deploy time. CI/CD provider is TBD; stages are provider-neutral. [ASM]

## 3. Quality Gates

Merge requires zero lint/type errors, 100% tests passing, 90% line/85% branch coverage floors (95%/90% for lifecycle/access/scan/cleanup), integration/OpenAPI success, and no critical/high dependency findings. Demo acceptance additionally requires E2E, accessibility, ZAP, and BLK-001–BLK-004 evidence. [OBS: test strategy]

## 4. Testing Integration

Unit, integration, contract, and E2E stages map directly to the approved [test strategy](test-strategy.md). Quarantined flaky tests are fixed within one sprint and never silently pass.

## 5. Environment Strategy

| Environment | Purpose | Data | Lifecycle |
|---|---|---|---|
| Local | inner loop and required local demo | synthetic | persistent |
| CI | PR tests | synthetic ephemeral containers | per run |
| Staging/demo | E2E and feasibility spikes | synthetic | persistent while demo is active |
| Production | not designed | real | TBD |

No production credentials enter non-production environments. The same artifact is promoted; environment configuration is injected.

## 6. Infrastructure as Code

Use Terraform/OpenTofu only when a hosted environment is selected; keep modules cloud-agnostic until then. IaC changes require format, validate, plan, Checkov/Trivy-config scan, human approval for apply, and drift review. [ASM: hosting undecided]

## 7. Release & Deployment Strategy

For the demo, use a manually approved rolling/recreate deployment with a feature flag for public sharing. Production deployment style is TBD; choose blue-green with reversible, expand-and-contract migrations before production. A feature flag off or prior immutable artifact is the rollback path.

## 8. Observability & Incident Response

Use secret-safe structured logs with correlation IDs, lifecycle/worker/cleanup metrics, and OpenTelemetry-ready traces. Alert on scan-worker failures, cleanup failures, quota rejection, public route failure, and sustained download-denial anomalies. No production SLO is claimed; define SLOs before production. [OBS: NFR-007/008]

## 9. DevSecOps

Use pre-commit secret detection; SAST, dependency/license scanning, SBOM creation, and image/IaC scans when containers/IaC exist; run OWASP ZAP baseline in the demo environment. Secrets come from a runtime secret store or local secure configuration, never source control. Critical/high vulnerabilities block demo acceptance.

## 10. Governance & Compliance

Use GitHub-flow-style short branches, one peer review, and passing automated gates. Preserve PR, pipeline, artifact, test, security-scan, and deploy evidence for demo traceability. Production regulatory, retention, privacy, and data-region obligations remain open and block promotion. [OBS: PRD Q-007]

## 11. Developer Experience

Provide a documented local start path, synthetic fixtures, Testcontainers/emulators, and one-command checks where practical. Targets: unit under 2 minutes, integration under 10 minutes, demo validation under 30 minutes. [OBS: test strategy]

## 12. DORA Metrics & Continuous Improvement

Baseline is new-project/N-A. For a future production service, target weekly deployment, lead time under one week, change failure rate under 15%, and restoration under one day. Review pipeline duration, flaky ratio (<1%), security backlog, and feasibility-gate status monthly.

## 13. Risks & Open Questions

| Item | Impact | Owner / target |
|---|---|---|
| R2, Keycloak, clamd, capacity spikes | blocks implementation commitment | Sebastian Szekely / before implementation |
| CI provider, hosting, container runtime, secret store | blocks production deployment design | Sebastian Szekely / before production |
| Compliance, region, SLA/RTO/RPO | blocks production promotion | Sebastian Szekely / before production |
| Absolute quota budgets | blocks final demo configuration | Sebastian Szekely / before demo |

## References

- [PRD](../prds/external-document-sharing-prd.md)
- [HLD](../hlds/external-document-sharing-hld.md)
- [Test strategy](test-strategy.md)
- [Risk register](risks.md)
- [Glossary](../glossary.md)
