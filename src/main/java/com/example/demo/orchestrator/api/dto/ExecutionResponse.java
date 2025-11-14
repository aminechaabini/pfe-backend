package com.example.demo.orchestrator.api.dto;

/**
 * Response DTO for execution requests.
 */
public record ExecutionResponse(
        String runId,
        String message
) {}
