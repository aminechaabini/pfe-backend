package com.example.demo.shared.valueobject;

/**
 * Result of an assertion validation.
 * Contains the original assertion spec and the outcome.
 */
public record AssertionResult(
    AssertionSpec spec,                 // The assertion that was evaluated
    boolean ok,                         // Whether the assertion passed
    String message                      // Human-readable result message
) {}
