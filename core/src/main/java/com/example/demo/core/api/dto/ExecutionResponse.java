package com.example.demo.core.api.dto;

/**
 * Response DTO for execution requests.
 */
public record ExecutionResponse(
        String runId,
        String message
) {}
