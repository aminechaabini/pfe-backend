package com.example.demo.core.domain.test.request;

import com.example.demo.core.domain.test.request.auth.Auth;
import com.example.demo.core.domain.test.request.body.Body;

/**
 * REST API request with HTTP-level authentication.
 * Query parameters and headers are inherited from HttpRequest.
 */
public class RestRequest extends HttpRequest<Body> {

    private Auth auth;

    public RestRequest() {
        super();
    }

    public RestRequest(HttpMethod method, String url, Body body) {
        super(method, url, body);
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    /**
     * Validates the REST request according to HTTP/REST specifications (RFC 7231).
     * Checks:
     * - Base HTTP validation (URL, method)
     * - GET/HEAD/DELETE MUST NOT have body
     * - PATCH MUST have body
     * - TRACE MUST NOT have body
     * - Content-Type header MUST be present when body exists
     * - Auth validation if present
     * @throws IllegalStateException if validation fails
     */
    @Override
    public void validate() {
        super.validate();

        HttpMethod method = getMethod();
        Body body = getBody();

        // RFC 7231: GET, HEAD, DELETE should not have body (no defined semantics)
        if ((method == HttpMethod.GET || method == HttpMethod.HEAD || method == HttpMethod.DELETE)
            && body != null) {
            throw new IllegalStateException(
                method + " requests MUST NOT have a body (RFC 7231)"
            );
        }

        // RFC 5789: PATCH must have body (contains patch instructions)
        if (method == HttpMethod.PATCH && body == null) {
            throw new IllegalStateException(
                "PATCH requests MUST have a body (RFC 5789)"
            );
        }

        // RFC 7231: TRACE must not have body
        if (method == HttpMethod.TRACE && body != null) {
            throw new IllegalStateException(
                "TRACE requests MUST NOT have a body (RFC 7231)"
            );
        }

        // RFC 7231: Content-Type is required when body is present
        if (body != null && !hasHeader("Content-Type")) {
            throw new IllegalStateException(
                "Content-Type header is required when body is present (RFC 7231)"
            );
        }

        // Validate authentication if present
        if (auth != null) {
            auth.validate();
        }
    }

    // NOTE: Authentication application happens at the infrastructure/execution layer.
    // The domain model only represents "this request has authentication".
    // Example usage in infrastructure layer:
    //   Map<String, List<String>> headers = new HashMap<>(request.getHeaders());
    //   if (request.getAuth() != null) {
    //       request.getAuth().applyTo(headers);
    //   }
}
