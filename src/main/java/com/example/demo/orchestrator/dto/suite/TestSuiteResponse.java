package com.example.demo.orchestrator.dto.suite;

import java.time.Instant;

public record TestSuiteResponse(
    Long id,
    Long projectId,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {}