# Step 1: Load PRD Template and Gather Inputs

## Objective

Load the PRD template and verify all required inputs are available.

## Entry Criteria

- [ ] Signal document available
- [ ] Hypothesis validated
- [ ] Technical feasibility notes available
- [ ] Personas available
- [ ] Journey maps available
- [ ] Context baseline available
- [ ] Risk register available
- [ ] Slug confirmed
- [ ] HLD available _(optional — used to enrich Technical Notes if already produced)_

## Actions

### 1.1 Load Template

Load PRD template from `templates/prd-template.md`.

### 1.2 Verify All Inputs

Check that all required inputs exist and are accessible:

```
PRD Generation - Input Verification

✅/❌ Signal document: [path]
✅/❌ Hypothesis: [path]
✅/❌ Technical feasibility: [path]
✅/❌ Personas: [path]
✅/❌ Journey maps: [path]
✅/❌ Context baseline: [path]
✅/❌ Risk register: [path]
✅/❌ Slug: [confirmed]
⬚ HLD (optional): [path or N/A]
```

**STOP**: If any inputs are missing, ask the human to provide them before proceeding.

### 1.3 Extract Signal-to-PRD Bridge Data

Map Signal fields to PRD sections using the bridge table in SKILL.md.

## Exit Criteria

- [ ] Template loaded
- [ ] All inputs verified and accessible
- [ ] Signal-to-PRD bridge data extracted

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.prd-generation:0.1.2:2026-10-22T08:49:51Z -->
