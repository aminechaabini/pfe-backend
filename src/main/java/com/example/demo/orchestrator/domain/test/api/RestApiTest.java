package com.example.demo.orchestrator.domain.test.api;

import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.RestRequest;
import com.example.demo.orchestrator.domain.test.request.body.Body;

/**
 * Concrete implementation of ApiTest for REST API testing.
 * Ensures that only RestRequest instances are used.
 */
public class RestApiTest extends ApiTest {

    public RestApiTest(String name, String description) {
        super(name, description);
    }

    /**
     * Override to enforce that only RestRequest instances are allowed.
     * This provides type safety at the domain level.
     */
    @Override
    public void setRequest(HttpRequest<Body> request) {
        if (request != null && !(request instanceof RestRequest)) {
            throw new IllegalArgumentException(
                String.format("RestApiTest requires a RestRequest, but got: %s",
                    request.getClass().getSimpleName())
            );
        }
        super.setRequest(request);
    }

    /**
     * Type-safe getter that returns a RestRequest.
     */
    public RestRequest getRestRequest() {
        return (RestRequest) getRequest();
    }
}
