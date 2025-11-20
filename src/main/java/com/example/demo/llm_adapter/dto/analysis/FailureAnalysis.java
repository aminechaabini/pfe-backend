package com.example.demo.llm_adapter.dto.analysis;

/**
 * AI output: Analysis of why a test failed and how to fix it.
 */
public record FailureAnalysis(
    /**
     * Root cause explanation in plain English (max 500 chars).
     * Example: "The API returned 401 because the authentication token has expired"
     */
    String rootCause,

    /**
     * Specific recommendations to fix the issue (max 1000 chars).
     * Example: "Update the test to refresh the authentication token before making the request"
     */
    String recommendation,

    /**
     * Issue category: "test_issue", "api_issue", or "environment_issue"
     */
    String issueType
) {
    public FailureAnalysis {
        if (rootCause == null || rootCause.isBlank()) {
            throw new IllegalArgumentException("Root cause required");
        }
        if (recommendation == null || recommendation.isBlank()) {
            throw new IllegalArgumentException("Recommendation required");
        }
        if (issueType == null || issueType.isBlank()) {
            throw new IllegalArgumentException("Issue type required");
        }
    }
}
