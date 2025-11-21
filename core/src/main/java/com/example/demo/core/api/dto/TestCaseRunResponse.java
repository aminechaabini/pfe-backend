package com.example.demo.core.api.dto;

import java.time.Instant;

/**
 * Response DTO for TestCaseRun.
 */
public record TestCaseRunResponse(
        Long id,
        Long testCaseId,
        String testCaseName,
        String status,  // NOT_STARTED, IN_PROGRESS, COMPLETED
        String result,  // SUCCESS, FAILURE (null until completed)
        Instant createdAt,
        Instant startedAt,
        Instant completedAt
) {}
