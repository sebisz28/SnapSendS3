# Risks and Dependencies

## Risk Assessment

| Risk | Category | Likelihood | Impact | Mitigation |
|---|---|---|---|---|
| A user-supplied filename injects a response header. | Security | Low | High | Sanitize CR, LF, and quotes before signing the disposition. |
| LocalStack does not apply signed response overrides. | Operations | Medium | Medium | Verify Caddy emits the signed disposition at the browser-visible endpoint. |
| A one-file response is accidentally treated as a ZIP. | UX | Medium | High | Regression-test filename and bytes for one-file and multi-file shares. |
| Existing expiry, password, or scan gate behavior regresses. | Security | Low | High | Retain authorization order and run existing service tests. |
| Extra download headers affect response latency. | Performance | Low | Low | Header-only change; verify with smoke test. |
| Schema migration required. | Data Model | N/A | N/A | None; existing `ShareFile.originalName` is used. |

## Dependencies

### Blocking (must complete first)

None.

### Dependent (depends on this task)

None identified.

### Related (shared context)

- [ ] SNS002: confirms local object storage and Caddy feasibility evidence.
- [ ] SNS001: delivers the broader secure share lifecycle.

## Validation Against Research

- [x] The approach preserves ADR-002 direct short-lived storage transfers.
- [x] The approach preserves ADR-005 multi-file ZIP delivery.
- [x] The approach preserves ADR-006 expiry enforcement.

### Conflicts/Gaps Found

| Issue | Description | Resolution |
|---|---|---|
| LocalStack behavior varies from R2. | The signed header override may require proxy support in local development. | Make the Caddy behavior an explicit integration verification criterion. |
