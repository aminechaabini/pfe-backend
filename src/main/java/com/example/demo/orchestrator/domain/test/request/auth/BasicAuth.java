package com.example.demo.orchestrator.domain.test.request.auth;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Basic HTTP auth ("Authorization: Basic base64(user:pass)").
 */
public final class BasicAuth implements Auth {
    private final String username;
    private final char[] password; // char[] for slightly better security than String

    public BasicAuth(String username, char[] password) {
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.password = password == null ? new char[0] : password.clone();
    }

    @Override
    public void applyTo(Map<String, List<String>> headers) {
        // Build "username:password" bytes and Base64 encode
        String creds = username + ":" + new String(password);
        String encoded = Base64.getEncoder().encodeToString(creds.getBytes(StandardCharsets.UTF_8));
        Auth.putHeaderSingle(headers, "Authorization", "Basic " + encoded);
    }

    /** Call when done to clear sensitive password char[] from memory (optional). */
    public void clearPassword() {
        Arrays.fill(password, '\u0000');
    }
}
