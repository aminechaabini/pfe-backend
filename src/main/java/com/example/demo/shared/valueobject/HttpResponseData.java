package com.example.demo.shared.valueobject;

import java.util.Map;

/**
 * Immutable HTTP response data from test execution.
 * Contains the complete response information for validation and reporting.
 */
public record HttpResponseData(
    int statusCode,                     // HTTP status code (200, 404, 500, etc.)
    Map<String, String> headers,        // Response headers
    String body,                        // Response body as string (JSON, XML, HTML, etc.)
    long responseTimeMs                 // Time taken to receive response in milliseconds
) {}
