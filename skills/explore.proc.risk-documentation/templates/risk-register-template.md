+++
template_name = "Risk Register Template"
version = "1.0"
output_format = "explore/explore-[slug]/risks.md"
validation_required = true
+++

# Risk Register: {Project Name}

**Signal**: {signal-id}  
**Phase**: SP4 — Risk Assessment  
**Created**: {YYYY-MM-DD}  
**Status**: {Active / Monitoring / Closed}  

---

## Risk Summary

| Category | Total Risks | Critical | High | Medium | Low | Mitigated |
|----------|-------------|----------|------|--------|-----|-----------|
| Technical | {N} | {N} | {N} | {N} | {N} | {N} |
| Business | {N} | {N} | {N} | {N} | {N} | {N} |
| Operational | {N} | {N} | {N} | {N} | {N} | {N} |
| Compliance | {N} | {N} | {N} | {N} | {N} | {N} |
| **Total** | **{N}** | **{N}** | **{N}** | **{N}** | **{N}** | **{N}** |

**Risk Score**: {Total risk score} / {Maximum possible score}  
**Overall Risk Level**: {Critical / High / Medium / Low}

---

## Risk Assessment Matrix

### Impact Levels

| Level | Description | Examples |
|-------|-------------|----------|
| **Critical** | Project failure, >3 month delay, >50% cost overrun | Complete architecture redesign required, regulatory blocker, key stakeholder withdrawal |
| **High** | Major delay (1-3 months), 20-50% cost overrun, significant scope reduction | Major integration failure, critical resource unavailable, security breach |
| **Medium** | Moderate delay (2-4 weeks), 10-20% cost increase, minor scope reduction | Performance issues, minor integration delays, team skill gaps |
| **Low** | Minor delay (<2 weeks), <10% cost impact, workarounds available | UI polish issues, minor bugs, documentation gaps |

### Likelihood Levels

| Level | Probability | Description |
|-------|-------------|-------------|
| **Very High** | >80% | Almost certain to occur, no clear mitigation |
| **High** | 60-80% | Likely to occur, known issues, mitigation challenging |
| **Medium** | 30-60% | May occur, some uncertainty, mitigation possible |
| **Low** | 10-30% | Unlikely but possible, well-controlled |
| **Very Low** | <10% | Rare scenario, highly controlled |

### Risk Score Calculation

**Risk Score** = Impact (1-5) × Likelihood (1-5)

| Score Range | Risk Level | Action Required |
|-------------|-----------|-----------------|
| 20-25 | Critical | Immediate action, escalate to leadership |
| 15-19 | High | Urgent mitigation, weekly monitoring |
| 8-14 | Medium | Active mitigation, bi-weekly monitoring |
| 4-7 | Low | Monitor, contingency plan ready |
| 1-3 | Very Low | Accept, periodic review |

---

## Technical Risks

### T1: {Risk Title}

**Risk ID**: T1  
**Category**: Technical  
**Status**: {Open / Monitoring / Mitigated / Closed}  

**Description**: {Detailed description of the technical risk}

**Impact**: {Critical / High / Medium / Low} (Score: {1-5})  
**Likelihood**: {Very High / High / Medium / Low / Very Low} (Score: {1-5})  
**Risk Score**: {Impact × Likelihood}

**Consequences if realized**:
- {Consequence 1}
- {Consequence 2}
- {Consequence 3}

**Root causes**:
- {Root cause 1}
- {Root cause 2}

**Triggers/Warning signs**:
- {Trigger 1 - what would indicate this risk is materializing}
- {Trigger 2}

**Mitigation strategy**:
- {Mitigation action 1 - specific, actionable step}
- {Mitigation action 2}
- {Mitigation action 3}

**Contingency plan** (if risk occurs):
- {Contingency action 1}
- {Contingency action 2}

**Owner**: {Stakeholder name}  
**Review date**: {YYYY-MM-DD}  
**Dependencies**: {Related risks: T2, B1, etc.}

---

### T2: {Risk Title}

**Risk ID**: T2  
**Category**: Technical  
**Status**: {Open / Monitoring / Mitigated / Closed}  

{Repeat structure from T1}

---

## Business Risks

### B1: {Risk Title}

**Risk ID**: B1  
**Category**: Business  
**Status**: {Open / Monitoring / Mitigated / Closed}  

**Description**: {Detailed description of the business risk}

**Impact**: {Critical / High / Medium / Low} (Score: {1-5})  
**Likelihood**: {Very High / High / Medium / Low / Very Low} (Score: {1-5})  
**Risk Score**: {Impact × Likelihood}

**Consequences if realized**:
- {Consequence 1}
- {Consequence 2}

**Root causes**:
- {Root cause 1}
- {Root cause 2}

**Triggers/Warning signs**:
- {Trigger 1}
- {Trigger 2}

**Mitigation strategy**:
- {Mitigation action 1}
- {Mitigation action 2}

**Contingency plan** (if risk occurs):
- {Contingency action 1}
- {Contingency action 2}

**Owner**: {Stakeholder name}  
**Review date**: {YYYY-MM-DD}  
**Dependencies**: {Related risks}

---

### B2: {Risk Title}

{Repeat structure from B1}

---

## Operational Risks

### O1: {Risk Title}

**Risk ID**: O1  
**Category**: Operational  
**Status**: {Open / Monitoring / Mitigated / Closed}  

{Repeat structure from T1}

---

### O2: {Risk Title}

{Repeat structure from T1}

---

## Compliance Risks

### C1: {Risk Title}

**Risk ID**: C1  
**Category**: Compliance  
**Status**: {Open / Monitoring / Mitigated / Closed}  

{Repeat structure from T1}

---

### C2: {Risk Title}

{Repeat structure from T1}

---

## Risk Dependencies

**Risk interaction map**:

```
T1 (Tech stack selection) → T2 (Integration complexity)
                          → O1 (Team skill gaps)

B1 (Market adoption) → B2 (Revenue targets)
                    → O2 (Support capacity)

C1 (Regulatory compliance) → T3 (Architecture constraints)
                           → O3 (Audit requirements)
```

**Cascading risks**:
- If {Risk ID} occurs, it increases likelihood of {Risk ID} by {%}
- If {Risk ID} occurs, it increases impact of {Risk ID} to {level}

---

## Mitigation Timeline

| Week | Risk ID | Mitigation Action | Owner | Status |
|------|---------|-------------------|-------|--------|
| Week 1 | T1, B1 | {Action} | {Owner} | ⚠️ In Progress |
| Week 2 | T2, O1 | {Action} | {Owner} | ⏳ Planned |
| Week 3 | C1 | {Action} | {Owner} | ⏳ Planned |
| Week 4 | Review all | Risk review meeting | PM | ⏳ Planned |

**Status Legend**:
- ✅ Complete = Mitigation implemented
- ⚠️ In Progress = Actively working on mitigation
- ⏳ Planned = Scheduled but not started
- ❌ Blocked = Cannot proceed, needs resolution

---

## Risk Monitoring

### Review Schedule

| Frequency | Participants | Focus |
|-----------|--------------|-------|
| Weekly | PM, Tech Lead | Critical and High risks |
| Bi-weekly | Full team | All active risks |
| Monthly | Stakeholders | Overall risk posture, new risks |

### Key Performance Indicators

| KPI | Target | Current | Trend |
|-----|--------|---------|-------|
| Average risk score | <10 | {Current score} | {↑ / → / ↓} |
| Critical risks | 0 | {N} | {↑ / → / ↓} |
| High risks | <3 | {N} | {↑ / → / ↓} |
| Risks mitigated on time | >80% | {%} | {↑ / → / ↓} |
| New risks identified | - | {N this period} | {↑ / → / ↓} |

---

## Retired Risks

**Risks that have been closed**:

| Risk ID | Title | Closed Date | Reason |
|---------|-------|-------------|--------|
| {ID} | {Title} | {YYYY-MM-DD} | {Mitigated / No longer applicable / Accepted} |
| {ID} | {Title} | {YYYY-MM-DD} | {Reason} |

---

## Lessons Learned

**From this risk assessment**:
- {Lesson 1 - what we learned about risk identification}
- {Lesson 2 - what we learned about mitigation effectiveness}
- {Lesson 3 - what we would do differently}

**For future projects**:
- {Recommendation 1}
- {Recommendation 2}

---

## Related Artifacts

- **Signal**: `signal/signals/signal-{source}-{slug}.md`
- **Context**: `explore/explore-{slug}/context.md`
- **Hypothesis**: `explore/explore-{slug}/hypothesis.md`
- **HLD**: `explore/hlds/{slug}-hld.md`
- **ADRs**: `explore/decisions/{slug}-adr-###-{name}.md`
- **PRD**: `explore/prds/{slug}-prd.md`

---

## Enrichment Log

| Date | Trigger | Change |
|------|---------|--------|
| {YYYY-MM-DD} | SP4 completion | Initial risk register created |
| {YYYY-MM-DD} | Risk review | {What changed - new risks, status updates, mitigation progress} |
| {YYYY-MM-DD} | {Event} | {What changed} |

---

**Last Updated**: {YYYY-MM-DD}  
**Updated By**: {Agent or Human name}  
**Next Review**: {YYYY-MM-DD}

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.risk-documentation:0.2.1:2026-10-22T08:59:43Z -->
