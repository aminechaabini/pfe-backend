package com.example.demo.shared.valueobject;

/**
 * Result of an assertion validation.
 * Contains only the essential information without coupling to AssertionSpec.
 */
public record AssertionResult(
    String type,        // Assertion type (statusEquals, jsonPathEquals, etc.)
    boolean ok,         // Pass/Fail
    String message,     // Human-readable message
    String expected,    // Expected value (optional)
    String actual       // Actual value (optional)
) {
    /**
     * Constructor for simple pass/fail without expected/actual values.
     */
    public AssertionResult(String type, boolean ok, String message) {
        this(type, ok, message, null, null);
    }
}
