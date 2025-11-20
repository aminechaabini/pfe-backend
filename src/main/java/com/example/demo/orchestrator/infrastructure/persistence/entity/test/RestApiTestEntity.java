package com.example.demo.orchestrator.infrastructure.persistence.entity.test;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for REST API tests.
 * Extends TestCaseEntity with REST-specific fields.
 *
 * Design Decisions:
 * - Full HTTP request stored as JSON for flexibility
 * - Assertions stored in separate table for analytics
 * - No need to query by URL/method (as per agreed design)
 */
@Entity
@DiscriminatorValue("REST_API")
public class RestApiTestEntity extends TestCaseEntity {

    /**
     * Full REST request serialized as JSON.
     * Includes: method, url, headers, queryParams, body, auth
     */
    @Column(columnDefinition = "TEXT", name = "request_json")
    private String requestJson;

    /**
     * Assertions for this test.
     * Stored in separate table for analytics/reporting.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_case_id")
    private List<AssertionEntity> assertions = new ArrayList<>();

    // Constructors

    public RestApiTestEntity() {
        super();
    }

    public RestApiTestEntity(String name, String description) {
        super(name, description);
    }

    public RestApiTestEntity(String name, String description, String requestJson) {
        super(name, description);
        this.requestJson = requestJson;
    }

    // Getters and Setters

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public List<AssertionEntity> getAssertions() {
        return assertions;
    }

    public void setAssertions(List<AssertionEntity> assertions) {
        this.assertions = assertions != null ? assertions : new ArrayList<>();
    }

    // Helper methods

    public void addAssertion(AssertionEntity assertion) {
        this.assertions.add(assertion);
    }

    public void removeAssertion(AssertionEntity assertion) {
        this.assertions.remove(assertion);
    }

    @Override
    public String getTestType() {
        return "REST_API";
    }

    @Override
    public String toString() {
        return "RestApiTestEntity{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", assertionsCount=" + assertions.size() +
                ", hasRequest=" + (requestJson != null) +
                '}';
    }
}
