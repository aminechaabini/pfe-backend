package com.example.demo.shared.events;

public record AssertionResult(AssertionSpec spec, boolean ok, String message) {
}
