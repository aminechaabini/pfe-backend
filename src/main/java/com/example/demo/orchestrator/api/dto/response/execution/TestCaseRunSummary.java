package com.example.demo.orchestrator.api.dto.response.execution;

import com.example.demo.orchestrator.domain.run.RunStatus;

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
