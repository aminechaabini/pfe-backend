package com.example.demo.orchestrator.domain.test.request.auth;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Bearer token authentication ("Authorization: Bearer <token>").
 * Good for OAuth2 access tokens or static API tokens.
 */
public final class BearerTokenAuth implements Auth {
    private final String token;

    public BearerTokenAuth(String token) {
        this.token = Objects.requireNonNull(token, "Token cannot be null");
    }

    @Override
    public void applyTo(Map<String, List<String>> headers) {
        Auth.putHeaderSingle(headers, "Authorization", "Bearer " + token);
    }
}
