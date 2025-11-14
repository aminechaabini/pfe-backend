package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;
import java.util.Map;

/**
 * Request to execute a REST API test.
 * Contains HTTP request, assertions, and variables for parameterization.
 */
public record RestRunRequest(
    String runId,                       // Unique identifier for this test run
    HttpRequestData httpRequest,        // The HTTP request to execute
    List<AssertionSpec> assertions,     // Assertions to validate the response
    Map<String, String> variables       // Variables for substitution (project + suite level)
) implements ApiRunRequest {}
