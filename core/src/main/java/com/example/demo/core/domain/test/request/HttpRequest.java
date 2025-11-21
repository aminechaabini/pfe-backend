package com.example.demo.core.domain.test.request;

import com.example.demo.core.domain.test.request.body.Body;

import java.util.*;

/**
 * Base HTTP request with method, URL, query parameters, headers, and body.
 * @param <B> the type of body
 */
public abstract class HttpRequest<B extends Body> {

    private HttpMethod method;
    private String url;
    private Map<String, List<String>> queryParams;
    private Map<String, List<String>> headers;
    private B body;

    protected HttpRequest() {
        this.queryParams = new HashMap<>();
        this.headers = new HashMap<>();
    }

    protected HttpRequest(HttpMethod method, String url, B body) {
        this();
        this.method = Objects.requireNonNull(method, "HTTP method cannot be null");
        this.url = Objects.requireNonNull(url, "URL cannot be null");
        this.body = body;
    }

    // Getters
    public HttpMethod getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, List<String>> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }

    public B getBody() {
        return body;
    }

    // Setters with validation
    public void setMethod(HttpMethod method) {
        this.method = Objects.requireNonNull(method, "HTTP method cannot be null");
    }

    public void setUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }
        this.url = url.trim();
    }

    public void setBody(B body) {
        this.body = body;
    }

    /**
     * Add a header with a single value.
     */
    public void addHeader(String name, String value) {
        Objects.requireNonNull(name, "Header name cannot be null");
        Objects.requireNonNull(value, "Header value cannot be null");
        this.headers.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    /**
     * Set a header, replacing any existing values.
     */
    public void setHeader(String name, String value) {
        Objects.requireNonNull(name, "Header name cannot be null");
        Objects.requireNonNull(value, "Header value cannot be null");
        List<String> values = new ArrayList<>();
        values.add(value);
        this.headers.put(name, values);
    }

    /**
     * Set multiple values for a header.
     */
    public void setHeader(String name, List<String> values) {
        Objects.requireNonNull(name, "Header name cannot be null");
        Objects.requireNonNull(values, "Header values cannot be null");
        this.headers.put(name, new ArrayList<>(values));
    }

    /**
     * Remove a header.
     */
    public void removeHeader(String name) {
        this.headers.remove(name);
    }

    /**
     * Clear all headers.
     */
    public void clearHeaders() {
        this.headers.clear();
    }

    /**
     * Add a query parameter with a single value.
     * Supports multiple values for the same parameter name.
     */
    public void addQueryParam(String name, String value) {
        Objects.requireNonNull(name, "Query param name cannot be null");
        Objects.requireNonNull(value, "Query param value cannot be null");
        this.queryParams.computeIfAbsent(name, k -> new ArrayList<>()).add(value);
    }

    /**
     * Set a query parameter, replacing any existing values.
     */
    public void setQueryParam(String name, String value) {
        Objects.requireNonNull(name, "Query param name cannot be null");
        Objects.requireNonNull(value, "Query param value cannot be null");
        List<String> values = new ArrayList<>();
        values.add(value);
        this.queryParams.put(name, values);
    }

    /**
     * Set multiple values for a query parameter.
     */
    public void setQueryParam(String name, List<String> values) {
        Objects.requireNonNull(name, "Query param name cannot be null");
        Objects.requireNonNull(values, "Query param values cannot be null");
        this.queryParams.put(name, new ArrayList<>(values));
    }

    /**
     * Remove a query parameter.
     */
    public void removeQueryParam(String name) {
        this.queryParams.remove(name);
    }

    /**
     * Clear all query parameters.
     */
    public void clearQueryParams() {
        this.queryParams.clear();
    }

    /**
     * Validates the HTTP request according to HTTP specifications.
     * Base validation checks that required fields are set.
     * @throws IllegalStateException if validation fails
     */
    public void validate() {
        if (url == null || url.isBlank()) {
            throw new IllegalStateException("URL is required");
        }
        if (method == null) {
            throw new IllegalStateException("HTTP method is required");
        }
    }

    /**
     * Helper method to check if a header exists (case-insensitive).
     */
    protected boolean hasHeader(String headerName) {
        return headers.keySet().stream()
                .anyMatch(key -> key.equalsIgnoreCase(headerName));
    }
}
