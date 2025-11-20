package com.example.demo.orchestrator.application.dto.generation;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for generating a test suite from a spec source.
 */
public record GenerateTestSuiteRequest(
        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotNull(message = "Spec source ID is required")
        Long specSourceId,

        GenerationOptions options
) {
}
