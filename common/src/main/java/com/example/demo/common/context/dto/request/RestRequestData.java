package com.example.demo.common.context.dto.request;

import java.util.List;

/**
 * AI output: Simple data representation of a REST request.
 * Maps to domain RestRequest object.
 *
 * Note: All fields are required (use empty lists instead of null).
 * This ensures proper JSON schema generation for LangChain4j.
 */
public record RestRequestData(
    /**
     * HTTP method (required).
     * Valid values: "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "TRACE"
     */
    String method,

    /**
     * Request URL/endpoint path (required).
     * Example: "/pets", "/pets/123"
     */
    String url,

    /**
     * HTTP headers (required, use empty list if none).
     * Example: [{"key": "Content-Type", "value": "application/json"}]
     */
    List<Header> headers,

    /**
     * Query parameters (required, use empty list if none).
     * Example: [{"key": "page", "value": "1"}]
     */
    List<QueryParam> queryParams,

    /**
     * Request body content (required, use empty string "" if no body).
     * For JSON: {"name": "John"}
     * For no body: ""
     */
    String body,

    /**
     * Type of request body (required).
     * Valid values: "JSON", "XML", "TEXT", "FORM", "BINARY", "NONE"
     * Use "NONE" when body is empty.
     */
    String bodyType,

    /**
     * Authentication configuration (required, use empty string for type if no auth).
     * Set type to "" or "NONE" if no authentication needed.
     */
    AuthData auth
) {
    public RestRequestData {
        // Validate required fields
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("HTTP method required");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL required");
        }
        if (headers == null) {
            throw new IllegalArgumentException("Headers cannot be null (use empty list)");
        }
        if (queryParams == null) {
            throw new IllegalArgumentException("QueryParams cannot be null (use empty list)");
        }
        if (body == null) {
            throw new IllegalArgumentException("Body cannot be null (use empty string)");
        }
        if (bodyType == null || bodyType.isBlank()) {
            throw new IllegalArgumentException("BodyType required (use 'NONE' if no body)");
        }
        // Auth can be null
    }

    /**
     * Helper: Create a simple GET request without body.
     */
    public static RestRequestData get(String url) {
        return new RestRequestData("GET", url, List.of(), List.of(), "", "NONE", null);
    }

    /**
     * Helper: Create a POST request with JSON body.
     */
    public static RestRequestData postJson(String url, String jsonBody) {
        return new RestRequestData(
            "POST",
            url,
            List.of(new Header("Content-Type", "application/json")),
            List.of(),
            jsonBody,
            "JSON",
            null
        );
    }
}

/**
 * Authentication data for REST requests.
 */
record AuthData(
    /**
     * Auth type: "BASIC", "BEARER", "NONE", or "" (for no auth)
     */
    String type,

    /**
     * For BASIC: username
     * For BEARER: not used (null or empty)
     * For NONE/"": not used (null or empty)
     */
    String username,

    /**
     * For BASIC: password
     * For BEARER: token value
     * For NONE/"": not used (null or empty)
     */
    String credential
) {
    public AuthData {
        if (type == null) {
            throw new IllegalArgumentException("Auth type cannot be null");
        }
        // Allow empty string "" or "NONE" for no authentication
        if (!type.isEmpty() && !"NONE".equals(type) && !"BASIC".equals(type) && !"BEARER".equals(type)) {
            throw new IllegalArgumentException("Auth type must be BASIC, BEARER, NONE, or empty string");
        }
    }
}
