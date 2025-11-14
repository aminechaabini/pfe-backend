package com.example.demo.shared.valueobject;

/**
 * Specification for extracting values from HTTP responses.
 * Extracted values are stored as variables for use in subsequent E2E steps.
 */
public record ExtractorSpec(
    String name,                        // Variable name to store extracted value (e.g., "authToken", "userId")
    String source,                      // Source location: "BODY", "HEADER", "COOKIE"
    String extractor,                   // Extraction method: "JSONPATH", "XPATH", "REGEX"
    String expression                   // Extraction expression (e.g., "$.token", "//user/@id", "token=(.*)")
) {}
