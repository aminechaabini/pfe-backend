package com.example.demo.shared.valueobject;

/**
 * Result of an assertion validation.
 */
public record AssertionResult(
    AssertionSpec spec,
    boolean ok,
    String message
) {
}
