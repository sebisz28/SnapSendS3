# Sequencing and Scope

## Implementation Phases

### Phase 1: Route and layout foundation

- [ ] Add path/query view selection: `/` landing, `/upload` sender workspace, and share query precedence on both.
- [ ] Add the landing page structure and scoped charcoal visual theme while preserving existing sender/recipient view styling.
- [ ] Verify Caddy fallback, direct `/upload` entry, legacy root share URL, and OIDC callback pathname behavior.

### Phase 2: Box reveal and CTA

- [ ] Create CSS box faces, 3D perspective, restrained shine, and warm interior glow.
- [ ] Bind clamped scroll progress to box opening and light with a passive rAF update.
- [ ] Add the prominent “Upload your files” link to `/upload` and button interaction states.
- [ ] Add reduced-motion static/fade behavior and keyboard/focus semantics.

### Phase 3: Integration and polish

- [ ] Validate mobile, tablet, desktop, short viewport, zoom, touch, and keyboard use.
- [ ] Verify existing OIDC sender flow, current upload workspace, old and new recipient share URLs.
- [ ] Run Angular production build and complete browser/accessibility walkthrough; capture harness limitation if automated tests remain unavailable.

## Parallel Work Opportunities

- No blocking cross-team or service dependency identified.
- CSS box styling and route-preservation test cases can be prepared independently, but route semantics and landing content should be integrated in the same Angular component to avoid route drift.

## Blocked Dependencies

- None.

## Dependent Tasks

- None identified.

## Scope Validation

### Original Requirements

- [x] Dedicated charcoal-to-black page with centered white-and-blue box: Phase 1 and Phase 2.
- [x] Scroll-driven gradual opening with subtle 3D perspective: Phase 2.
- [x] Restrained soft-white and warm-gold interior light: Phase 2.
- [x] Prominent end-of-experience upload CTA: Phase 2.
- [x] Navigate to existing login/sign-in entry and preserve auth: Phase 1 and Phase 3.
- [x] Hover/pressed/focus feedback: Phase 2.
- [x] Responsive layout, accessible controls, and reduced-motion behavior: Phase 2 and Phase 3.
- [x] Preserve sender, recipient, share-link, and OIDC flows: Phases 1 and 3.

### Scope Creep Detected

- None. Path handling is required to add the requested dedicated page safely without breaking current share links and auth.

### Non-Goals Confirmed Excluded

- Authentication provider or flow changes.
- Backend/API/storage changes.
- Redesigning sender workspace or recipient experience.
- Apple brand/product screen imitation or unrelated new features.
- New animation/runtime dependencies unless proven necessary.

## Task Tags

- **Complexity**: moderate
- **Component**: app
- **Type**: enhancement
- **Priority**: medium (no urgency supplied)
- **Risk**: medium (route compatibility and motion behavior)
