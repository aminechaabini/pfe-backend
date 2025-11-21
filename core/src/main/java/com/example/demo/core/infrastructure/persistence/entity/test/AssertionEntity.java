package com.example.demo.core.infrastructure.persistence.entity.test;

import com.example.demo.core.domain.test.assertion.AssertionType;
import com.example.demo.core.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;

/**
 * Persistence entity for test assertions.
 * Stored in separate table to enable analytics and reporting.
 *
 * Design Decisions:
 * - Separate table (not JSON) for querying and aggregations
 * - Can belong to TestCase (API tests) or E2eStep (E2E tests)
 * - Type stored as enum for type safety
 *
 * Use Cases:
 * - "Which assertion types fail most often?"
 * - "Success rate by assertion type"
 * - Aggregated reporting across test runs
 */
@Entity
@Table(name = "assertions")
public class AssertionEntity extends BaseEntity {

    /**
     * Type of assertion (e.g., STATUS_EQUALS, JSONPATH_EQUALS, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "type")
    private AssertionType type;

    /**
     * Target of the assertion (e.g., "status", "$.user.name", "//user/name")
     */
    @Column(nullable = false, length = 500, name = "target")
    private String target;

    /**
     * Expected value for comparison
     */
    @Column(nullable = false, length = 1000, name = "expected")
    private String expected;

    /**
     * Foreign key to parent TestCase (for API tests).
     * Either testCaseId or e2eStepId will be set, not both.
     */
    @Column(name = "test_case_id", insertable = false, updatable = false)
    private Long testCaseId;

    /**
     * Foreign key to parent E2eStep (for E2E tests).
     * Either testCaseId or e2eStepId will be set, not both.
     */
    @Column(name = "e2e_step_id", insertable = false, updatable = false)
    private Long e2eStepId;

    // Constructors

    public AssertionEntity() {
    }

    public AssertionEntity(AssertionType type, String target, String expected) {
        this.type = type;
        this.target = target;
        this.expected = expected;
    }

    // Getters and Setters

    public AssertionType getType() {
        return type;
    }

    public void setType(AssertionType type) {
        this.type = type;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public Long getE2eStepId() {
        return e2eStepId;
    }

    @Override
    public String toString() {
        return "AssertionEntity{" +
                "id=" + getId() +
                ", type=" + type +
                ", target='" + target + '\'' +
                ", expected='" + expected + '\'' +
                ", testCaseId=" + testCaseId +
                ", e2eStepId=" + e2eStepId +
                '}';
    }
}
