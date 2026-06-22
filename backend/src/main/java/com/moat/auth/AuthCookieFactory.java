package com.moat.auth;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class AuthCookieFactory {

    private final AuthProperties props;

    public AuthCookieFactory(AuthProperties props) {
        this.props = props;
    }

    public ResponseCookie authCookie(String token) {
        return ResponseCookie.from(props.cookie().name(), token)
                .httpOnly(true)
                .secure(props.cookie().secure())
                .path("/")
                .maxAge(props.jwt().expiry())
                .sameSite(props.cookie().sameSite())
                .build();
    }

    public ResponseCookie logoutCookie() {
        return ResponseCookie.from(props.cookie().name(), "")
                .httpOnly(true)
                .secure(props.cookie().secure())
                .path("/")
                .maxAge(0)
                .sameSite(props.cookie().sameSite())
                .build();
    }
}
