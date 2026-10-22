# ADR-004: clamd streaming scan gate

**Status:** Accepted — conditional on BLK-003  
**Date:** 2026-10-22  
**Decision Maker:** Sebastian Szekely  
**Supersedes:** None

## Context

Every downloadable file needs a malware verdict before recipient access. **Affects:** HLD Sections 6, 7, 9, 11.

## Decision

Use asynchronous `clamd` streaming from the scan worker. Allow ready state only after clean verdict; retry transient failures at most three times with a 60-second timeout, then mark `FAILED` and deny access.

## Options Considered

### Option A: clamd streaming
- **Pros:** Scans content without a second local-file copy; fits Spring worker design.
- **Cons:** Requires runtime protocol and capacity validation.
- **Selected because:** It satisfies the stated scanner choice and fail-closed rule.

### Option B: Local process or third-party scanning service
- **Pros:** Could simplify an individual deployment or offer managed capacity.
- **Cons:** Conflicts with selected scanner or adds unapproved external dependency.
- **Rejected because:** No requirement supports either alternative.

## Consequences

### Positive
- Pending, infected, and failed outcomes share one deny-by-default model.
- Verdicts persist in the Share lifecycle.

### Negative
- Scanner availability can delay delivery.
- The 60-second/three-retry contract requires integration proof.

### Risks
- BLK-003 remains; no Share may bypass the scanner while it is unresolved.

## Related Decisions

- DEC-012 in the decision log; BLK-003.
- [HLD](../hlds/external-document-sharing-hld.md) §§7, 9, 11.
