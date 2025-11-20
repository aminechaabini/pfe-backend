package com.example.demo.orchestrator.application.dto.execution;

import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * DTO for executing a single test case.
 */
public record ExecuteTestCaseRequest(
        @NotNull(message = "Test case ID is required")
        Long testCaseId,

        Map<String, String> environmentVariables
) {
}
