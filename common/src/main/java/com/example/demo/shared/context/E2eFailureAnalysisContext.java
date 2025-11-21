package com.example.demo.shared.context;

/**
 * Context for TestFailureAnalyzer AI service (E2E).
 * Contains E2E test execution details and failure information for analysis.
 */
public record E2eFailureAnalysisContext(
    String testName,
    int failedStepIndex,        // Which step failed (0-based)
    String failedStepName,
    String stepType,            // "REST" or "SOAP"
    String method,              // HTTP method (for REST) or null
    String url,
    String request,             // Request body/envelope
    int expectedStatus,
    int actualStatus,
    String actualBody,
    String failedAssertion,     // Which assertion failed
    String extractedVariables,  // Variables extracted from previous steps
    boolean wasPassingBefore    // Test history
) implements Context {
}
