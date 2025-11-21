package com.example.demo.core.api.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Response DTO for Project.
 */
public record ProjectResponse(
        Long id,
        String name,
        String description,
        Map<String, String> variables,
        Instant createdAt,
        Instant updatedAt
) {}
