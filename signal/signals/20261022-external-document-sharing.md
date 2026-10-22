---
date: 20261022
title: "External document sharing"
state: Active
source: "User conversation, 2026-10-22"
type: "Opportunity"
severity: 3
resonance: 2
sponsor: "Engineering Lead"
explore_type: "Explore Readiness Check"
---

## The Signal

Authenticated dava.flow users need a secure way to send files to recipients outside their local network without relying on an agreed external file-transfer service. `[ASSUMPTION]`

## Initial Evidence

- The need identified in this session is to upload documents and share download links with people outside the Wi-Fi or network. `[FACT - source: User conversation, 2026-10-22]`
- This capture contains no incidents, user requests, file-volume data, or measured process delays. `[FACT - source: User conversation, 2026-10-22]`
- The ability of the current hosting setup to provide public HTTPS access and external object storage remains unverified. `[FACT - source: User conversation, 2026-10-22]`
- The proposed scope is to let authenticated users upload files of any type, subject to a 10 GB maximum per upload. `[ASSUMPTION]`
- No named requester, existing workaround, projected volume, file-size profile, or concrete operational exchange has been confirmed. `[FACT - source: User conversation, 2026-10-22]`
- An illustrative scenario is an authenticated user sending a packing list and delivery photos to an external carrier; it assumes two to five files totalling 20–100 MB, a few times each week. `[ASSUMPTION]`

## Why This Matters

Secure external sharing could simplify the exchange of operational documents with customers, suppliers, and carriers. `[OPINION - rationale: It would replace an unconfirmed dependency on an external file-transfer service]`

The business effect on speed, cost, security, and service quality has not been measured. `[FACT - source: User conversation, 2026-10-22]`

## Actors

| Actor | Role | Impact |
|---|---|---|
| Authenticated dava.flow users | Upload and share files | Proposed to send any file type up to 10 GB externally. |
| External recipients | Download shared documents | May receive documents as customers, suppliers, or carriers; exact groups are unconfirmed. |
| External carrier | Illustrative recipient | Could receive a packing list and delivery photos in an unvalidated scenario. |
| Engineering Lead | Owns the pursue/no-pursue decision | Confirmed sponsor with appetite to pursue now. |

## Constraints

- Uploads are proposed for authenticated users and any file type, with a maximum of 10 GB per upload. `[ASSUMPTION]`
- Shared data must be retained for six hours. `[FACT - source: User conversation, 2026-10-22]`
- Uploaded files require malware scanning. `[FACT - source: User conversation, 2026-10-22]`
- A share containing two or more files must be downloaded as a ZIP, RAR, or other archive. `[FACT - source: User conversation, 2026-10-22]`
- The illustrative scenario assumes email attachments today and a personal file-sharing service when attachments are too large. `[ASSUMPTION]`
- Expected file volumes, link controls, and recipient needs are unknown. `[FACT - source: User conversation, 2026-10-22]`
- Public HTTPS reachability and private external object storage support are unverified. `[FACT - source: User conversation, 2026-10-22]`

## Completion Criteria Status

- [x] 1. Fundamentals — Complete: opportunity, source, preliminary importance, and duplicate check are recorded.
- [ ] 2. Evidence & Context — Partial: the initial need and illustrative scenario are captured, but corroborating evidence and operational data are absent; validate these during Explore.
- [ ] 3. Framing & Meaning — Partial: authenticated senders, external recipients, unrestricted file types, a proposed 10 GB maximum, multi-file archive downloads, and an illustrative carrier exchange are known; scope and outcomes need confirmation.
- [ ] 4. Strategic Alignment — Partial: Engineering Lead is the sponsor with an appetite to pursue now; North Star linkage and wider regulatory assessment are unconfirmed.
- [ ] 5. Readiness & Feasibility — Partial: urgency, six-hour retention, and malware-scanning requirements are known; hosting, storage, and security feasibility are unverified.
- [x] 6. Prioritisation — Complete: importance is 3/5 because coordination may improve but impact is unmeasured; urgency is 2/5 because no deadline, incident, or delay is known; position is act now by sponsor direction.
- [x] 7. Explore Type — Complete: Explore Readiness Check is selected to validate the named assumptions before implementation planning.

## Completion Notes

| Date | Session | Changes |
|---|---|---|
| 20261022 | Initial capture | Recorded an external document-sharing opportunity, preliminary importance and urgency, known actors, and evidence gaps. |
| 20261022 | Strengthen | Added proposed authenticated-user access, unrestricted file types, and a 10 GB upload limit as assumptions. |
| 20261022 | Strengthen | Assigned the Engineering Lead as sponsor. |
| 20261022 | Strengthen | Recorded sponsor appetite to pursue now, six-hour retention, and required malware scanning. |
| 20261022 | Strengthen | Recorded that two or more files must be downloaded as one archive. |
| 20261022 | Strengthen | Confirmed that no corroborating evidence is currently available; the opportunity remains unvalidated. |
| 20261022 | Strengthen | Added an illustrative packing-list and delivery-photo exchange as an assumption, not evidence. |
| 20261022 | Strengthen | Sponsor directed that the signal move forward at Medium confidence through an Explore Readiness Check. |

## Signal Summary

| Field | Content |
|---|---|
| Problem | Authenticated dava.flow users may need to send files securely to recipients outside their local network. `[ASSUMPTION]` |
| Hypothesis | Secure external sharing could simplify operational-document exchange with external parties. `[OPINION - rationale: It would replace an unconfirmed dependency on an external file-transfer service]` |
| Confidence | Medium — the sponsor directed the opportunity to proceed and the use case, scope, constraints, and prioritisation are defined; corroborating demand remains a validation item. |
| Key Evidence | The need was identified in this session; no incidents, demand data, or operational measurements have been provided. The carrier scenario is illustrative only. `[FACT - source: User conversation, 2026-10-22]` |
| Critical Assumptions | External recipients need this capability; authenticated users may upload any file type; a 10 GB maximum is appropriate. `[ASSUMPTION]` |
| Actors | Authenticated dava.flow users, external recipients, and the Engineering Lead sponsor. |
| Constraints | Six-hour retention, malware scanning, and archive download for two or more files; hosting and compliance feasibility are unverified. |
| Importance / Urgency | Importance 3/5 because coordination may improve but business impact is unmeasured; urgency 2/5 because no deadline, incident, or delay is known. |
| Explore Type | Explore Readiness Check — this is a bounded opportunity with a clear sponsor and initial constraints, but the demand and operational evidence must be validated during Explore. |
| Sponsor | Engineering Lead; appetite: pursue now. |

## Explore Type Assessment

| Lane | Score | Rationale |
|---|---:|---|
| Fast Lane | 2/5 | Sponsor and a bounded initial scenario exist, but corroborating demand and feasibility evidence are incomplete. |
| Explore Readiness Check | 4/4 | A sponsor, problem hypothesis, initial actors, constraints, and validation gaps are explicit. |
| Diverge-Converge | 2/5 | The opportunity has open questions, but not enough competing problem framings to justify broad divergence. |

## Routing Decision

**Decision:** Route to Explore — Explore Readiness Check  
**Approver:** Sebastian Szekely (`Sebastian.Szekely@endava.com`)  
**Date:** 2026-10-22  
**Timeline expectation:** Begin immediately.

**Rationale:** The Engineering Lead has directed that this bounded opportunity proceed now. The initial use case, constraints, prioritisation, sponsor, and validation gaps are documented. `[FACT - source: User conversation, 2026-10-22]`

### Route-Readiness Verification

| Criterion | Result | Notes |
|---|---|---|
| 1. Fundamentals | Pass | Opportunity type, source, importance, and duplicate check are recorded. |
| 2. Evidence & Context | Conditional | The scenario is illustrative; demand, workaround, and operating data must be validated during Explore. |
| 3. Framing & Meaning | Conditional | The initial actors and constraints are clear; final scope and outcomes need confirmation. |
| 4. Strategic & Market Alignment | Conditional | Sponsor appetite is confirmed; North Star linkage and regulatory assessment remain Explore activities. |
| 5. Readiness & Feasibility | Conditional | Retention, malware scanning, and packaging are known; deployment, storage, security, and operational feasibility must be validated. |
| 6. Prioritisation & Positioning | Pass | Importance 3/5, urgency 2/5, and sponsor-directed act-now position are documented. |
| 7. Explore Type | Pass | Explore Readiness Check is selected and scored. |

**Routing condition:** Proceed by sponsor direction at Medium confidence. The conditional items above are mandatory validation activities in Explore and do not represent confirmed facts. `[FACT - source: User conversation, 2026-10-22]`

### Suggested Explore Activities

1. Validate the representative external-carrier workflow with a real requester and collect expected volume, file-size, and frequency data.
2. Confirm sender authorization, recipient and link controls, retention, audit, privacy, and regulatory requirements.
3. Assess public HTTPS, private object storage, malware-scanning, archive-generation, and operational feasibility against the existing deployment environment.
