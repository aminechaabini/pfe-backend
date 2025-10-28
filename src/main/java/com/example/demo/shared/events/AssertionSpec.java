package com.example.demo.shared.events;

public record AssertionSpec(
        String type,      // "statusEquals" | "jsonPathExists" | "jsonPathEquals" | "xpathEquals"
        String expr,      // e.g. "$.id" or "//Customer/Name"
        String expected   // optional, for equals
) {
}
