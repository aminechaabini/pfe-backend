package com.example.demo.core.dto.ai;

import com.example.demo.common.context.dto.analysis.FailureAnalysis;

/**
 * Result wrapper for test failure analysis from AI.
 */
public record FailureAnalysisResult(
    FailureAnalysis analysis
) {
    public FailureAnalysisResult {
        if (analysis == null) {
            throw new IllegalArgumentException("Failure analysis cannot be null");
        }
    }
}
