package com.example.demo.orchestrator.persistence.entity.test;

import com.example.demo.orchestrator.persistence.common.BaseEntity;
import jakarta.persistence.*;

/**
 * Abstract base entity for all test case types.
 * Uses SINGLE_TABLE inheritance strategy for better query performance.
 *
 * Subclasses:
 * - RestApiTestEntity (REST API tests)
 * - SoapApiTestEntity (SOAP API tests)
 * - E2eTestEntity (End-to-End multi-step tests)
 *
 * Design Decisions:
 * - SINGLE_TABLE inheritance for best query performance
 * - Discriminator column 'test_type' to identify subclass
 * - All test types share common fields (name, description)
 * - Type-specific fields are nullable (acceptable tradeoff)
 */
@Entity
@Table(name = "test_cases")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "test_type",
    discriminatorType = DiscriminatorType.STRING,
    length = 20
)
public abstract class TestCaseEntity extends BaseEntity {

    @Column(nullable = false, length = 40)
    private String name;

    @Column(length = 2000)
    private String description;

    /**
     * Foreign key to test suite.
     * Managed by @JoinColumn in TestSuiteEntity.
     */
    @Column(name = "test_suite_id", insertable = false, updatable = false)
    private Long testSuiteId;

    // Constructors

    protected TestCaseEntity() {
    }

    protected TestCaseEntity(String name, String description) {
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

    public Long getTestSuiteId() {
        return testSuiteId;
    }

    /**
     * Returns the discriminator value for this test type.
     * Used for identifying the test type in queries.
     */
    public abstract String getTestType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", testSuiteId=" + testSuiteId +
                '}';
    }
}
