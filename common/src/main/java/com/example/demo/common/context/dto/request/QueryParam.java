package com.example.demo.common.context.dto.request;

/**
 * A key-value pair representing a query parameter.
 * Used instead of Map<String, String> to ensure proper JSON schema generation.
 */
public record QueryParam(
    String key,
    String value
) {
    public QueryParam {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Query parameter key cannot be null or blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Query parameter value cannot be null");
        }
    }
}
