package com.example.demo.core.application.dto.generation;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for analyzing a test failure.
 */
public record AnalyzeFailureRequest(
        @NotNull(message = "Test case run ID is required")
        Long testCaseRunId
) {
}
