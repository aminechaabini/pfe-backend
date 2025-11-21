package com.example.demo.core.api.dto.response.spec;

import com.example.demo.core.domain.spec.SpecType;

import java.time.Instant;

/**
 * API response DTO for SpecSource (summary).
 */
public record SpecSourceResponse(
        Long id,
        String name,
        String fileName,
        SpecType specType,
        String version,
        Integer endpointCount,
        Instant createdAt,
        Instant updatedAt
) {
}
