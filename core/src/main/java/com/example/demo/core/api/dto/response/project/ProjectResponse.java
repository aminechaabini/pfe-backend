package com.example.demo.core.api.dto.response.project;

import java.time.Instant;
import java.util.Map;

/**
 * API response DTO for Project.
 */
public record ProjectResponse(
        Long id,
        String name,
        String description,
        Map<String, String> variables,
        Integer testSuiteCount,
        Integer specSourceCount,
        Instant createdAt,
        Instant updatedAt
) {
}
