package com.example.demo.core.infrastructure.persistence.entity.spec;

import com.example.demo.core.domain.spec.EndpointType;
import com.example.demo.core.infrastructure.persistence.common.BaseEntity;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract persistence entity for Endpoint.
 * Represents an API endpoint extracted from a specification.
 *
 * Uses SINGLE_TABLE inheritance strategy with subclasses:
 * - RestEndpointEntity (REST endpoints)
 * - SoapEndpointEntity (SOAP endpoints)
 *
 * Relationships:
 * - Many-to-One with SpecSourceEntity (extracted from spec)
 * - Many-to-One with ProjectEntity (for direct access)
 * - Many-to-Many with TestSuiteEntity (endpoints can be tested by multiple suites)
 *
 * Design Decisions:
 * - SINGLE_TABLE for performance (no joins needed for polymorphic queries)
 * - Both specSource and project references for flexible querying
 * - specDetails stored as JSON for flexibility
 */
@Entity
@Table(name = "endpoints")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "endpoint_type", discriminatorType = DiscriminatorType.STRING, length = 10)
public abstract class EndpointEntity extends BaseEntity {

    @Column(length = 500)
    private String summary;

    @Column(name = "operation_id", length = 200)
    private String operationId;

    @Column(name = "spec_details", columnDefinition = "TEXT")
    private String specDetails;

    /**
     * Many-to-One relationship with spec source.
     * Each endpoint is extracted from one spec source.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spec_source_id", nullable = false)
    private SpecSourceEntity specSource;

    /**
     * Many-to-One relationship with project.
     * Denormalized for direct access without joining through specSource.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectEntity project;

    /**
     * Many-to-Many relationship with test suites (inverse side).
     * Endpoints can be tested by multiple test suites.
     */
    @OneToMany(mappedBy = "endpoint")
    private Set<TestSuiteEntity> testSuites = new HashSet<>();

    // Constructors

    public EndpointEntity() {
    }

    // Getters and Setters

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getSpecDetails() {
        return specDetails;
    }

    public void setSpecDetails(String specDetails) {
        this.specDetails = specDetails;
    }

    public SpecSourceEntity getSpecSource() {
        return specSource;
    }

    public void setSpecSource(SpecSourceEntity specSource) {
        this.specSource = specSource;
    }

    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    public Set<TestSuiteEntity> getTestSuites() {
        return testSuites;
    }

    public void setTestSuites(Set<TestSuiteEntity> testSuites) {
        this.testSuites = testSuites != null ? testSuites : new HashSet<>();
    }

    // Abstract methods

    /**
     * Get the endpoint type (REST or SOAP).
     * Must be implemented by subclasses.
     */
    public abstract EndpointType getType();

    /**
     * Get a unique key for this endpoint (for deduplication).
     * Must be implemented by subclasses.
     */
    public abstract String getUniqueKey();

    @Override
    public String toString() {
        return "EndpointEntity{" +
                "id=" + getId() +
                ", type=" + getType() +
                ", summary='" + summary + '\'' +
                ", operationId='" + operationId + '\'' +
                ", specSourceId=" + (specSource != null ? specSource.getId() : null) +
                ", projectId=" + (project != null ? project.getId() : null) +
                '}';
    }
}
