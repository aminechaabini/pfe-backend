package com.example.demo.shared.valueobject;

import java.util.Map;

/**
 * Immutable HTTP request data for transport across layers.
 * Contains the raw HTTP request information without protocol-specific logic.
 */
public record HttpRequestData(
    String method,                      // "GET", "POST", "PUT", "DELETE", "PATCH"
    String url,                         // Full URL, may contain variables like ${baseUrl}/users
    Map<String, String> headers,        // HTTP headers (single-value)
    byte[] body                         // Request body (nullable for GET requests)
) {}
