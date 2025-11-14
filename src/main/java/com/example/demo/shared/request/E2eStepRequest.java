package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.ExtractorSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;

/**
 * Represents a single step in an E2E test execution.
 * Each step executes one HTTP request, validates with assertions,
 * and can extract values for subsequent steps.
 */
public record E2eStepRequest(
    String stepId,                      // Unique identifier for this step
    String stepName,                    // Human-readable step name (e.g., "Login", "Create Order")
    int stepOrder,                      // Execution order (0-based index)
    HttpRequestData httpRequest,        // The HTTP request to execute
    String protocol,                    // "REST" or "SOAP"
    String soapAction,                  // SOAPAction header (nullable, only for SOAP)
    List<AssertionSpec> assertions,     // Validations to perform on response
    List<ExtractorSpec> extractors      // Values to extract from response for next steps
) {}
