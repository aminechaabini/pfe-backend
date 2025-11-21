package com.example.demo.runner.executor;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Executes HTTP requests and returns responses.
 * Abstraction over java.net.http.HttpClient for testability.
 */
public interface HttpRequestExecutor {

    /**
     * Execute an HTTP request.
     *
     * @param request the HTTP request to execute
     * @return HTTP response with string body
     * @throws RuntimeException if execution fails
     */
    HttpResponse<String> execute(HttpRequest request);
}
