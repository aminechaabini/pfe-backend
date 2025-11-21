package com.example.demo.shared.context;

/**
 * Context for TestFailureAnalyzer AI service (REST).
 * Contains REST test execution details and failure information for analysis.
 */
public record RestFailureAnalysisContext(
    String testName,
    String method,              // HTTP method
    String url,
    String requestBody,         // JSON/XML/text body
    int expectedStatus,
    int actualStatus,
    String actualBody,
    String failedAssertion,     // Which assertion failed
    boolean wasPassingBefore    // Test history
) implements Context {
}
