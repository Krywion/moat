package com.moat.auth;

import com.moat.user.Role;
import com.moat.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "test-secret-at-least-32-bytes-long-0123456789";
    private final AuthProperties props = new AuthProperties(
            new AuthProperties.Jwt(SECRET, Duration.ofHours(24)),
            new AuthProperties.Cookie("auth_token", false, "Strict"));

    private final JwtConfig jwtConfig = new JwtConfig();
    private final JwtService jwtService = new JwtService(jwtConfig.jwtEncoder(props), props);
    private final JwtDecoder decoder = jwtConfig.jwtDecoder(props);

    @Test
    void issuedToken_containsSubjectAndRole() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("alice@example.com");
        user.setRole(Role.USER);

        String token = jwtService.issueToken(user);

        Jwt decoded = decoder.decode(token);
        assertThat(decoded.getSubject()).isEqualTo(user.getId().toString());
        assertThat(decoded.getClaimAsString("role")).isEqualTo("USER");
    }
}
