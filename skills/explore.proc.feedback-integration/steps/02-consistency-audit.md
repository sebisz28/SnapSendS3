# Step 2: Consistency Audit (BEFORE rewriting)

Compare the stakeholder version against the last canonical draft. Identify every change and classify it.

```
Consistency Audit

| # | Section | Change Description | Classification | Evidence |
|---|---------|-------------------|---------------|----------|
| 1 | [Section] | [What changed] | Improvement / Neutral / Potential Regression / Contradiction | [Why this classification] |
| 2 | [Section] | [What changed] | Improvement / Neutral / Potential Regression / Contradiction | [Why this classification] |

Specific checks:
- Terminology drift: [N] instances found
- Boundary ownership shifts: [N] instances found
- Event/state semantic changes: [N] instances found
- New assumptions introduced: [N] instances found
- Direct contradictions with established decisions: [N] instances found

Summary:
Improvements: [N] | Neutral: [N] | Potential Regressions: [N] | Contradictions: [N]

⚠️ Potential regressions and contradictions require architect review before any rewriting.

How would you like to proceed?
- Review dispositions — show me accept/reject recommendations for each item
- Accept all improvements — let me decide only on regressions and contradictions
- Override — I have specific instructions for certain items
```

**STOP**: Wait for architect to review the consistency audit results.

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.feedback-integration:0.1.2:2026-10-22T09:04:57Z -->
