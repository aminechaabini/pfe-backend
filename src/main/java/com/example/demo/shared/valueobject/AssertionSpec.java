package com.example.demo.shared.valueobject;

/**
 * Specification for an assertion to validate HTTP responses.
 * Defines what to check and what value is expected.
 */
public record AssertionSpec(
    String type,                        // "statusEquals", "jsonPathExists", "jsonPathEquals", "xpathEquals", etc.
    String expr,                        // Expression to evaluate (JSONPath, XPath, header name, etc.)
    String expected                     // Expected value (nullable for *Exists assertions)
) {}
