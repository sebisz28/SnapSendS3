# Locked Domain Model: SnapSendS3 External Document Sharing

**Status**: Locked after PRD approval  
**Source**: `explore/explore-external-document-sharing/domain-analysis.md`  
**Approved by**: Sebastian Szekely on 2026-10-22

## Canonical Entities

```text
[Sender identity] (1) ── creates ──< [Share]
                                      ├── composes ──< [Share File] (1..*)
                                      ├── records ──< [Download Attempt] (0..*)
                                      └── produces ── [Archive] (0..1)
```

| Entity | Purpose | Key rules |
|---|---|---|
| Share | Six-hour external access aggregate. | Ready only after every file is clean and any required ZIP is ready. |
| Share File | Private uploaded R2 object. | Cannot be delivered or archived before Clean. |
| Archive | Derived ZIP for multi-file Share. | Required for 2+ files; private; generated from clean files only. |
| Download Attempt | Minimal public-access audit event. | Never stores password or download grant. |
| Sender identity | Keycloak identity reference. | Only authenticated Sender may create Share. |

## Canonical Lifecycle

`Draft → Uploading → Scanning → Ready → Expired → Deleted`  
`Scanning → Rejected → Deleted`

Invalid: Uploading → Ready, Scanning → Ready while any file is not Clean, Expired → Ready, and any transition from Deleted.

## Canonical Rules

1. `expiresAt = createdAt + 6 hours` and access is denied at or after expiry.
2. R2 originals and archives remain private; only short-lived grants leave the API.
3. Scan failure, pending scan, and infection fail closed.
4. Two or more files are delivered as one ZIP only.
5. Optional password is stored as a hash and is checked before a grant.
6. Cleanup deletes originals, archive and metadata idempotently; failures retry.

## Open Domain Decisions

The configured maximum size, per-file versus per-Share interpretation, R2 usage guardrail, scanner timeout policy, Keycloak sender claim, production topology and compliance policy remain open. See the approved PRD for owners and target resolution.

