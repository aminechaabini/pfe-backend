package com.example.demo.core.dto.test;

import jakarta.validation.constraints.Size;

/** ID is in the path; all fields optional (partial update). */
public record UpdateRestApiTestRequest(
    @Size(max = 255) String name,
    @Size(max = 1000) String description,
    @Size(max = 10) String httpMethod,
    @Size(max = 2000) String url,
    String headersJson,
    String queryJson,
    String body,
    String assertionsJson,
    Long version
) {}
