package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.*;

/**
 * Base HTTP request with method, URL, headers, and body.
 * @param <B> the type of body
 */
public class HttpRequest<B extends Body> {

    private HttpMethod method;
    private String url;
    private Map<String, List<String>> headers;
    private B body;

    protected HttpRequest() {
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
}
