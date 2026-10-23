+++
[metadata]
task_id = "SNS003"
title   = "Fix invalid single-file download"
status  = "02-planning"

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
implemented = ""

[assignments]
definition     = ""
planning       = "Codex"
implementation = ""
+++

# Task: Fix invalid single-file download

**Task ID**: SNS003
**Status**: 01-pending-planning
**Phase**: govern
**Date**: 2026-10-23
**Branch**: plan/SNS003-fix-invalid-single-file-download

## Problem Statement

Single-file shares currently download raw source bytes while being presented as a ZIP archive. Windows rejects the resulting file as invalid. A recipient must receive one uploaded file under its original filename, while a multi-file share must remain a valid ZIP archive.

## Goals & Acceptance Criteria

### Goals

- Deliver a valid, saved download for both one-file and multi-file shares.
- Preserve the private, short-lived download-grant model.

### Acceptance Criteria

- [ ] A recipient downloading a one-file share receives the uploaded filename and valid original bytes.
- [ ] A recipient downloading a multi-file share receives a valid `snapsends3-share.zip` archive containing all uploaded files.
- [ ] The download response uses attachment behavior and does not render the file inline.
- [ ] Expiry, password, and scan-gate controls continue to apply before a download URL is issued.

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
