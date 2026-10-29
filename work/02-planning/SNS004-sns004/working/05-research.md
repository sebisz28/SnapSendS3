# Research Findings

## Search Terms

- Angular app routing and entry points
- Sender sign-in and OIDC redirect behavior
- Recipient share-link query handling
- Accessible client flows
- Scroll-linked and reduced-motion animation

## Decision Inventory

| Decision ID | Status | Key Requirement | Link |
|-------------|--------|-----------------|------|
| ADR-001: Modular monolith | Accepted | Existing app is part of the SnapSendS3 application boundary; no frontend-specific route decision. | [ADR-001](../../../../explore/decisions/external-document-sharing-adr-001-modular-monolith.md) |
| ADR-002: Direct scoped R2 transfers | Accepted, conditional on BLK-001 | Upload/download bytes remain on existing private-transfer path; landing-page work must not alter transfer behavior. | [ADR-002](../../../../explore/decisions/external-document-sharing-adr-002-direct-scoped-r2-transfers.md) |
| ADR-003–006 | Accepted | Backend worker, scanning, archive, and retention decisions are outside this UI task. | [Decision directory](../../../../explore/decisions/) |

## Related Tasks

| Task ID | Status | Relationship | Link |
|---------|--------|--------------|------|
| SNS001 | 04-implementing | Parent capability/epic formation task; its task definition is still a template and its plan describes Angular sender and recipient experiences. No direct implementation constraint found. | [SNS001 plan](../../../04-implementing/SNS001-epic-formation-for-snapsends3-external-document-sh/plan.md) |
| SNS003 | 06-completed | Backend download filename regression; no overlap with the landing-page route or presentation. | [SNS003 summary](../../../06-completed/SNS003-fix-invalid-single-file-download/summary.md) |

## Documentation References

| Document | Relevance | Link |
|----------|-----------|------|
| External Document Sharing PRD, NFR-006 | Requires accessible recipient-facing flows with keyboard walkthrough and WCAG-AA-oriented automated checks. This task adds stronger explicit accessibility requirements for its new page. | [PRD](../../../../explore/prds/external-document-sharing-prd.md) |
| External Document Sharing HLD, §§2 and 12 | Angular hosts sender and public-recipient experiences; the HLD identifies future Angular public experience routes as feasible without changing the storage boundary. | [HLD](../../../../explore/hlds/external-document-sharing-hld.md) |
| Repository deployment notes | Deployed share links are assembled from the current origin and browser pathname plus `?share=...`; changing the root path must preserve incoming recipient links or update link generation compatibly. | [Repository docs](../../../../docs/repositories.md) |
| Current web client | A standalone Angular 19 component owns sender, recipient, and PKCE/OIDC behaviors; share tokens come from the `share` query parameter and redirect URI is the current pathname. | [main.ts](../../../../apps/web/src/main.ts), [package.json](../../../../apps/web/package.json) |

## Conflicts and Gaps

| Conflict/Gap | Description | Resolution |
|--------------|-------------|------------|
| “Existing login page” terminology | There is no separate login page. The current sender workspace includes a “Sign in” action that starts OIDC and returns to the same pathname. | Plan the CTA to navigate to the existing sender workspace/sign-in entry point and preserve the OIDC implementation. Confirm the route with observable app behavior during implementation. |
| Root path and recipient deep links | The current app handles `?share=...` on the same root pathname used by generated share links. Replacing the root view risks breaking previously issued links. | Preserve query-based recipient rendering at `/`; add a distinct landing path or route-aware root so old share URLs remain valid. The exact path is a planning decision to be recorded. |
| No frontend-specific ADR | Existing ADRs govern backend/storage concerns and do not specify a routing or motion approach. | Keep the plan limited to app-level UI routing and native capabilities; do not modify API contracts or storage. |
