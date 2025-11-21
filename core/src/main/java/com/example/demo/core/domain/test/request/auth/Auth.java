package com.example.demo.core.domain.test.request.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Auth {

    /**
     * Apply authentication to the given headers map.
     */
    void applyTo(Map<String, List<String>> headers);

    /**
     * Validates the authentication configuration.
     * @throws IllegalStateException if validation fails
     */
    void validate();

    /**
     * Helper method to put a single header value into the headers map.
     */
    static void putHeaderSingle(Map<String, List<String>> headers, String headerName, String headerValue) {
        List<String> values = new ArrayList<>();
        values.add(headerValue);
        headers.put(headerName, values);
    }
}
