# Sequencing and Scope

## Implementation Phases

### Phase 1: Regression Coverage

- [ ] Add or complete unit tests for one-file original-name and multi-file ZIP-name selection.
- [ ] Add a regression test for sanitized filename handling.

### Phase 2: Local Delivery Verification

- [ ] Verify the storage adapter signs attachment content disposition with the selected filename.
- [ ] Verify Caddy returns the signed response disposition for LocalStack-backed downloads.

### Phase 3: Integration Verification

- [ ] Build the API and Caddy images, create fresh one-file and multi-file shares, and verify response headers, magic bytes, and ZIP integrity.
- [ ] Run the API test suite and record results.

## Parallel Work Opportunities

- [ ] Unit coverage and Caddy configuration review can proceed independently after the expected response behavior is defined.

## Scope Validation

### Original Requirements

- [x] A one-file recipient download is valid: addressed in Phases 1–3.
- [x] A multi-file recipient ZIP remains valid: addressed in Phases 1 and 3.
- [x] Localhost behavior remains supported: addressed in Phase 3.

### Scope Creep Detected

None.

### Non-Goals Confirmed Excluded

- Public domain deployment: not included.
- Storage provider migration: not included.

## Task Tags

- **Complexity**: moderate
- **Component**: cross-cutting
- **Type**: fix
- **Priority**: high
- **Risk**: medium
