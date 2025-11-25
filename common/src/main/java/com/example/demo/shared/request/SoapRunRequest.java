package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;
import java.util.Map;

/**
 * Request to execute a SOAP API test.
 */
public record SoapRunRequest(
    String runId,
    HttpRequestData httpRequest,
    List<AssertionSpec> assertions,
    Map<String, String> variables
) implements ApiRunRequest {
}
