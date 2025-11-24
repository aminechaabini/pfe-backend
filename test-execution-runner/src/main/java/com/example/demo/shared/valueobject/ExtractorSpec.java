package com.example.demo.shared.valueobject;

/**
 * Specification for extracting a variable from a response.
 */
public record ExtractorSpec(
    String name,        // Variable name to store extracted value
    String extractor,   // Extractor type: JSON_PATH, XPATH, REGEX
    String expr,        // JSONPath/XPath/Regex expression
    String source       // Source: BODY, HEADER
) {
    public ExtractorSpec(String name, String extractor, String expr) {
        this(name, extractor, expr, "BODY");
    }
}
