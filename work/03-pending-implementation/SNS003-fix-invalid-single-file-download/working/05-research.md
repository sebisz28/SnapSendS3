# Research Findings

## Search Terms

- download
- ZIP
- `Content-Disposition`
- R2 / LocalStack
- object storage

## Decision Inventory

| Decision ID | Status | Key Requirement | Link |
|---|---|---|---|
| ADR-002 | Accepted | The API issues short-lived, object-specific download grants; it does not proxy file bytes. | `explore/decisions/external-document-sharing-adr-002-direct-scoped-r2-transfers.md` |
| ADR-005 | Accepted | Two or more clean files are delivered as one ZIP archive. | `explore/decisions/external-document-sharing-adr-005-streaming-zip-to-r2.md` |
| ADR-006 | Accepted | Access expires at the API boundary and cleanup cannot restore access. | `explore/decisions/external-document-sharing-adr-006-dual-expiry-cleanup.md` |

## Related Tasks

| Task ID | Status | Relationship | Link |
|---|---|---|---|
| SNS001 | Implementing | Delivers recipient downloads and ZIP behavior. | `explore/epics/SNS001-snapsends3-secure-sharing.md` |
| SNS002 | Planning | Validates storage and Caddy feasibility prerequisites. | `work/02-planning/SNS002-validate-snapsends3-feasibility-gates/` |

## Documentation References

| Document | Relevance | Link |
|---|---|---|
| Repository deployment guide | Defines localhost and public origin behavior for Caddy-proxied object storage. | `docs/repositories.md` |
| Share service tests | Already specify that a one-file share uses its original name for storage URL generation. | `apps/api/src/test/java/com/snapsends3/service/ShareServiceTest.java` |

## Conflicts and Gaps

| Conflict/Gap | Description | Resolution |
|---|---|---|
| Missing test coverage | Existing code selects the correct delivery names, but only the one-file service behavior is covered. | Add multi-file, adapter sanitization, and local integration evidence. |
| LocalStack response behavior | LocalStack may ignore S3 response-header query overrides. | Verify the existing Caddy proxy translates the signed query override into `Content-Disposition` and test the live header. |
