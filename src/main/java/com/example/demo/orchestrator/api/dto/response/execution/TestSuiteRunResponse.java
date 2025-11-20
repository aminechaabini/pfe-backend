package com.example.demo.orchestrator.api.dto.response.execution;

import com.example.demo.orchestrator.domain.run.RunStatus;

import java.time.Instant;
import java.util.List;

/**
 * API response DTO for TestSuiteRun.
 */
public record TestSuiteRunResponse(
        Long id,
        Long testSuiteId,
        String testSuiteName,
        RunStatus status,
        Instant startTime,
        Instant endTime,
        Long durationMs,
        Integer totalTests,
        Integer passedTests,
        Integer failedTests,
        List<TestCaseRunSummary> testCaseRuns
) {
}
