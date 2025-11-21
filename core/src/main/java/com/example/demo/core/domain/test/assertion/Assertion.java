package com.example.demo.core.domain.test.assertion;

import java.util.Objects;

/**
 * Represents a test assertion with type, target, and expected value.
 */
public record Assertion(AssertionType type, String target, String expected) {
    
    public Assertion {
        Objects.requireNonNull(type, "Assertion type cannot be null");
        Objects.requireNonNull(target, "Assertion target cannot be null");
        Objects.requireNonNull(expected, "Assertion expected value cannot be null");
    }
}
