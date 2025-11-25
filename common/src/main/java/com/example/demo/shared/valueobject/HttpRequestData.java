package com.example.demo.shared.valueobject;

import java.util.Map;

/**
 * HTTP request data for test execution.
 */
public record HttpRequestData(
    String method,
    String url,
    Map<String, String> headers,
    byte[] body
) {
    public HttpRequestData(String method, String url, Map<String, String> headers, String bodyText) {
        this(method, url, headers, bodyText != null ? bodyText.getBytes() : new byte[0]);
    }

    public HttpRequestData(String method, String url) {
        this(method, url, Map.of(), new byte[0]);
    }
}
