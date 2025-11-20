package com.example.demo.orchestrator.dto;

/**
 * Context for TestFailureAnalyzer AI service (SOAP).
 * Contains SOAP test execution details and failure information for analysis.
 */
public record SoapFailureAnalysisContext(
    String testName,
    String url,
    String soapEnvelope,        // SOAP request envelope
    String soapAction,          // SOAPAction header
    int expectedStatus,
    int actualStatus,
    String actualBody,          // SOAP response/fault
    String failedAssertion,     // Which assertion failed
    boolean wasPassingBefore    // Test history
) implements Context {
}
