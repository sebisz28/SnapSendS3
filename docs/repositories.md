# Repositories

## SnapSendS3

**Status:** Application scaffold lives in this warehouse under `apps/api` and `apps/web`.

SnapSendS3 is the dava.flow web application for warehouse operations and secure external document sharing. It will use Java 21 with Spring Boot, an Angular/TypeScript frontend, PostgreSQL, and private Cloudflare R2 object storage via the S3-compatible API.

The API uses Maven (`apps/api/pom.xml`) and the Angular frontend uses npm (`apps/web/package.json`). `docker compose up --build` starts PostgreSQL, LocalStack S3, ClamAV, Keycloak, the API, and a Caddy HTTPS edge at `https://localhost:8443`. The imported local Keycloak demo Sender is `sender` / `sender`; its access token contains the `dava.sender` role.
