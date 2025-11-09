package com.example.demo.orchestrator.persistence.entity.test;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for SOAP API tests.
 * Extends TestCaseEntity with SOAP-specific fields.
 *
 * Design Decisions:
 * - Full SOAP request stored as JSON for flexibility
 * - Assertions stored in separate table for analytics
 * - Similar structure to RestApiTestEntity for consistency
 */
@Entity
@DiscriminatorValue("SOAP_API")
public class SoapApiTestEntity extends TestCaseEntity {

    /**
     * Full SOAP request serialized as JSON.
     * Includes: url, headers, soapAction, soapBody, auth
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

    public SoapApiTestEntity() {
        super();
    }

    public SoapApiTestEntity(String name, String description) {
        super(name, description);
    }

    public SoapApiTestEntity(String name, String description, String requestJson) {
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
        return "SOAP_API";
    }

    @Override
    public String toString() {
        return "SoapApiTestEntity{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", assertionsCount=" + assertions.size() +
                ", hasRequest=" + (requestJson != null) +
                '}';
    }
}
