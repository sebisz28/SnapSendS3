# Step 3: Draft Component Breakdown and Integration

Generate the structural core: components, external integrations, internal data flows, and key flows.

```
HLD: Component Breakdown + Integration & Data Flows

## Component Breakdown
| Component | Responsibility | Technology | Dependencies |
|-----------|---------------|------------|--------------|
| [Component] | [What it does] | [Tech or TBD] | [Dependencies] |

⚙️ Architecture rules enforced:
- [✓/✗] Bounded-context ownership: no blurred boundaries
- [✓/✗] Human approvals and corrections modeled as core flows
- [✓/✗] Each component has clear responsibility boundary

## Integration & Data Flows

External Integrations:
| System | Integration Type | Purpose | Protocol |
|--------|-----------------|---------|----------|
| [System] | [Type] | [Purpose] | [Protocol or TBD] |

Internal Data Flows:
[ASCII diagram showing data flow between components]

Key Flows:
1. [Flow name]: [Source] → [Destination]
   - Trigger: [What initiates]
   - Data: [What moves]
   - Outcome: [Result]

⚠️ Assumed contracts flagged for blocker register: [N]

How would you like to proceed?
- Correct — proceed to decisions and tech stack
- Adjust components — tell me which component and what to change
- Adjust flows — tell me which integration or flow is wrong
- Flag blocker — an assumed contract needs escalation
```

**STOP**: Wait for architect to validate component breakdown and integration design.

<!-- dft:verified:fWm53NXvAL8kKDRioRsB8/0AeutlimfUyK52g1Rn8N4=:explore.proc.hld-drafting:0.2.1:2026-10-22T08:59:42Z -->
