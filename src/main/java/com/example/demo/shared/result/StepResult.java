package com.example.demo.shared.result;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.HttpResponseData;

import java.util.List;
import java.util.Map;

/**
 * Result of a single E2E step execution.
 * Contains the step outcome, response, assertions, and extracted variables.
 */
public record StepResult(
    String stepId,                      // Unique identifier for this step
    String stepName,                    // Human-readable step name
    int stepOrder,                      // Execution order
    String status,                      // "PASS", "FAIL", "ERROR"
    HttpResponseData response,          // HTTP response received (nullable if ERROR)
    List<AssertionResult> assertionResults, // Assertion validation results
    Map<String, String> extractedVariables, // Variables extracted from THIS step
    long durationMs,                    // Time taken to execute this step
    String errorMessage                 // Error message (nullable unless ERROR)
) {}
