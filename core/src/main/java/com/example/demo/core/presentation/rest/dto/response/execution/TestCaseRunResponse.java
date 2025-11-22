package com.example.demo.core.presentation.rest.dto.response.execution;

import com.example.demo.core.domain.run.RunStatus;

import java.time.Instant;
import java.util.List;

/**
 * API response DTO for TestCaseRun (detailed).
 * Includes full request/response data for failure analysis.
 */
public record TestCaseRunResponse(
        Long id,
        Long testCaseId,
        String testCaseName,
        RunStatus status,
        Instant startTime,
        Instant endTime,
        Long durationMs,
        String actualResponse,
        String expectedResult,
        String errorMessage,
        List<AssertionResultResponse> assertionResults
) {
}
