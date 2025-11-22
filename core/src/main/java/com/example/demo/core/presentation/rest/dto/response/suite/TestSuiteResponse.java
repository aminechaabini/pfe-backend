package com.example.demo.core.presentation.rest.dto.response.suite;

import java.time.Instant;
import java.util.Map;

/**
 * API response DTO for TestSuite (summary).
 */
public record TestSuiteResponse(
        Long id,
        String name,
        String description,
        Map<String, String> variables,
        Integer testCaseCount,
        Long endpointId,
        Instant createdAt,
        Instant updatedAt
) {
}
