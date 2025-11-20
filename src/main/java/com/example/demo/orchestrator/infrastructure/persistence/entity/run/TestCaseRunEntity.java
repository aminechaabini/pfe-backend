package com.example.demo.orchestrator.infrastructure.persistence.entity.run;

import com.example.demo.orchestrator.domain.run.RunResult;
import com.example.demo.orchestrator.domain.run.RunStatus;
import com.example.demo.orchestrator.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Abstract base entity for test case execution runs.
 * Uses SINGLE_TABLE inheritance strategy for consistency with TestCaseEntity.
 *
 * Subclasses:
 * - ApiTestRunEntity (for REST/SOAP API test executions)
 * - E2eTestRunEntity (for E2E test executions)
 *
 * Design Decisions:
 * - SINGLE_TABLE inheritance for performance
 * - Mirrors TestCaseEntity hierarchy for consistency
 * - Stores actual execution data (responses, timings, etc.)
 */
@Entity
@Table(name = "test_case_runs")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
    name = "run_type",
    discriminatorType = DiscriminatorType.STRING,
    length = 20
)
public abstract class TestCaseRunEntity extends BaseEntity {

    /**
     * Foreign key to parent test suite run.
     * Managed by @JoinColumn in TestSuiteRunEntity.
     */
    @Column(name = "test_suite_run_id", insertable = false, updatable = false)
    private Long testSuiteRunId;

    /**
     * Reference to the test case that was executed.
     * Stored as ID to avoid tight coupling.
     */
    @Column(name = "test_case_id", nullable = false)
    private Long testCaseId;

    /**
     * Name of the test case (denormalized for reporting)
     */
    @Column(name = "test_case_name", length = 40)
    private String testCaseName;

    /**
     * Execution status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "status")
    private RunStatus status;

    /**
     * Execution result (SUCCESS, FAILURE, CANCELLED)
     * Only set when status is COMPLETED
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "result")
    private RunResult result;

    /**
     * When the execution started
     */
    @Column(name = "started_at")
    private Instant startedAt;

    /**
     * When the execution completed
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Error message if the test failed
     */
    @Column(columnDefinition = "TEXT", name = "error_message")
    private String errorMessage;

    // Constructors

    protected TestCaseRunEntity() {
    }

    protected TestCaseRunEntity(Long testCaseId, String testCaseName, RunStatus status) {
        this.testCaseId = testCaseId;
        this.testCaseName = testCaseName;
        this.status = status;
    }

    // Getters and Setters

    public Long getTestSuiteRunId() {
        return testSuiteRunId;
    }

    public Long getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(Long testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public RunStatus getStatus() {
        return status;
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public RunResult getResult() {
        return result;
    }

    public void setResult(RunResult result) {
        this.result = result;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // Helper methods

    /**
     * Calculates the duration of the run in milliseconds.
     * Returns null if not yet completed.
     */
    public Long getDurationMillis() {
        if (startedAt != null && completedAt != null) {
            return completedAt.toEpochMilli() - startedAt.toEpochMilli();
        }
        return null;
    }

    /**
     * Returns the discriminator value for this run type.
     */
    public abstract String getRunType();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "id=" + getId() +
                ", testCaseId=" + testCaseId +
                ", testCaseName='" + testCaseName + '\'' +
                ", status=" + status +
                ", result=" + result +
                '}';
    }
}
