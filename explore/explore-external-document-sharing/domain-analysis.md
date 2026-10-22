# Domain Analysis: SnapSendS3 external document sharing

**Project**: dava.flow — SnapSendS3  
**Created**: 2026-10-22  
**Last Updated**: 2026-10-22  
**Status**: Draft

---

## 1. Domain Glossary

### Core Domain Terms

| Term | Definition | Synonyms (Avoid) | Context | Examples |
|---|---|---|---|---|
| **Sender** | A Keycloak-authenticated user who creates a Share. | uploader, owner | Sender-facing application flow | Warehouse user sending a packing list. |
| **Recipient** | An external, unauthenticated person who accesses a Share using its public link. | guest, downloader | Public download flow | Carrier receiving delivery photos. |
| **Share** | A time-limited grant that groups one or more files for external delivery. | transfer, package | Central aggregate | A six-hour link for a carrier. |
| **Share File** | An uploaded file belonging to a Share and stored as a private R2 object. | attachment | Upload, scanning and cleanup | `packing-list.pdf`. |
| **Archive** | A ZIP representation produced for a Share containing two or more files. | bundle, package | Recipient download | ZIP of photos and packing list. |
| **Public link** | An opaque URL containing the Share token, not an R2 object URL. | share URL | Recipient entry point | `https://…/s/<token>`. |
| **Download grant** | A short-lived, R2-compatible download URL issued after Share access checks pass. | presigned URL | Byte delivery | A grant for an approved ZIP. |
| **Scan gate** | The rule that blocks delivery until every Share File is reported clean by ClamAV. | malware gate | Security control | A file awaiting scan cannot download. |

### Domain Actions

| Action | Definition | When Used | Examples |
|---|---|---|---|
| **Create share** | Start a Share and establish its six-hour expiry. | Authenticated sender begins transfer. | Creates opaque token and metadata. |
| **Upload** | Transfer a selected file to private R2 storage. | While Share is Uploading. | Browser sends `photo.jpg` using a scoped grant. |
| **Scan** | Inspect an uploaded file for malware. | After an upload completes. | ClamAV returns clean or infected. |
| **Archive** | Build a ZIP from clean Share Files. | Share has two or more files. | Worker writes `share-123.zip`. |
| **Download** | Obtain a temporary grant and retrieve the approved file or Archive. | Recipient passes access checks. | Recipient downloads ZIP. |
| **Expire** | End access and schedule deletion of Share bytes and metadata. | Six hours after Share creation. | Cleanup removes R2 objects. |

### States and Statuses

| State | Definition | Applies To | Next States |
|---|---|---|---|
| **Uploading** | Share accepts file uploads. | Share | Scanning, Rejected, Expired |
| **Scanning** | At least one uploaded file awaits or undergoes ClamAV scanning. | Share, Share File | Ready, Rejected, Expired |
| **Ready** | All files are clean and required Archive is ready. | Share | Expired |
| **Rejected** | A file is infected or cannot be safely made available. | Share | Expired |
| **Expired** | The Share is no longer downloadable after its retention limit. | Share | Deleted |
| **Deleted** | Metadata and known private objects have been removed. | Share, Share File, Archive | none |

### Roles and Actors

| Role | Definition | Responsibilities | Not To Be Confused With |
|---|---|---|---|
| **Sender** | Domain role creating a Share. | Select files and initiate sharing. | An organizational job title. |
| **Recipient** | Domain role retrieving an approved Share. | Open link; supply password if configured. | A Keycloak user. |
| **Scan worker** | Automated domain actor that obtains ClamAV results. | Change file scan state. | A human security reviewer. |
| **Archive worker** | Automated actor that builds required ZIPs. | Create Archive after clean results. | A recipient-side extraction tool. |
| **Cleanup worker** | Automated actor that removes expired data. | Delete private R2 objects and metadata. | A retention-policy approver. |

---

## 2. Domain Model

### Core Entities

**Share**

- **Description**: The aggregate that controls external access to uploaded files.
- **Category**: Core.
- **Key Attributes**: `shareId` (UUID), `publicToken` (opaque unique string), `senderSubject` (Keycloak subject), `createdAt`, `expiresAt`, `passwordHash` (optional), `status`.
- **Lifecycle States**: Draft, Uploading, Scanning, Ready, Rejected, Expired, Deleted.
- **Relationships**: One Sender creates many Shares; one Share composes one or more Share Files, many Download Attempts, and zero or one Archive.
- **Business Rules**: It expires exactly six hours after creation; it is downloadable only when Ready.

**Share File**

- **Description**: An original uploaded object governed by its Share.
- **Category**: Core.
- **Key Attributes**: `fileId`, `originalName`, `declaredContentType`, `sizeBytes`, `objectKey`, `scanStatus`, `scanCompletedAt`.
- **Lifecycle States**: PendingUpload, Uploaded, Scanning, Clean, Infected, ScanFailed, Deleted.
- **Relationships**: Belongs to exactly one Share; is stored as one private R2 object.
- **Business Rules**: It cannot be downloadable or added to an Archive before Clean.

**Download Attempt**

- **Description**: An auditable attempt to use a public link.
- **Category**: Transactional.
- **Key Attributes**: `attemptId`, `shareId`, `occurredAt`, `outcome`, `clientIp` (if retained by applicable policy).
- **Lifecycle States**: Immutable event.
- **Relationships**: Belongs to one Share.
- **Business Rules**: It must not contain a supplied password or a download grant.

### Supporting Entities

**Archive**

- **Description**: A derived private ZIP for a multi-file Share.
- **Purpose**: Provides one recipient download when a Share has two or more files.
- **Key Attributes**: `shareId`, `format` (`zip`), `objectKey`, `sizeBytes`, `status`, `createdAt`.
- **Relationships**: Zero or one Archive belongs compositionally to one Share.

**Sender identity reference**

- **Description**: The Keycloak subject and display information used to attribute Share creation.
- **Purpose**: Avoids copying identity management into SnapSendS3.
- **Relationships**: Referenced by Share; Keycloak remains authoritative.

### Reference Data

| Reference Type | Description | Examples | Source |
|---|---|---|---|
| **Share status** | Permitted Share lifecycle values. | Uploading, Scanning, Ready | Application enum. |
| **Scan status** | Result of asynchronous antivirus processing. | Clean, Infected, ScanFailed | Application enum / ClamAV result. |
| **Download outcome** | Access-audit result. | Allowed, PasswordFailed, NotReady, Expired | Application enum. |
| **Archive format** | Supported packaging for a multi-file Share. | ZIP | Product decision. |

### Entity Relationship Diagram (Text-Based)

```text
[Sender identity reference] (1) ── creates ──< [Share]
                                             ├── composes ──< [Share File] (1..*)
                                             ├── records ──< [Download Attempt] (0..*)
                                             └── produces ── [Archive] (0..1)

[Share File] ── stored privately as ──> [Cloudflare R2 object]
[Archive] ── stored privately as ──> [Cloudflare R2 object]
```

---

## 3. Entity Lifecycle States

### Share Lifecycle

| State | Entry condition | Allowed actions | Exit condition |
|---|---|---|---|
| **Draft** | Server creates metadata before uploads. | Allocate scoped upload grants. | First upload begins or Share is abandoned. |
| **Uploading** | Sender is uploading one or more files. | Upload files; finalize upload set. | Sender finalizes → Scanning; expiry → Expired. |
| **Scanning** | Upload set is finalized. | Scan files; create Archive only after all clean. | All clean and archive ready → Ready; infected/failed policy outcome → Rejected; expiry → Expired. |
| **Ready** | Every file is Clean; multi-file Archive is Ready. | Verify password when configured; issue short-lived download grant. | Expiry → Expired. |
| **Rejected** | A file is infected, or safe processing cannot complete. | Show unavailable state; schedule cleanup. | Expiry/cleanup → Deleted. |
| **Expired** | `now >= expiresAt`. | No download or new grants. | Cleanup succeeds → Deleted. |
| **Deleted** | Metadata and tracked objects are removed. | None. | Terminal. |

**Valid transitions**: Draft → Uploading → Scanning → Ready → Expired → Deleted; Scanning → Rejected → Deleted; Uploading/Scanning/Ready → Expired.

**Invalid transitions**: Uploading → Ready (scan bypass); Scanning → Ready while any file is not Clean; Expired → Ready; Deleted → any state.

### Share File Lifecycle

`PendingUpload → Uploaded → Scanning → Clean | Infected | ScanFailed → Deleted`.

A file can move to Deleted from any nonterminal state when its parent Share expires. Clean means the scanner reported no malware; it does not establish a complete content-policy decision for arbitrary file types.

### Archive Lifecycle

`NotRequired` applies to a single-file Share. For a multi-file Share: `Pending → Building → Ready | Failed → Deleted`. Archive creation is forbidden while any member file is not Clean.

---

## 4. Domain Rules and Constraints

### Business Rules

| Code | Rule | Applies to | Validation and exception handling |
|---|---|---|---|
| **BR-1** | Only a Keycloak-authenticated Sender may create a Share. | Share creation | Validate token subject; reject unauthenticated request. |
| **BR-2** | A Share contains at least one Share File. | Finalization | Reject finalization with zero files. |
| **BR-3** | `expiresAt = createdAt + 6 hours`. | Share | Calculate server-side; deny access at or after expiry. |
| **BR-4** | No recipient download is allowed before every Share File is Clean. | Public access | Return unavailable; never issue a download grant. |
| **BR-5** | A Share with two or more files downloads as one ZIP Archive. | Archive/download | Create ZIP after clean scans; withhold access until Ready. |
| **BR-6** | R2 objects are private; a recipient never receives a permanent object URL. | Storage/access | Use short-lived, scoped grant only after Share checks. |
| **BR-7** | A configured password is optional but, when present, must be verified before a grant. | Public access | Store a password hash; record failed outcome without recording secret. |
| **BR-8** | A file or Share exceeding configured limits must not complete. | Upload | Enforce server-side declared and observed limits; report validation failure. |
| **BR-9** | Expired private objects and derived archives must be deleted. | Retention | Cleanup retries failures and records exceptions for review. |

### Constraints

| Code | Constraint | Type | Impact / rationale |
|---|---|---|---|
| **C-1** | Java 21, Spring Boot, Angular/TypeScript and PostgreSQL are the product baseline. | Technical | Constrains implementation choices. |
| **C-2** | Cloudflare R2 holds private file and archive objects. | Technical | Requires S3-compatible credentials and object cleanup. |
| **C-3** | ClamAV runs asynchronously. | Technical / security | Public delivery remains blocked pending results. |
| **C-4** | Java ZIP worker creates multi-file archive. | Technical / business | RAR or other archive support is not committed. |
| **C-5** | Caddy is the local-demo HTTPS edge. | Technical | Production topology remains unvalidated. |
| **C-6** | Maximum upload is proposed as 10 GB. | Business assumption | Every proxy, API, R2, scanner and archive stage must be validated. |
| **C-7** | Recipient identity checks, download limits and revocation are out of scope. | Product scope | Optional password and expiry are the only public-link controls. |

### Invariants (Always True)

1. **Private storage**: Every original and derived object is private. Enforced by R2 configuration and scoped grant generation.
2. **No scan bypass**: A download grant is issued only for a Ready Share. Enforced by transactional status check before grant creation.
3. **No access after expiry**: A request at or after `expiresAt` cannot download. Enforced by application check independent of cleanup completion.
4. **No secret persistence in audit**: Password values and presigned URLs are never stored in Download Attempts. Enforced by audit-event schema.

### Calculations and Validation Rules

| Calculation / field | Rule | Result |
|---|---|---|
| **Share expiry** | `createdAt + 6 hours` | `expiresAt`. |
| **File-count packaging** | `fileCount >= 2` | Require ZIP; otherwise deliver the single clean file. |
| **Maximum upload** | Proposed aggregate or per-file scope requires product clarification. | Enforce configured maximum; do not assume the interpretation. |
| **publicToken** | High-entropy opaque value, unique among active Shares. | Recipient-facing lookup key. |

---

## 5. User Roles and Responsibilities

| Role | Category | Responsibilities | Permissions / constraints |
|---|---|---|---|
| **Sender** | Primary user | Create, upload and finalize own Share. | Creates Shares and sees own creation outcome; cannot make R2 data public or override scan results. |
| **Recipient** | Secondary user | Retrieve ready Share through its link. | Reads approved payload only; no login, edit, revocation or ownership functions. |
| **Scan worker** | System role | Submit/obtain antivirus result and change scan status. | Cannot make an infected/failed file Ready. |
| **Archive worker** | System role | Build ZIP from clean Share Files. | Acts only for multi-file Share with all files Clean. |
| **Cleanup worker** | System role | Remove expired objects/metadata. | Cannot extend expiry or restore access. |

### Permission Matrix

| Role | Share | Share File | Archive | Download Attempt | Special actions |
|---|---|---|---|---|---|
| Sender | Create, read own creation result | Create, read own | Read own result | none | Finalize upload. |
| Recipient | Read public availability | none | Download approved payload | creates event | Supply optional password. |
| Scan worker | Update status | Read/update scan status | none | none | Record scan outcome. |
| Archive worker | Read/update status | Read clean files | Create/update | none | Build ZIP. |
| Cleanup worker | Update/delete | Delete | Delete | delete per retention policy | Remove expired R2 objects. |

---

## 6. Current State

The current state is illustrative, not a confirmed operational baseline.

**Illustrative process: external document delivery**

1. A user emails a packing list and delivery photos to an external carrier.
2. If attachments are too large, the user uses a personal file-sharing service.
3. The recipient retrieves the files through that external route.

**Known limitations**: No authenticated application-owned sharing route, consistent retention policy, scan gate, or verified audit trail has been confirmed. Frequency of a few exchanges per week and 20–100 MB across two to five files is illustrative only.

**What must be preserved**: Recipients need a simple no-account download route; the delivery path must work outside the sender’s local network.

---

## 7. Domain Risks and Unknowns

### Unknowns

| Code | Unknown | Why it matters | Resolution / owner | Priority |
|---|---|---|---|---|
| **U-1** | Whether 10 GB means per-file or total per Share. | Determines validation, storage and archive behavior. | Product decision — Sebastian Szekely. | High |
| **U-2** | Actual upload volumes, file types and real workflow demand. | Determines value and sizing. | Validate with future senders — Sebastian Szekely. | High |
| **U-3** | Deployment topology and production HTTPS/R2 credentials. | Determines public reachability and security operations. | Deployment validation — Sebastian Szekely. | High |
| **U-4** | Privacy, audit and regional compliance requirements. | Determines logging and retention policy. | Compliance review — Sebastian Szekely. | High |
| **U-5** | Required behavior after scanner outage or scan failure. | Determines availability/security trade-off. | Architecture decision — Sebastian Szekely. | Medium |

### Assumptions

| Code | Assumption | Risk if wrong | Validation method | Status |
|---|---|---|---|---|
| **A-1** | Anonymous recipient plus optional password is sufficient. | Access controls may be inadequate. | Pilot with intended users. | [NEEDS VALIDATION] |
| **A-2** | Cloudflare R2 supports the necessary private, scoped transfers. | Delivery design must change. | Technical spike with real credentials. | [NEEDS VALIDATION] |
| **A-3** | ClamAV can process representative file sizes in time. | Shares may not become ready before expiry. | Performance test. | [NEEDS VALIDATION] |
| **A-4** | Java ZIP processing is practical for the allowed payload. | Archive worker may exceed local resources. | Capacity test. | [NEEDS VALIDATION] |

### Domain Risks

| Code | Risk | Likelihood | Impact | Mitigation | Owner | Status |
|---|---|---|---|---|---|---|
| **R-1** | A 10 GB payload exceeds a proxy, scanner, worker or local resource limit. | Medium | High | Stream transfers; validate every layer; reject safely on configured limit. | Sebastian Szekely | Open |
| **R-2** | Cleanup fails, leaving private R2 objects beyond six hours. | Medium | High | Idempotent scheduled cleanup, retries and retained failure record. | Sebastian Szekely | Open |
| **R-3** | Scanner outage prevents safe delivery. | Medium | Medium | Fail closed; surface unavailable state; define retry/expiry behavior. | Sebastian Szekely | Open |
| **R-4** | Public token guessing or URL leakage exposes a Share. | Low | High | High-entropy opaque tokens, optional password, HTTPS and short expiry. | Sebastian Szekely | Open |

### Edge Cases to Consider

| Edge case | Handling | Priority |
|---|---|---|
| Upload expires mid-transfer. | Reject completion and clean partial/orphaned object. | High |
| One multi-file member is infected. | Do not create archive or grant any download; clean up at expiry. | High |
| Archive build fails. | Keep Share unavailable; retry within expiry; do not fall back to individual downloads. | High |
| Recipient opens expired link. | Return expired/unavailable response, never an R2 URL. | High |
| Password is supplied incorrectly. | Record safe failure outcome; do not reveal whether a password exists beyond defined UX. | Medium |

---

## 8. Domain Model Summary

**Core entities**: Share is the security and lifecycle aggregate; Share File is the scan-governed original object; Download Attempt provides access evidence. Archive is derived only when the Share has multiple clean files.

**Critical rules**: authenticated creation, six-hour expiry, private objects, clean-only delivery, ZIP for two or more files, and optional password verification.

**Complexity drivers**: asynchronous scanning, large-object streaming and archive creation, reliable cross-system cleanup, and public access without recipient identity.

**Product implications**: sender UI must make scan/ready/expired status clear; recipient UI must be intentionally simple and disclose only availability; upload validation must state the exact configured limit and archive behavior.

**Architecture implications**: make status transitions durable in PostgreSQL; use server-authorized, short-lived R2 grants; keep all object storage private; treat cleanup and scan retries as idempotent background jobs.

---

## 9. Assumptions to Validate

### High priority — before implementation design is finalized

| Assumption | Risk if wrong | Method | Owner | Status |
|---|---|---|---|---|
| 10 GB is feasible across Caddy, Spring, R2, ClamAV and ZIP processing. | Core scope is infeasible or unsafe. | End-to-end technical spike. | Sebastian Szekely | [NEEDS VALIDATION] |
| Private R2 presigned/scoped transfers fit the selected deployment. | Public delivery cannot operate securely. | R2/Caddy deployment test. | Sebastian Szekely | [NEEDS VALIDATION] |
| Six-hour deletion is sufficient for the intended recipients. | Product has poor fit or retention violation. | Stakeholder/pilot validation. | Sebastian Szekely | [NEEDS VALIDATION] |

### Medium priority — before development

| Assumption | Risk if wrong | Method | Owner | Status |
|---|---|---|---|---|
| Anonymous link with optional password is adequate. | Security requirements expand. | Stakeholder review. | Sebastian Szekely | [NEEDS VALIDATION] |
| ZIP is the acceptable archive format for multi-file Shares. | Recipient compatibility issue. | Pilot confirmation. | Sebastian Szekely | [NEEDS VALIDATION] |
| Scan failure should fail closed until expiry. | Security/availability trade-off is unresolved. | Architecture decision record. | Sebastian Szekely | [NEEDS VALIDATION] |

### Validation Plan

| Activity | Method | Timeline | Owner | Dependency |
|---|---|---|---|---|
| Transfer-and-scan spike | Test representative and maximum payloads end to end. | Before build commitment. | Sebastian Szekely | R2, ClamAV and Caddy local configuration. |
| Public-link pilot | Walk through sender and anonymous recipient flows. | Before production promotion. | Sebastian Szekely | Working prototype. |
| Compliance review | Identify applicable privacy/audit rules. | Before production promotion. | Sebastian Szekely | Target deployment details. |

---

## Related Artifacts

- **Signal**: `signal/signals/20261022-external-document-sharing.md`
- **Context**: `explore/explore-external-document-sharing/context.md`
- **Technical feasibility**: `explore/explore-external-document-sharing/technical-feasibility.md`
- **Architecture context**: `explore/explore-external-document-sharing/architecture-context.md`
- **PRD**: `explore/prds/external-document-sharing-prd.md` (pending)

**Last Updated**: 2026-10-22  
**Status**: Draft
