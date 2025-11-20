package com.example.demo.orchestrator.application.dto.suite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for creating a new test suite.
 */
public record CreateTestSuiteRequest(
        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotBlank(message = "Test suite name is required")
        @Size(max = 40, message = "Test suite name must be at most 40 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        Long endpointId,  // null for E2E tests

        Map<String, String> initialVariables
) {
}
