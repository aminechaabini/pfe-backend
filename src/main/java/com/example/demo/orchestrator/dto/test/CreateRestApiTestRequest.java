package com.example.demo.orchestrator.dto.test;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateRestApiTestRequest(
    @NotNull Long suiteId,
    @NotBlank @Size(max = 255) String name,
    @Size(max = 1000) String description,
    @NotBlank @Size(max = 10) String httpMethod,   // GET, POST, ...
    @NotBlank @Size(max = 2000) String url,
    String headersJson,
    String queryJson,
    String body,
    String assertionsJson
) {}
