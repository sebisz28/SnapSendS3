# SnapSendS3 API conventions

- API paths and DTO contracts live in `src/main/resources/openapi/schema.yml`.
- Maven OpenAPI Generator creates API interfaces and DTOs at build time under `target/generated-sources/openapi`.
- Controllers implement generated interfaces and contain no `@RequestMapping`, `@GetMapping`, or `@PostMapping` annotations.
- Lombok is the default for Java boilerplate such as immutable DTOs and entities.
- Persisted domain state uses Spring Data JPA; controllers do not access repositories directly.
- Database changes are versioned with Flyway; Hibernate validates but never creates schema.
