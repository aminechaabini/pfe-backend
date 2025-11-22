package com.example.demo.core.presentation.rest.dto;

/**
 * Response DTO for execution requests.
 */
public record ExecutionResponse(
        String runId,
        String message
) {}
