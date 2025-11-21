package com.example.demo.shared.context.dto.request;

import java.util.Map;

/**
 * AI output: Simple data representation of a REST request.
 * Maps to domain RestRequest object.
 *
 * Note: Headers and queryParams use simplified Map<String, String> format.
 * Service layer converts to Map<String, List<String>> for domain.
 */
public record RestRequestData(
    /**
     * HTTP method.
     * Valid values: "GET", "POST", "PUT", "PATCH", "DELETE", "HEAD", "OPTIONS", "TRACE"
     *
     * Important constraints (enforced by domain):
     * - GET, HEAD, DELETE must not have body
     * - PATCH must have body
     * - POST usually has body (but not required)
     */
    String method,

    /**
     * Request URL/endpoint path.
     * Can include path variables: "/api/orders/{id}"
     * Can use variables: "/api/orders/${orderId}"
     * Example: "/api/orders", "/api/users/123"
     */
    String url,

    /**
     * HTTP headers as key-value pairs.
     * Example: {"Content-Type": "application/json", "Authorization": "Bearer token"}
     *
     * Note: If body is present, Content-Type is required.
     * Use variables: {"Authorization": "Bearer ${authToken}"}
     */
    Map<String, String> headers,

    /**
     * Query parameters as key-value pairs.
     * Example: {"page": "1", "size": "10"}
     * Will be appended to URL as: /api/orders?page=1&size=10
     */
    Map<String, String> queryParams,

    /**
     * Request body content as string, or null if no body.
     *
     * Format depends on bodyType:
     * - JSON: Valid JSON string: {"name": "John", "age": 30}
     * - XML: Valid XML string: <user><name>John</name></user>
     * - TEXT: Plain text
     * - FORM: URL-encoded: name=John&age=30
     * - BINARY: Base64-encoded binary data
     * - NONE: null (no body)
     */
    String body,

    /**
     * Type of request body.
     * Valid values: "JSON", "XML", "TEXT", "FORM", "BINARY", "NONE"
     *
     * This determines how the body is processed:
     * - JSON: Parsed and sent as application/json
     * - XML: Parsed and sent as application/xml
     * - TEXT: Sent as text/plain
     * - FORM: Sent as application/x-www-form-urlencoded
     * - BINARY: Decoded and sent as application/octet-stream
     * - NONE: No body sent (use for GET, DELETE, etc.)
     */
    String bodyType,

    /**
     * Authentication configuration (optional).
     * If null, no authentication is used.
     */
    AuthData auth
) {
    public RestRequestData {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("HTTP method required");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("URL required");
        }
        if (headers == null) {
            headers = Map.of();
        }
        if (queryParams == null) {
            queryParams = Map.of();
        }
        if (bodyType == null) {
            bodyType = "NONE";
        }
    }

    /**
     * Helper: Create a simple GET request without body.
     */
    public static RestRequestData get(String url) {
        return new RestRequestData("GET", url, Map.of(), Map.of(), null, "NONE", null);
    }

    /**
     * Helper: Create a POST request with JSON body.
     */
    public static RestRequestData postJson(String url, String jsonBody) {
        return new RestRequestData(
            "POST",
            url,
            Map.of("Content-Type", "application/json"),
            Map.of(),
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
     * Auth type: "BASIC" or "BEARER"
     */
    String type,

    /**
     * For BASIC: username
     * For BEARER: not used (null)
     */
    String username,

    /**
     * For BASIC: password
     * For BEARER: token value
     */
    String credential
) {
    public AuthData {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Auth type required");
        }
        if (!"BASIC".equals(type) && !"BEARER".equals(type)) {
            throw new IllegalArgumentException("Auth type must be BASIC or BEARER");
        }
    }
}