package com.example.demo.orchestrator.infrastructure.persistence.entity.test;

import com.example.demo.orchestrator.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for individual E2E test steps.
 * First-class entity with identity, enabling reusability and tracking.
 *
 * Design Decisions:
 * - Has its own id, name, and description (first-class entity)
 * - HTTP request stored as JSON
 * - Extractors stored as JSON (simple list)
 * - Assertions stored in separate table
 * - Order maintained by E2eTestEntity's @OrderColumn
 */
@Entity
@Table(name = "e2e_steps")
public class E2eStepEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Order index within the parent E2E test.
     * Managed by @OrderColumn in E2eTestEntity.
     */
    @Column(name = "step_order", insertable = false, updatable = false)
    private Integer orderIndex;

    /**
     * Foreign key to parent E2E test.
     * Managed by @JoinColumn in E2eTestEntity.
     */
    @Column(name = "e2e_test_id", insertable = false, updatable = false)
    private Long e2eTestId;

    /**
     * HTTP request for this step, serialized as JSON.
     * Can be RestRequest or SoapRequest.
     */
    @Column(columnDefinition = "TEXT", name = "http_request_json")
    private String httpRequestJson;

    /**
     * Data extractors for this step, serialized as JSON.
     * Used to extract values from response for use in subsequent steps.
     * Format: List of {name, source (BODY/HEADER), extractor (JSONPATH/XPATH/REGEX), expression}
     */
    @Column(columnDefinition = "TEXT", name = "extractors_json")
    private String extractorsJson;

    /**
     * Assertions for this step.
     * Stored in separate table for analytics/reporting.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "e2e_step_id")
    private List<AssertionEntity> assertions = new ArrayList<>();

    // Constructors

    public E2eStepEntity() {
    }

    public E2eStepEntity(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Long getE2eTestId() {
        return e2eTestId;
    }

    public String getHttpRequestJson() {
        return httpRequestJson;
    }

    public void setHttpRequestJson(String httpRequestJson) {
        this.httpRequestJson = httpRequestJson;
    }

    public String getExtractorsJson() {
        return extractorsJson;
    }

    public void setExtractorsJson(String extractorsJson) {
        this.extractorsJson = extractorsJson;
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
    public String toString() {
        return "E2eStepEntity{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", orderIndex=" + orderIndex +
                ", e2eTestId=" + e2eTestId +
                ", assertionsCount=" + assertions.size() +
                '}';
    }
}
