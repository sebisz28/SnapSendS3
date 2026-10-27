# SNS003 Local Compose Download Verification

**Date:** 2026-10-27 13:15 EET
**Origin:** `https://localhost:8443`
**Runner:** `verify-local-downloads.ps1`

## Results

- API health returned `UP` through Caddy.
- **One file:** created a fresh share, uploaded a 1×1 PNG, finalized it, waited for scan completion, and downloaded it through the public recipient endpoint. The response was an attachment named `sns003-proof.png`; PNG signature and 1×1 dimensions matched, and SHA-256 matched the uploaded bytes (`2684079B6183C4F1795CC91D463A27EBF2900683E28591A16995FBB1B55D3581`).
- **Multiple files:** created a fresh share, uploaded `one.txt` and `two.txt`, finalized it, waited for scan/archive completion, and downloaded it through the public recipient endpoint. The response was an attachment named `snapsends3-share.zip`; the ZIP opened successfully and both extracted contents matched the uploaded UTF-8 bytes. Entry names use the archive writer's `NNN-<original name>` prefix (this run: `001-one.txt`, `002-two.txt`). Archive SHA-256 for this run: `AE275C643F9D6F70C89BBA0B051895902442C9B7BB6B44240A383A8FEEF1E1EF`.
- The API regression suite passed separately with 15 tests, zero failures, and zero errors (`mvn.cmd verify`, 2026-10-27).

## Reproduction

Start the local stack with `docker compose up --build -d`, then run:

```powershell
pwsh -NoProfile -File work/04-implementing/SNS003-fix-invalid-single-file-download/evidence/verify-local-downloads.ps1
```

The script uses only the local demo Sender account and generated test content. It creates two short-lived local shares. Its certificate bypass applies to the local development HTTPS endpoint used for this check.
