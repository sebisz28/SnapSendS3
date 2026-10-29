# Acceptance Criteria

## Verification Status

The implementation is merged in PR #5. Criteria below are checked only where the current automated or browser evidence supports them. Remaining accessibility, reduced-motion, responsive, authentication, and upload end-to-end checks are recorded in [the implementation summary](../summary.md).

## Epic Link

No approved epic acceptance criterion was identified that directly covers this landing-page refresh. The work aligns with the Angular sender/recipient experience scope described in the [approved external document sharing PRD](../../../../explore/prds/external-document-sharing-prd.md), including NFR-006 accessibility.

## Happy Path Criteria

- [x] AC1: A visit to `/` without a recipient token or sign-in callback displays a dedicated landing experience with the centered white-and-blue box on a charcoal-to-black background.
- [ ] AC2: Scrolling from the top through the landing scene progressively opens the box and reveals a restrained soft-white and warm-gold interior light; the opening state corresponds to the scroll position and is understandable at desktop and mobile widths.
- [x] AC3: After the reveal, a prominent “Upload your files” control is available and navigates to the existing sender workspace/sign-in entry at `/upload`.
- [ ] AC4: The existing sign-in action, OIDC PKCE round-trip, and sender upload workspace work as before after entering through `/upload`; no new authentication flow is presented.
- [ ] AC5: Buttons and interactive controls show clear focus, hover, and pressed states where applicable, with readable contrast against their backgrounds.

## Error Handling Criteria

- [ ] AC6: If the scroll-progress enhancement cannot initialize, the page still shows a usable box presentation and the “Upload your files” route remains available.
- [x] AC7: If a user opens `/upload` directly, the existing sender/sign-in workspace renders without requiring a prior landing-page visit.

## Edge Case Criteria

- [x] AC8: An existing recipient link at `/?share=<token>` continues to render the current recipient experience instead of the landing page.
- [x] AC9: A recipient URL at `/upload?share=<token>` renders the same existing recipient experience and does not show the sender workspace. (Route-selection unit coverage confirms share-query precedence on `/upload`.)
- [ ] AC10: When reduced motion is preferred, the page does not depend on scroll-driven transforms; the box/light is static or fades briefly and the upload action remains accessible.
- [ ] AC11: At narrow mobile, landscape mobile, tablet, and desktop widths, content does not overflow horizontally, the artwork remains legible, and the CTA is reachable.
- [ ] AC12: Keyboard-only navigation reaches the upload link with visible focus, and assistive technology encounters a meaningful heading and link while decorative box artwork is ignored.

## Integration Criteria

- [x] AC13: Caddy's existing SPA fallback serves the app for `/upload` and the new path does not require changes to API or authentication configuration.
- [ ] AC14: A share created from the sender workspace generates a URL that opens the recipient view, and pre-existing root-path share URLs remain usable.
- [ ] AC15: Existing sender and recipient API requests, file upload behavior, share generation, and download behavior remain unchanged.
