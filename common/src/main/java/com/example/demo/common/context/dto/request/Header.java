package com.example.demo.common.context.dto.request;

/**
 * A key-value pair representing an HTTP header.
 * Used instead of Map<String, String> to ensure proper JSON schema generation.
 */
public record Header(
    String key,
    String value
) {
    public Header {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Header key cannot be null or blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Header value cannot be null");
        }
    }
}
