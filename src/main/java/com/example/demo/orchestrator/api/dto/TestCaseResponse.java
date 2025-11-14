package com.example.demo.orchestrator.api.dto;

import java.time.Instant;

/**
 * Response DTO for TestCase (polymorphic - REST, SOAP, E2E).
 */
public record TestCaseResponse(
        Long id,
        String name,
        String description,
        String type,  // "REST", "SOAP", "E2E"
        int assertionCount,
        int stepCount,
        Instant createdAt,
        Instant updatedAt
) {}
