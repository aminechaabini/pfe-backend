package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;
import java.util.Map;

/**
 * Request to execute a SOAP API test.
 * Contains HTTP request with SOAP envelope, SOAPAction header, assertions, and variables.
 */
public record SoapRunRequest(
    String runId,                       // Unique identifier for this test run
    HttpRequestData httpRequest,        // The HTTP request with SOAP envelope
    String soapAction,                  // SOAPAction header (required for SOAP 1.1)
    List<AssertionSpec> assertions,     // Assertions to validate the response
    Map<String, String> variables       // Variables for substitution (project + suite level)
) implements ApiRunRequest {}
