package com.example.demo.common.context.dto.spec2suite.test;

import com.example.demo.common.context.dto.assertion.AssertionData;
import com.example.demo.common.context.dto.request.SoapRequestData;

import java.util.List;

/**
 * AI output: Instructions for creating a SoapApiTest.
 * Maps to domain SoapApiTest object.
 */
public record CreateSoapApiTestRequest(
    /**
     * Test name (max 40 chars).
     * Should be descriptive and reference the SOAP operation.
     * Example: "GetOrder - Success", "CreateUser - Missing Field"
     */
    String name,

    /**
     * Optional description (max 2000 chars).
     * Example: "Verify GetOrder operation returns order details for valid ID"
     */
    String description,

    /**
     * SOAP request configuration.
     * Must not be null.
     */
    SoapRequestData request,

    /**
     * List of assertions to validate the response.
     * Must have at least one assertion (typically status code).
     *
     * Note: Only XML-compatible assertions allowed for SOAP:
     * - STATUS_EQUALS, XPATH_EQUALS, XPATH_EXISTS, XSD_VALID
     * - HEADER_EQUALS, BODY_CONTAINS, RESPONSE_TIME_LESS_THAN
     * JSON assertions (JSONPATH_*) are NOT allowed for SOAP tests.
     */
    List<AssertionData> assertions
) {
    public CreateSoapApiTestRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Test name required");
        }
        if (request == null) {
            throw new IllegalArgumentException("SOAP request required");
        }
        if (assertions == null || assertions.isEmpty()) {
            throw new IllegalArgumentException("At least one assertion required");
        }
    }
}
