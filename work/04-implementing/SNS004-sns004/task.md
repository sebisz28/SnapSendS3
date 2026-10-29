+++
[metadata]
task_id = "SNS004"
title   = "sns004"
status  = "04-implementing"

[sources]
epic      = ""
documents = [
    "../../../explore/prds/external-document-sharing-prd.md",
    "../../../explore/hlds/external-document-sharing-hld.md",
    "../../../explore/decisions/external-document-sharing-adr-001-modular-monolith.md",
    "../../../explore/decisions/external-document-sharing-adr-002-direct-scoped-r2-transfers.md",
    "../../../docs/repositories.md",
    "../../../apps/web/src/main.ts",
    "../../../apps/web/package.json",
    "../../../ops/keycloak/snapsends3-realm.json"
]

[links]
blocks  = []
related = ["../SNS001-epic-formation-for-snapsends3-external-document-sh/task.md"]
parent  = []
child   = []

[workflow]
defined = "2026-10-29"
planned = "2026-10-29"
implemented = "2026-10-29"

[assignments]
definition     = ""
planning       = "Sebastian.Szekely@endava.com"
implementation = "Sebastian.Szekely@endava.com"
+++

# Task: sns004

**Task ID**: SNS004
**Status**: 04-implementing
**Phase**: flow
**Date**: 2026-10-29
**Branch**: impl/sns004-branded-login (follow-up to PR #5)

## Problem Statement

The current interface feels visually poor and needs a polished, modern refresh. The new experience should introduce users to the file-upload service through a focused, scroll-driven box reveal while preserving the existing login and authentication behavior.

## Goals & Acceptance Criteria

### Goals

- Create a dedicated landing page for the box reveal experience.
- Establish a clean visual hierarchy with considered spacing, refined typography, subtle depth, and purposeful interactions.
- Make the existing file-upload entry point easy to find and use, sending signed-out users directly to the existing Keycloak sign-in page and returning them to `/upload` after sign-in.
- Apply the same restrained charcoal, blue, and warm-light visual language to the Keycloak login page without imitating Apple product screens.
- Give the `/upload` sender workspace a colorful, polished refresh inspired by Apple's design principles, without copying Apple branding or changing upload/auth behavior.

### Acceptance Criteria

- [x] The dedicated page opens with a centered white-and-blue 3D box on a charcoal-gray-to-faded-black background.
- [ ] Scrolling progressively opens the box with smooth motion tied to scroll position and a restrained soft-white and warm-gold light revealed from inside.
- [x] The box has subtle highlights and a gentle luminous finish; the interior opening shows only the restrained warm light, without a blue ribbon crossing it.
- [x] At the end of the reveal, a prominent button labeled “Upload your files” is visible and easy to find. (The CTA is in normal page flow and its route was browser-checked.)
- [x] Activating “Upload your files” starts the existing authorization-code + PKCE sign-in directly; after sign-in, the user returns to the sender workspace at `/upload`. If already signed in, the CTA goes straight to `/upload`. (The `/upload` callback is unit-tested; credentials were not submitted.)
- [x] The Keycloak credential page uses a SnapSendS3 theme consistent with the landing page while preserving Keycloak form semantics, validation, password recovery, and authentication behavior. (Fresh-realm login page and credential controls verified; recovery and validation were not submitted.)
- [x] The `/upload` sender workspace has refined typography, color, spacing, and depth while retaining the existing upload, password, sign-in, and share controls. (Structural regression test passes; no upload transaction was changed.)
- [x] Previously issued recipient share links continue to work after the landing page is added. (Legacy root share route browser-checked.)
- [ ] Buttons and other interactive elements provide clear, subtle hover and pressed feedback.
- [ ] The experience is usable across mobile and desktop layouts and input methods.
- [ ] Content and controls are accessible, including keyboard operation, visible focus, and appropriate semantics and contrast.
- [ ] Reduced-motion preferences are respected with a simpler static or fade-based presentation.
- [ ] Scroll animation remains smooth and understandable on mobile and desktop, with the upload action available after the reveal.

## Non-Goals

- Replacing the identity provider or changing the behavior of the existing login or authentication flow.
- Copying Apple branding or specific Apple product screens.
- Adding unrelated content or interactions that clutter the landing page.

## Context & References

### Source Material

- User-provided requirements in the request that created this task — visual direction, scroll experience, navigation, accessibility, and responsive behavior.

### Related Tasks

- **Blocks**: None identified.
- **Related**: [SNS001 — Epic formation for SnapSendS3 external document sharing](../SNS001-epic-formation-for-snapsends3-external-document-sh/task.md).

## Constraints & Dependencies

- The “Upload your files” action must use the application's existing sender sign-in entry and preserve current authentication behavior.
- The experience must support reduced-motion preferences and responsive mobile and desktop layouts.

## Success Metrics

- All acceptance criteria can be demonstrated on the dedicated page, including reduced-motion and mobile layouts.
- Users can identify and activate “Upload your files” after completing the reveal and reach the existing sender sign-in entry.

## Notes

---

**Implementation Note**: This task definition captures requirements and acceptance criteria only. Technical implementation details belong in `plan.md`, created during the planning phase.
