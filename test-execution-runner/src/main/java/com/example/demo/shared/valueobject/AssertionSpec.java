package com.example.demo.shared.valueobject;

/**
 * Specification for an assertion to validate in a test.
 */
public record AssertionSpec(
    String type,      // statusEquals, jsonPathEquals, xpathEquals, etc.
    String expr,      // JSONPath/XPath expression or empty for status
    String expected   // Expected value to compare against
) {
    public AssertionSpec(String type, String expected) {
        this(type, "", expected);
    }
}
