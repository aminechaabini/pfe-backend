package com.example.demo.core.presentation.rest.dto.response.suite;

import java.time.Instant;

/**
 * API response DTO for TestCase (summary).
 */
public record TestCaseResponse(
        Long id,
        String name,
        String description,
        String type,  // "REST_API", "SOAP_API", "E2E"
        Instant createdAt,
        Instant updatedAt
) {
}
