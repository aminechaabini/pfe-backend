package com.example.demo.core.domain.test.api;

import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.request.HttpRequest;
import com.example.demo.core.domain.test.request.body.Body;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Base class for API tests (REST, SOAP, etc.)
 * Generic over the request type to provide compile-time type safety.
 *
 * @param <R> The specific type of HttpRequest (e.g., RestRequest, SoapRequest)
 */
public abstract class ApiTest<R extends HttpRequest<? extends Body>> extends TestCase {

    private R request;
    private final List<Assertion> assertions = new ArrayList<>();

    protected ApiTest(String name, String description) {
        super(name, description);
    }

    /**
     * Protected constructor for reconstitution by subclasses.
     * Bypasses validation.
     */
    protected ApiTest(Long id, String name, String description, Instant createdAt, Instant updatedAt) {
        super(id, name, description, createdAt, updatedAt);
    }

    // Getters
    public R getRequest() {
        return request;
    }

    public List<Assertion> getAssertions() {
        return Collections.unmodifiableList(assertions);
    }

    // Setters
    public void setRequest(R request) {
        this.request = Objects.requireNonNull(request, "Request cannot be null");
        touch();
    }

    /**
     * Add an assertion to this test.
     */
    public void addAssertion(Assertion assertion) {
        Objects.requireNonNull(assertion, "Assertion cannot be null");
        this.assertions.add(assertion);
        touch();
    }

    /**
     * Remove an assertion from this test.
     */
    public boolean removeAssertion(Assertion assertion) {
        boolean result = this.assertions.remove(assertion);
        if (result) touch();
        return result;
    }

    /**
     * Clear all assertions.
     */
    public void clearAssertions() {
        if (!this.assertions.isEmpty()) {
            this.assertions.clear();
            touch();
        }
    }

    /**
     * Get the number of assertions.
     */
    public int getAssertionCount() {
        return assertions.size();
    }

    /**
     * Internal method for setting request during reconstitution (bypasses validation and touch).
     */
    protected void setRequestInternal(R request) {
        this.request = request;
    }

    /**
     * Internal method for adding assertions during reconstitution (bypasses validation and touch).
     */
    protected void addAssertionsInternal(List<Assertion> assertions) {
        if (assertions != null) {
            this.assertions.addAll(assertions);
        }
    }
}
