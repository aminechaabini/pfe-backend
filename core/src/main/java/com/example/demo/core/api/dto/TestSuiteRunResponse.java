package com.example.demo.core.api.dto;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for TestSuiteRun.
 */
public record TestSuiteRunResponse(
        Long id,
        Long testSuiteId,
        String testSuiteName,
        String status,
        String result,
        List<TestCaseRunResponse> testCaseRuns,
        long passedCount,
        long failedCount,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {}
