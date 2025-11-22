package com.example.demo.core.presentation.rest.dto.response.execution;

import com.example.demo.core.domain.run.RunStatus;

/**
 * Summary of a test case run (used in TestSuiteRunResponse).
 */
public record TestCaseRunSummary(
        Long id,
        String testCaseName,
        RunStatus status,
        Long durationMs
) {
}
