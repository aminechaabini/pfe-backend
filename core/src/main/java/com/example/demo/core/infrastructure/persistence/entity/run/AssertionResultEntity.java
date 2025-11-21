package com.example.demo.core.infrastructure.persistence.entity.run;

import com.example.demo.core.domain.test.assertion.AssertionType;
import com.example.demo.core.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;

/**
 * Persistence entity for individual assertion evaluation results.
 * Records what happened when a specific assertion was checked.
 *
 * Design Decisions:
 * - Separate table for analytics and reporting
 * - Can belong to ApiTestRun or E2eStepRun
 * - Stores both expected and actual values for debugging
 *
 * Use Cases:
 * - "Which assertion types fail most often?"
 * - "Success rate by assertion type"
 * - Detailed debugging of test failures
 */
@Entity
@Table(name = "assertion_results")
public class AssertionResultEntity extends BaseEntity {

    /**
     * Foreign key to parent API test run.
     * Either testCaseRunId or e2eStepRunId will be set, not both.
     */
    @Column(name = "test_case_run_id", insertable = false, updatable = false)
    private Long testCaseRunId;

    /**
     * Foreign key to parent E2E step run.
     * Either testCaseRunId or e2eStepRunId will be set, not both.
     */
    @Column(name = "e2e_step_run_id", insertable = false, updatable = false)
    private Long e2eStepRunId;

    /**
     * Reference to the original assertion definition
     */
    @Column(name = "assertion_id")
    private Long assertionId;

    /**
     * Type of assertion that was evaluated
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "assertion_type")
    private AssertionType assertionType;

    /**
     * Target of the assertion (e.g., "status", "$.user.name")
     */
    @Column(nullable = false, length = 500, name = "target")
    private String target;

    /**
     * Expected value from the assertion definition
     */
    @Column(nullable = false, length = 1000, name = "expected_value")
    private String expectedValue;

    /**
     * Actual value obtained during execution
     */
    @Column(length = 1000, name = "actual_value")
    private String actualValue;

    /**
     * Whether the assertion passed
     */
    @Column(nullable = false, name = "passed")
    private boolean passed;

    /**
     * Error message if the assertion failed
     */
    @Column(columnDefinition = "TEXT", name = "error_message")
    private String errorMessage;

    // Constructors

    public AssertionResultEntity() {
    }

    public AssertionResultEntity(Long assertionId, AssertionType assertionType,
                                 String target, String expectedValue, boolean passed) {
        this.assertionId = assertionId;
        this.assertionType = assertionType;
        this.target = target;
        this.expectedValue = expectedValue;
        this.passed = passed;
    }

    // Getters and Setters

    public Long getTestCaseRunId() {
        return testCaseRunId;
    }

    public Long getE2eStepRunId() {
        return e2eStepRunId;
    }

    public Long getAssertionId() {
        return assertionId;
    }

    public void setAssertionId(Long assertionId) {
        this.assertionId = assertionId;
    }

    public AssertionType getAssertionType() {
        return assertionType;
    }

    public void setAssertionType(AssertionType assertionType) {
        this.assertionType = assertionType;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getExpectedValue() {
        return expectedValue;
    }

    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }

    public String getActualValue() {
        return actualValue;
    }

    public void setActualValue(String actualValue) {
        this.actualValue = actualValue;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "AssertionResultEntity{" +
                "id=" + getId() +
                ", assertionType=" + assertionType +
                ", target='" + target + '\'' +
                ", expectedValue='" + expectedValue + '\'' +
                ", actualValue='" + actualValue + '\'' +
                ", passed=" + passed +
                ", testCaseRunId=" + testCaseRunId +
                ", e2eStepRunId=" + e2eStepRunId +
                '}';
    }
}
