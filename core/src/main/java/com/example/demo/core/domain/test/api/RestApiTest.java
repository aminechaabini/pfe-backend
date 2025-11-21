package com.example.demo.core.domain.test.api;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.request.RestRequest;
import com.example.demo.core.domain.test.request.body.Body;

/**
 * Concrete implementation of ApiTest for REST API testing.
 * Type parameter ensures only RestRequest instances can be used (compile-time safety).
 * Validates that assertions match the request body type.
 */
public class RestApiTest extends ApiTest<RestRequest> {

    public RestApiTest(String name, String description) {
        super(name, description);
    }

    /**
     * Override to enforce that assertions match the response body type.
     * JSON assertions require JSON body, XML assertions require XML body.
     */
    @Override
    public void addAssertion(Assertion assertion) {
        RestRequest request = getRequest();

        // Allow assertions to be added before request is set
        if (request != null) {
            Body body = request.getBody();

            if (!assertion.type().isCompatibleWith(body)) {
                throw new IllegalArgumentException(
                    String.format("Assertion %s is not compatible with body type %s",
                        assertion.type(), body.getClass().getSimpleName())
                );
            }
        }

        super.addAssertion(assertion);
    }
}
