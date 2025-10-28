package com.example.demo.orchestrator.dto.project;

import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {}
