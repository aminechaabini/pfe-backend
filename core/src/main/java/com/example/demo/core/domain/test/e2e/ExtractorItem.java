package com.example.demo.core.domain.test.e2e;

/**
 * Extractor item for E2E tests.
 * Defines how to extract variables from HTTP responses.
 */
public record ExtractorItem(
    String variableName,
    ExtractorType type,
    String expression
) {
    public ExtractorItem {
        if (variableName == null || variableName.isBlank()) {
            throw new IllegalArgumentException("Variable name cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Extractor type cannot be null");
        }
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Extractor expression cannot be null or blank");
        }
    }
}

/**
 * Type of extractor (JSONPath, XPath, or Regex).
 */
enum ExtractorType {
    JSONPATH,
    XPATH,
    REGEX
}
