package com.example.demo.shared.context.dto.e2e;

import com.example.demo.shared.context.dto.assertion.AssertionData;
import com.example.demo.shared.context.dto.request.RestRequestData;
import com.example.demo.shared.context.dto.request.SoapRequestData;

import java.util.List;

/**
 * AI output: Single step in an E2E workflow test.
 * Maps to domain E2eStep object.
 *
 * Uses type discriminator pattern to handle mixed REST/SOAP workflows
 * (LangChain4j doesn't support polymorphism).
 *
 * Each step executes a request, validates response with assertions,
 * and optionally extracts data for use in subsequent steps.
 */
public record E2eStepData(
    /**
     * Step name (max 40 chars).
     * Examples: "Create User", "Get Auth Token", "Submit Order"
     */
    String name,

    /**
     * Step description explaining what this step does (max 500 chars).
     * Example: "Creates a new user account and extracts the user ID"
     * Optional - can be null.
     */
    String description,

    /**
     * Step execution order (0-based index).
     * Steps execute sequentially in ascending order.
     */
    int orderIndex,

    /**
     * Step type discriminator.
     * Valid values: "REST" or "SOAP"
     * Determines which request field should be populated.
     */
    String stepType,

    /**
     * REST request configuration.
     * Must be non-null if stepType = "REST", must be null if stepType = "SOAP".
     */
    RestRequestData restRequest,

    /**
     * SOAP request configuration.
     * Must be non-null if stepType = "SOAP", must be null if stepType = "REST".
     */
    SoapRequestData soapRequest,

    /**
     * Response assertions to validate step execution.
     * For REST steps: can use STATUS_EQUALS, JSONPATH_*, HEADER_* assertions
     * For SOAP steps: can use STATUS_EQUALS, XPATH_*, HEADER_* assertions
     * Must have at least 1 assertion.
     */
    List<AssertionData> assertions,

    /**
     * Data extractors to capture values from the response.
     * Extracted values stored as variables (${variableName}) for use in later steps.
     * Optional - can be empty list.
     * Examples:
     * - Extract user ID: JSON_PATH "$.data.userId" → ${userId}
     * - Extract auth token: HEADER "Authorization" → ${authToken}
     */
    List<ExtractorData> extractors
) {
    public E2eStepData {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Step name required");
        }
        if (stepType == null || stepType.isBlank()) {
            throw new IllegalArgumentException("Step type required (REST or SOAP)");
        }
        if (orderIndex < 0) {
            throw new IllegalArgumentException("Order index must be >= 0");
        }

        // Validate request field matches stepType
        if ("REST".equalsIgnoreCase(stepType)) {
            if (restRequest == null) {
                throw new IllegalArgumentException("REST request required when stepType is REST");
            }
            if (soapRequest != null) {
                throw new IllegalArgumentException("SOAP request must be null when stepType is REST");
            }
        } else if ("SOAP".equalsIgnoreCase(stepType)) {
            if (soapRequest == null) {
                throw new IllegalArgumentException("SOAP request required when stepType is SOAP");
            }
            if (restRequest != null) {
                throw new IllegalArgumentException("REST request must be null when stepType is SOAP");
            }
        } else {
            throw new IllegalArgumentException("Step type must be REST or SOAP, got: " + stepType);
        }

        if (assertions == null || assertions.isEmpty()) {
            throw new IllegalArgumentException("At least one assertion required");
        }

        // Ensure extractors is not null
        if (extractors == null) {
            extractors = List.of();
        }
    }
}
