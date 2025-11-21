package com.example.demo.core.dto.test;

import java.time.Instant;

public record RestApiTestResponse(
    Long id,
    Long suiteId,
    String name,
    String description,
    String httpMethod,
    String url,
    String headersJson,
    String queryJson,
    String body,
    String assertionsJson,
    Instant createdAt,
    Instant updatedAt
) {}
