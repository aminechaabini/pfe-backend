package com.example.demo.core.api.dto.response.generation;

import java.util.List;

/**
 * API response DTO for test failure analysis.
 */
public record FailureAnalysisResponse(
        Long testCaseRunId,
        String failureReason,
        String suggestedFix,
        List<String> possibleCauses,
        String aiAnalysis
) {
}
