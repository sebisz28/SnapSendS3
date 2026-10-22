+++
name = "explore.proc.risk-documentation"
description = "Use this skill when you need to document and track project risks — identifying risk categories, scoring by impact and likelihood, defining mitigation strategies, and tracking accepted risks. Activates after architecture documentation provides the technical context. Also relevant when someone says 'what could go wrong,' 'assess the risks,' or 'create a risk register.' Does NOT cover regulatory or compliance constraints — use Regulatory Compliance for legal and standards requirements."
license = "Proprietary. See LICENSE.md"
+++

# Risk Documentation

Document and track project risks with impact/likelihood scoring and mitigation strategies.

## Step Execution Rule
**ONE STEP AT A TIME**: Read step → Execute step → Complete step → Next step
❌ Reading ahead ❌ Multiple steps ❌ Skipping step files

## When to Use

Use this skill when you need to:
- Create risk registers for project planning
- Assess feasibility and identify risks
- Track mitigation strategies and owners
- Document accepted risks with rationale
- Maintain risk status throughout project lifecycle

## Pre-Check

If a risk register already exists at `explore/explore-[slug]/risk-register.md`:
1. Load the existing risk register
2. Present to the steering team: "Existing risk register found. Review and update, or create fresh?"
3. If updating → skip to the validation step with existing data for review
4. If creating fresh → proceed from Step 1

## Inputs to Request (if missing)

Before creating risk documentation, ensure you have:

1. **PRD** — `explore/prds/[slug]-prd.md` (requirements analysis risks)
2. **Context baseline** — From Context Documentation skill
3. **ADRs** — Architecture Decision Records (trade-off risks, rejected alternative fallback scenarios)
4. **User flows** — `explore/domain/flows-[slug].md` (edge cases from error states)
5. **Wireframes** — `explore/design/wireframes-[slug].md` (design feasibility notes)
6. **Slug** — Project identifier for file naming (e.g., `care-it`)

**Optional inputs** (enhance quality if available):
- Handoff notes (`explore/design/handoff-notes.md`) — design pipeline feasibility risks
- Usability test plan (`explore/design/usability-test-plan-[slug].md`) — interaction risks from testing
- Architecture drivers (`explore/explore-[slug]/architecture-drivers.md`) — quality attribute trade-off risks

**STOP**: If PRD is missing, create it first using PRD Generation skill.

> **Note**: Risk documentation can be invoked early (post-PRD, pre-architecture). Update incrementally as HLD and ADRs mature — architecture trade-off risks will be added when those artifacts become available.

## Process Steps

| Step | File | Purpose |
|------|------|---------|
| 1 | [01-identify-categories.md](./steps/01-identify-categories.md) | Identify risk categories |
| 2 | [02-document-risks.md](./steps/02-document-risks.md) | Document each risk with full details |
| 3 | [03-score-prioritize.md](./steps/03-score-prioritize.md) | Score and prioritize risks |
| 4 | [04-present-validation.md](./steps/04-present-validation.md) | Present risks for human validation |
| 5 | [05-accepted-risks.md](./steps/05-accepted-risks.md) | Document accepted risks |
| 6 | [06-write-document.md](./steps/06-write-document.md) | Write risk register |
| 7 | [07-validation.md](./steps/07-validation.md) | Run completeness validation |

## Output Format

```
explore/explore-[slug]/risks.md
```

**Template**: `templates/risk-register-template.md`

**Structure**:
- Risk Summary (counts by category and priority)
- Risk Assessment Matrix (impact/likelihood definitions, score calculation)
- Risk entries by category (Technical, Business, Operational, Compliance)
- Risk Dependencies (interaction map, cascading risks)
- Mitigation Timeline
- Risk Monitoring (review schedule, KPIs)
- Retired Risks
- Lessons Learned
- Related Artifacts
- Enrichment Log

## Integration with Workflows

**Consumes**:
- **PRD Generation** — Requirements analysis risks, dependency risks, assumption gaps
- **Context Documentation** — Assumptions become risks
- **ADRs** — Architecture trade-off risks, rejected alternative fallback scenarios
- **User Flow Creation** — Edge cases from error states and decision-point failures
- **Wireframing** — Design feasibility notes, interaction complexity risks
- **Hi-Fi Handoff** — Design pipeline feasibility risks (conditional: D/C with `ui_in_scope`)
- **Usability Testing** — Interaction risks, usability edge cases (conditional: D/C)
- **Architecture Drivers** — Quality attribute trade-off risks (conditional: if `architecture-drivers.md` exists)
- **Hypothesis Documentation** — Risks may invalidate hypothesis
- **Regulatory Compliance** — Compliance risks feed into risk register

**Produces** (consumed by):
- **Architecture Solutioning** (`explore.proc.architecture-solutioning`) — Architecture must address identified risks
- **Epic Forming** (`explore.proc.epic-forming`) — Risk mitigations become acceptance criteria and dedicated tasks

## Best Practices

**Do**:
- ✅ Identify risks across all categories (technical, delivery, operational, security, business)
- ✅ Be specific about impact (not just "High" but "2-month delay to launch")
- ✅ Define concrete mitigation actions, not vague statements
- ✅ Assign owners to all open risks
- ✅ Document why risks are accepted, not just that they are
- ✅ Link risks to related ADRs and assumptions
- ✅ Update risk status as project progresses

**Don't**:
- ❌ Create generic risks without specific triggers
- ❌ Score all risks as "High" (prioritization loses meaning)
- ❌ Skip contingency planning
- ❌ Leave risks unowned
- ❌ Accept risks without documenting rationale and approver
- ❌ Forget to link architecture decisions to their risks

## Completeness Checklist

**Post-edit**: Re-run this checklist after any modification to the output artifact.

- [ ] Risks scored by impact × likelihood (not all "High")
- [ ] Mitigation strategies defined with named owners
- [ ] Accepted risks documented with approver name and date
- [ ] Risk register follows template structure
- [ ] Risks linked to related ADRs and assumptions
- [ ] Contingency plans defined for high-severity risks

## Gotchas

- ⚡ **Risk severity inflation**: The agent tends to classify most risks as "High" to appear thorough. When everything is high-priority, nothing is. Use the impact × likelihood matrix honestly — a risk with low likelihood is not high-severity just because the impact would be bad.
- ⚡ **Mitigation without owner**: Mitigations documented without a named owner never get executed. Every mitigation must have a specific owner (team or person) — "TBD" is acceptable temporarily but must be resolved before the risk register is finalized.
- ⚡ **Accepted risk without approver**: The agent sometimes marks risks as "Accepted" without documenting who approved the acceptance and on what date. Accepted risks without an approver are unaccepted risks — always record the decision-maker and rationale.

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.risk-documentation:0.2.1:2026-10-22T08:59:43Z -->
