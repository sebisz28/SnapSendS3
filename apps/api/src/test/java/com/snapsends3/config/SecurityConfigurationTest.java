package com.snapsends3.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityConfigurationTest {
    @Test
    void mapsTheKeycloakSenderRoleToASpringAuthority() {
        Jwt token = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60))
                .claim("roles", List.of("dava.sender"))
                .build();

        assertThat(new SecurityConfiguration().rolesConverter().convert(token))
                .extracting(authority -> authority.getAuthority())
                .containsExactly("ROLE_dava.sender");
    }
}
