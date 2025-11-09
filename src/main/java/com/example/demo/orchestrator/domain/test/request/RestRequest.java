package com.example.demo.orchestrator.domain.test.request;

import com.example.demo.orchestrator.domain.test.request.auth.Auth;
import com.example.demo.orchestrator.domain.test.request.body.Body;

import java.util.*;

/**
 * REST API request with query parameters, authentication, and content type.
 */
public class RestRequest extends HttpRequest<Body> {

    private Map<String, String> queryParams;
    private Auth auth;
    private String contentType;

    public RestRequest() {
        super();
        this.queryParams = new HashMap<>();
    }

    public RestRequest(HttpMethod method, String url, Body body) {
        super(method, url, body);
        this.queryParams = new HashMap<>();
    }

    // Getters
    public Map<String, String> getQueryParams() {
        return Collections.unmodifiableMap(queryParams);
    }

    public Auth getAuth() {
        return auth;
    }

    public String getContentType() {
        return contentType;
    }

    // Setters
    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Add a query parameter.
     */
    public void addQueryParam(String name, String value) {
        Objects.requireNonNull(name, "Query param name cannot be null");
        Objects.requireNonNull(value, "Query param value cannot be null");
        this.queryParams.put(name, value);
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

    // NOTE: Authentication application has been removed from the domain model.
    // Authentication should be applied at the infrastructure/execution layer, not here.
    // The domain model should only represent "this request has authentication",
    // not perform the actual authentication header application.
    //
    // Example usage in infrastructure layer:
    //   Map<String, List<String>> headers = new HashMap<>(request.getHeaders());
    //   if (request.getAuth() != null) {
    //       request.getAuth().applyTo(headers);
    //   }
    //   // then use headers in actual HTTP call
}
