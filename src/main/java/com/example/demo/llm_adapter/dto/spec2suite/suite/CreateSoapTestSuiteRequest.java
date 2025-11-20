package com.example.demo.llm_adapter.dto.spec2suite.suite;

import com.example.demo.llm_adapter.dto.spec2suite.test.CreateSoapApiTestRequest;

import java.util.List;
import java.util.Map;

/**
 * AI output: Instructions for creating a SOAP-only TestSuite.
 *
 * Note: Suite is pure SOAP - no mixing with REST tests.
 */
public record CreateSoapTestSuiteRequest(
    /**
     * Test suite name (max 40 chars).
     * Example: "Payment Service SOAP API Tests"
     */
    String name,

    /**
     * Optional description (max 2000 chars).
     */
    String description,

    /**
     * Suite-level variables (e.g., baseUrl, apiKey).
     * Available to all tests in the suite.
     */
    Map<String, String> variables,

    /**
     * List of SOAP API tests to create.
     * Must have at least one test.
     */
    List<CreateSoapApiTestRequest> soapApiTests
) {
    public CreateSoapTestSuiteRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Suite name required");
        }
        if (soapApiTests == null || soapApiTests.isEmpty()) {
            throw new IllegalArgumentException("At least one SOAP test required");
        }
        if (variables == null) {
            variables = Map.of();
        }
    }
}
