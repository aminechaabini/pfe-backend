package com.example.demo.runner.builder;

import com.example.demo.shared.valueobject.HttpRequestData;
import java.net.http.HttpRequest;
import java.util.Map;

/**
 * Builds java.net.http.HttpRequest from shared HttpRequestData,
 * resolving template variables in URL, headers, and body.
 */
public interface HttpRequestBuilder {

    /**
     * Build an HTTP request with variable substitution.
     *
     * @param requestData the request data from shared contract
     * @param variables variables for template substitution (${var} or {var})
     * @return ready-to-execute java.net.http.HttpRequest
     */
    HttpRequest build(HttpRequestData requestData, Map<String, String> variables);
}
