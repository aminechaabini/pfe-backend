package com.example.demo.core.infrastructure.persistence.entity.run;

import com.example.demo.core.domain.run.RunResult;
import com.example.demo.core.domain.run.RunStatus;
import com.example.demo.core.infrastructure.persistence.common.BaseEntity;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for test suite execution runs.
 * Records the execution history and results of test suite runs.
 *
 * Design Decisions:
 * - Separate lifecycle from test definitions (history persists)
 * - Reference to TestSuite without cascade delete
 * - Owns TestCaseRun entities (cascade all operations)
 *
 * Use Cases:
 * - Track execution history over time
 * - Analyze success/failure trends
 * - Generate reports and metrics
 */
@Entity
@Table(name = "test_suite_runs", indexes = {
    @Index(name = "idx_test_suite_created", columnList = "test_suite_id, created_at")
})
public class TestSuiteRunEntity extends BaseEntity {

    /**
     * Reference to the test suite that was executed.
     * No cascade delete - runs persist even if suite is deleted.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id", nullable = false)
    private TestSuiteEntity testSuite;

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
     * Error message if the run failed
     */
    @Column(columnDefinition = "TEXT", name = "error_message")
    private String errorMessage;

    /**
     * Individual test case run results.
     * Cascade all operations - test case runs belong to suite run.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_suite_run_id")
    private List<TestCaseRunEntity> testCaseRuns = new ArrayList<>();

    // Constructors

    public TestSuiteRunEntity() {
    }

    public TestSuiteRunEntity(TestSuiteEntity testSuite, RunStatus status) {
        this.testSuite = testSuite;
        this.status = status;
    }

    // Getters and Setters

    public TestSuiteEntity getTestSuite() {
        return testSuite;
    }

    public void setTestSuite(TestSuiteEntity testSuite) {
        this.testSuite = testSuite;
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

    public List<TestCaseRunEntity> getTestCaseRuns() {
        return testCaseRuns;
    }

    public void setTestCaseRuns(List<TestCaseRunEntity> testCaseRuns) {
        this.testCaseRuns = testCaseRuns != null ? testCaseRuns : new ArrayList<>();
    }

    // Helper methods

    public void addTestCaseRun(TestCaseRunEntity testCaseRun) {
        this.testCaseRuns.add(testCaseRun);
    }

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

    @Override
    public String toString() {
        return "TestSuiteRunEntity{" +
                "id=" + getId() +
                ", testSuiteId=" + (testSuite != null ? testSuite.getId() : null) +
                ", status=" + status +
                ", result=" + result +
                ", testCaseRunsCount=" + testCaseRuns.size() +
                ", startedAt=" + startedAt +
                ", completedAt=" + completedAt +
                '}';
    }
}
