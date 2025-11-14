package com.example.demo.orchestrator.api.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Response DTO for TestSuite.
 */
public record TestSuiteResponse(
        Long id,
        String name,
        String description,
        Map<String, String> variables,
        int testCount,
        Instant createdAt,
        Instant updatedAt
) {}
