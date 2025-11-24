package com.example.demo.shared.result;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.HttpResponseData;

import java.util.List;

/**
 * Result of executing an API test (REST or SOAP).
 */
public record ApiRunResult(
    String runId,
    String status,
    long duration,
    HttpResponseData response,
    List<AssertionResult> assertionResults,
    String errorMessage
) implements RunResult {
}
