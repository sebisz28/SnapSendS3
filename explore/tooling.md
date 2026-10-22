# Tooling

## Application stack

| Area | Standard |
| --- | --- |
| Backend | Java 21 with Spring Boot |
| Frontend | Angular with TypeScript |
| Relational data | PostgreSQL |
| Object storage | Cloudflare R2 through its S3-compatible API; buckets remain private |
| Build | Maven, using Spring Boot dependency management |
| Database migrations | Flyway |
| Backend testing | JUnit 5, Mockito, Spring Boot Test, and Testcontainers for PostgreSQL |
| Object-storage testing | An S3-compatible R2 integration test |
| Local development | Docker Compose for PostgreSQL and, where required, an S3-compatible emulator |
| Production delivery | Containerised Spring Boot API and Angular static build behind an HTTPS reverse proxy or managed platform |

## Constraints

- Do not introduce a separate file-storage runtime: Cloudflare R2 is the object store.
- R2 buckets are private. The backend issues narrowly scoped, short-lived S3 presigned URLs for direct transfers.
- Production public entry points must be served over HTTPS.
