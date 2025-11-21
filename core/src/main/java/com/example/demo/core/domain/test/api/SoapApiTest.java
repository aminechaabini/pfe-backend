package com.example.demo.core.domain.test.api;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.request.SoapRequest;

/**
 * Concrete implementation of ApiTest for SOAP API testing.
 * Type parameter ensures only SoapRequest instances can be used (compile-time safety).
 * Validates that only XML-compatible assertions are used (SOAP is always XML).
 */
public class SoapApiTest extends ApiTest<SoapRequest> {

    public SoapApiTest(String name, String description) {
        super(name, description);
    }

    /**
     * Override to enforce that only XML-compatible assertions are allowed.
     * SOAP responses are always XML, so JSON assertions are not permitted.
     */
    @Override
    public void addAssertion(Assertion assertion) {
        if (assertion.type().requiresJson()) {
            throw new IllegalArgumentException(
                String.format("SOAP tests do not support JSON assertions. Cannot use: %s",
                    assertion.type())
            );
        }
        super.addAssertion(assertion);
    }
}
