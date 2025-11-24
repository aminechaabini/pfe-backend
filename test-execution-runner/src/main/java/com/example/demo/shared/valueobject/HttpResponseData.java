package com.example.demo.shared.valueobject;

import java.util.Map;

/**
 * HTTP response data from test execution.
 */
public record HttpResponseData(
    int statusCode,
    Map<String, String> headers,
    String body,
    long responseTime
) {
}
