package com.example.demo.shared.result;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.HttpResponseData;

import java.util.List;

/**
 * Result of an API test execution (REST or SOAP).
 * Contains the HTTP response, assertion results, and execution metadata.
 */
public record ApiRunResult(
    String runId,                       // Unique identifier for this test run
    String status,                      // "PASS", "FAIL", "ERROR"
    long durationMs,                    // Total execution time in milliseconds
    HttpResponseData response,          // HTTP response received (nullable if ERROR)
    List<AssertionResult> assertionResults, // Assertion validation results
    String errorMessage                 // Error message (nullable unless ERROR)
) implements RunResult {}
