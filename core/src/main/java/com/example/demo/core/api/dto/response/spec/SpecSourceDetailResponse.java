package com.example.demo.core.api.dto.response.spec;

import com.example.demo.core.domain.spec.SpecType;

import java.time.Instant;
import java.util.List;

/**
 * API response DTO for SpecSource with endpoints (detail).
 */
public record SpecSourceDetailResponse(
        Long id,
        String name,
        String fileName,
        SpecType specType,
        String version,
        Integer endpointCount,
        List<EndpointResponse> endpoints,
        Instant createdAt,
        Instant updatedAt
) {
}
