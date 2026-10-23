# SnapSendS3 API conventions

- API paths and DTO contracts live in `src/main/resources/openapi/schema.yml`.
- Maven OpenAPI Generator creates API interfaces and DTOs at build time under `target/generated-sources/openapi`.
- Controllers implement generated interfaces and contain no `@RequestMapping`, `@GetMapping`, or `@PostMapping` annotations.
- Lombok is the default for Java boilerplate such as immutable DTOs and entities.
- Persisted domain state uses Spring Data JPA; controllers do not access repositories directly.
- Database changes are versioned with Flyway; Hibernate validates but never creates schema.
- Optional share passwords are accepted only in JSON request bodies and persisted as salted PBKDF2-HMAC-SHA256 hashes; raw passwords are never stored.
- Public download attempts persist only the Share ID, outcome, and timestamp in `download_attempts`.
- The scheduled cleanup worker deletes expired Share objects and metadata, retrying failed storage cleanup up to three times. Configure R2 lifecycle deletion separately as defense in depth.
- Sender endpoints require a Keycloak JWT with `roles` containing `dava.sender`; the server derives the Share owner from the JWT subject.
- `SNAPSENDS3_STORAGE_BUDGET_BYTES` sets the demo storage budget; the API warns at 80% and rejects new Shares or uploads that would reach or exceed the configured budget. It is not an R2 free-tier quota.
