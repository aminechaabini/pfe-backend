package com.example.demo.core.dto.ai;

import com.example.demo.common.context.dto.e2e.CreateE2eTestRequest;

/**
 * Result wrapper for generated E2E test from AI.
 */
public record GeneratedE2eTestResult(
    CreateE2eTestRequest e2eTest
) {
    public GeneratedE2eTestResult {
        if (e2eTest == null) {
            throw new IllegalArgumentException("E2E test cannot be null");
        }
    }
}
