package com.moat.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app")
public record AuthProperties(Jwt jwt, Cookie cookie) {

    public record Jwt(String secret, Duration expiry) {
    }

    public record Cookie(String name, boolean secure, String sameSite) {
    }
}
