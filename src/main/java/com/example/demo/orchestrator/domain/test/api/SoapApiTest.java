package com.example.demo.orchestrator.domain.test.api;

import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;
import com.example.demo.orchestrator.domain.test.request.body.Body;

/**
 * Concrete implementation of ApiTest for SOAP API testing.
 * Ensures that only SoapRequest instances are used.
 */
public class SoapApiTest extends ApiTest {

    public SoapApiTest(String name, String description) {
        super(name, description);
    }

    /**
     * Override to enforce that only SoapRequest instances are allowed.
     * This provides type safety at the domain level.
     */
    @Override
    public void setRequest(HttpRequest<Body> request) {
        if (request != null && !(request instanceof SoapRequest)) {
            throw new IllegalArgumentException(
                String.format("SoapApiTest requires a SoapRequest, but got: %s",
                    request.getClass().getSimpleName())
            );
        }
        super.setRequest(request);
    }

    /**
     * Type-safe getter that returns a SoapRequest.
     */
    public SoapRequest getSoapRequest() {
        return (SoapRequest) getRequest();
    }
}
