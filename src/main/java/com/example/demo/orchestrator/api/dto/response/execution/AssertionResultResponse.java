package com.example.demo.orchestrator.api.dto.response.execution;

/**
 * API response DTO for assertion result.
 */
public record AssertionResultResponse(
        String type,
        Boolean passed,
        String expected,
        String actual,
        String message
) {
}
