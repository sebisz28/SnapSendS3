# Step 2: Document Risks

## Objective

For each identified risk, create a full entry with description, trigger, impact, likelihood, mitigation, contingency, and owner.

## Entry Criteria

- [ ] Step 1 (Identify Categories) complete

## Actions

### 2.1 Document Each Risk

For each risk, create an entry:

```
Risk [N]: [Risk Title]

Category: [Technical/Business/Operational/Compliance]
Description: [What could go wrong]
Trigger: [What would cause this risk to materialize]
Impact: [Critical/High/Medium/Low] - [Specific consequence if it occurs]
Likelihood: [Very High/High/Medium/Low/Very Low] - [Probability of occurrence]
Risk Score: [Impact × Likelihood]

Root causes:
- [Root cause 1]
- [Root cause 2]

Triggers/Warning signs:
- [What would indicate this risk is materializing]

Mitigation Strategy:
- [Action 1 to reduce likelihood or impact]
- [Action 2]

Contingency Plan:
- [What to do if risk materializes]

Owner: [Who is responsible]
Status: [Open/Monitoring/Mitigated/Closed]
Target Resolution: [Date or milestone]

Related:
- ADR: [Link to relevant ADR if applicable]
- Assumption: [Link to assumption if applicable]
- Dependencies: [Related risk IDs]
```

### 2.2 Use Category Prefixes for Risk IDs

- Technical: T1, T2, T3...
- Business: B1, B2, B3...
- Operational: O1, O2, O3...
- Compliance: C1, C2, C3...

## Exit Criteria

- [ ] All risks documented with full details
- [ ] Each risk has ID, category, description, trigger
- [ ] Mitigation strategy and contingency plan defined
- [ ] Owners assigned
- [ ] Related ADRs and assumptions linked

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.risk-documentation:0.2.1:2026-10-22T08:59:43Z -->
