+++
[metadata]
task_id = "SNS003"
title   = "Fix invalid single-file download"
status  = "04-implementing"

kind = "bug"
[sources]
epic      = "explore/epics/SNS001-snapsends3-secure-sharing.md"
documents = [
  "explore/decisions/external-document-sharing-adr-002-direct-scoped-r2-transfers.md",
  "explore/decisions/external-document-sharing-adr-005-streaming-zip-to-r2.md",
  "explore/decisions/external-document-sharing-adr-006-dual-expiry-cleanup.md",
  "docs/repositories.md"
]

[links]
blocks  = []
related = ["work/02-planning/SNS002-validate-snapsends3-feasibility-gates/task.md"]
parent  = []
child   = []

[workflow]
defined = "2026-10-23"
planned = "2026-10-23"
implemented = "2026-10-23"

[assignments]
definition     = ""
planning       = "Codex"
implementation = "Sebastian.Szekely@endava.com"
+++

# Task: Fix invalid single-file download

**Task ID**: SNS003
**Status**: 04-implementing
**Phase**: govern
**Date**: 2026-10-23
**Branch**: impl/SNS003-fix-invalid-single-file-download

## Problem Statement

The codebase already selects an original name for a one-file share and a ZIP name for a multi-file share, but the local end-to-end behavior has not been proven. Windows previously rejected a download named as a ZIP because it contained source-file bytes. This task adds the missing regression and local integration evidence so recipients receive valid downloads.

## Goals & Acceptance Criteria

### Goals

- Prove a valid, saved download for both one-file and multi-file shares.
- Preserve the private, short-lived download-grant model.

### Acceptance Criteria

- [x] Regression tests cover original-name selection for one-file shares and ZIP-name selection for multi-file shares.
- [x] Storage adapter tests cover an attachment disposition and safe filename sanitization.
- [x] Local Compose verification proves browser-visible attachment headers, original bytes, and valid ZIP integrity.
- [x] Expiry, password, and scan-gate controls continue to apply before a download URL is issued.

## Non-Goals

- Public DNS, remote deployment, and migration from LocalStack to Cloudflare R2.
- Changing archive behavior for multi-file shares.

## Context & References

### Source Material

- `explore/epics/SNS001-snapsends3-secure-sharing.md` — recipient delivery requirement.
- ADR-002 and ADR-005 — private short-lived download grants and ZIP behavior.

### Related Tasks

- **Blocks**: None.
- **Related**: SNS002 feasibility validation.

## Constraints & Dependencies

- Maintain `https://localhost:8443` as the local origin and retain the existing Caddy object-storage proxy.

## Success Metrics

- The one-file response is valid when opened by Windows; the multi-file response passes ZIP integrity checks.

## Notes

---

**Implementation Note**: This task definition captures requirements and acceptance criteria only. Technical implementation details belong in `plan.md`, created during the planning phase.
