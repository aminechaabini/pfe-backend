package com.example.demo.orchestrator.application.dto.execution;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO for executing a test suite.
 */
public record ExecuteTestSuiteRequest(
        @NotNull(message = "Test suite ID is required")
        Long testSuiteId,

        Map<String, String> environmentVariables
) {
}
