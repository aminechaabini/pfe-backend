package com.example.demo.llm_adapter.dto.spec2suite.test;

import com.example.demo.llm_adapter.dto.assertion.AssertionData;
import com.example.demo.llm_adapter.dto.request.RestRequestData;

import java.util.List;

/**
 * AI output: Instructions for creating a RestApiTest.
 * Maps to domain RestApiTest object.
 */
public record CreateRestApiTestRequest(
    /**
     * Test name (max 40 chars).
     * Should be descriptive and include HTTP method.
     * Example: "POST /orders - Success"
     */
    String name,

    /**
     * Optional description (max 2000 chars).
     * Example: "Verify order creation with valid data returns 201"
     */
    String description,

    /**
     * REST request configuration.
     * Must not be null.
     */
    RestRequestData request,

    /**
     * List of assertions to validate the response.
     * Must have at least one assertion (typically status code).
     */
    List<AssertionData> assertions
) {
    public CreateRestApiTestRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Test name required");
        }
        if (request == null) {
            throw new IllegalArgumentException("Request required");
        }
        if (assertions == null || assertions.isEmpty()) {
            throw new IllegalArgumentException("At least one assertion required");
        }
    }
}