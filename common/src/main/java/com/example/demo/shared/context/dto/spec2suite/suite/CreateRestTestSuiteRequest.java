package com.example.demo.shared.context.dto.spec2suite.suite;

import com.example.demo.shared.context.dto.spec2suite.test.CreateRestApiTestRequest;

import java.util.List;
import java.util.Map;

/**
 * AI output: Instructions for creating a REST-only TestSuite.
 *
 * Note: Suite is pure REST - no mixing with SOAP tests.
 */
public record CreateRestTestSuiteRequest(
    /**
     * Test suite name (max 40 chars).
     * Example: "Order API Tests"
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
     * List of REST API tests to create.
     * Must have at least one test.
     */
    List<CreateRestApiTestRequest> restApiTests
) {
    public CreateRestTestSuiteRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Suite name required");
        }
        if (restApiTests == null || restApiTests.isEmpty()) {
            throw new IllegalArgumentException("At least one REST test required");
        }
        if (variables == null) {
            variables = Map.of();
        }
    }
}