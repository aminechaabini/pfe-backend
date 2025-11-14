package com.example.demo.shared.request;

import com.example.demo.shared.valueobject.AssertionSpec;
import com.example.demo.shared.valueobject.HttpRequestData;

import java.util.List;
import java.util.Map;

/**
 * Base interface for API test execution requests.
 * Sealed to ensure only REST and SOAP requests are permitted.
 */
public sealed interface ApiRunRequest extends RunRequest permits RestRunRequest, SoapRunRequest {

    HttpRequestData httpRequest();

    List<AssertionSpec> assertions();

    Map<String, String> variables();
}
