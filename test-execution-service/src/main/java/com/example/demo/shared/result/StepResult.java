package com.example.demo.shared.result;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.HttpResponseData;

import java.util.List;
import java.util.Map;

/**
 * Result of executing a single E2E step.
 */
public record StepResult(
    String stepId,
    String stepName,
    int stepOrder,
    String status,
    HttpResponseData response,
    List<AssertionResult> assertionResults,
    Map<String, String> extractedVariables,
    long duration,
    String errorMessage
) {
}
