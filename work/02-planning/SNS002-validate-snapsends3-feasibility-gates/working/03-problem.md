# Problem Capture

## Problem Statement

SnapSendS3 cannot safely begin feature implementation until its four external safety/capacity contracts are evidenced. The desired result is reproducible proof that private R2 transfers, Keycloak sender authorization, ClamAV terminal behavior, and Caddy/ZIP limits match the approved design.

## Stakeholders

- Sebastian Szekely — product, architecture, engineering, and demo authority.
- Sender and Recipient — indirect users protected by the gates.

## Affected Components

- Spring Boot R2, Keycloak, clamd, archive, lifecycle, and configuration adapters.
- Caddy local-demo route and limits.
- Cross-cutting integration, security, and test infrastructure.

## Initial Questions

- What local R2 endpoint/credentials and bucket policy will be supplied?
- What Keycloak realm/client/issuer/audience exposes `roles:dava.sender`?
- Where does clamd run and which protocol/limits are available?
- What Caddy TLS host, body-size, and timeout settings are available?

## Existing Context

- `explore/hlds/external-document-sharing-blocker-register.md`
- `explore/hlds/external-document-sharing-hld.md`
- `explore/explore-external-document-sharing/test-strategy.md`
