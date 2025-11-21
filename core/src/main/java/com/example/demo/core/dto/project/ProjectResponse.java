package com.example.demo.core.dto.project;

import java.time.Instant;

public record ProjectResponse(
    Long id,
    String name,
    String description,
    Instant createdAt,
    Instant updatedAt
) {}
