package com.example.demo.common.context.dto.spec2suite.suite;

/**
 * A key-value pair representing a suite-level variable.
 * Used instead of Map<String, String> to ensure proper JSON schema generation.
 */
public record Variable(
    String key,
    String value
) {
    public Variable {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Variable key cannot be null or blank");
        }
        if (value == null) {
            throw new IllegalArgumentException("Variable value cannot be null");
        }
    }
}
