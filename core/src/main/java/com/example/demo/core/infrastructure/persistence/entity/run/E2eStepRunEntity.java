package com.example.demo.core.infrastructure.persistence.entity.run;

import com.example.demo.core.domain.run.RunResult;
import com.example.demo.core.domain.run.RunStatus;
import com.example.demo.core.infrastructure.persistence.common.BaseEntity;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for individual E2E step execution results.
 * Records what happened when a specific step was executed.
 *
 * Design Decisions:
 * - Similar to ApiTestRunEntity but for individual steps
 * - Stores extracted values for use in subsequent steps
 * - Assertion results in separate table
 */
@Entity
@Table(name = "e2e_step_runs")
public class E2eStepRunEntity extends BaseEntity {

    /**
     * Foreign key to parent E2E test run.
     * Managed by @JoinColumn in E2eTestRunEntity.
     */
    @Column(name = "e2e_test_run_id", insertable = false, updatable = false)
    private Long e2eTestRunId;

    /**
     * Reference to the E2E step that was executed
     */
    @Column(name = "e2e_step_id", nullable = false)
    private Long e2eStepId;

    /**
     * Name of the step (denormalized for reporting)
     */
    @Column(name = "step_name", length = 100)
    private String stepName;

    /**
     * Order index within the E2E test run.
     * Managed by @OrderColumn in E2eTestRunEntity.
     */
    @Column(name = "step_order", insertable = false, updatable = false)
    private Integer orderIndex;

    /**
     * Execution status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "status")
    private RunStatus status;

    /**
     * Execution result (SUCCESS, FAILURE, CANCELLED)
     */
    @Enumerated(EnumType.STRING)
    @Column(length = 20, name = "result")
    private RunResult result;

    /**
     * When the step execution started
     */
    @Column(name = "started_at")
    private Instant startedAt;

    /**
     * When the step execution completed
     */
    @Column(name = "completed_at")
    private Instant completedAt;

    /**
     * Actual HTTP status code received
     */
    @Column(name = "actual_status_code")
    private Integer actualStatusCode;

    /**
     * Actual response body received
     */
    @Column(columnDefinition = "TEXT", name = "actual_response_body")
    private String actualResponseBody;

    /**
     * Actual response headers received, stored as JSON
     */
    @Column(columnDefinition = "TEXT", name = "actual_response_headers")
    private String actualResponseHeadersJson;

    /**
     * Response time in milliseconds
     */
    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    /**
     * Values extracted from the response, stored as JSON.
     * Format: Map<String, String> (variable name -> extracted value)
     * These are used in subsequent steps.
     */
    @Column(columnDefinition = "TEXT", name = "extracted_values")
    private String extractedValuesJson;

    /**
     * Error message if the step failed
     */
    @Column(columnDefinition = "TEXT", name = "error_message")
    private String errorMessage;

    /**
     * Individual assertion results for this step.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "e2e_step_run_id")
    private List<AssertionResultEntity> assertionResults = new ArrayList<>();

    // Constructors

    public E2eStepRunEntity() {
    }

    public E2eStepRunEntity(Long e2eStepId, String stepName, RunStatus status) {
        this.e2eStepId = e2eStepId;
        this.stepName = stepName;
        this.status = status;
    }

    // Getters and Setters

    public Long getE2eTestRunId() {
        return e2eTestRunId;
    }

    public Long getE2eStepId() {
        return e2eStepId;
    }

    public void setE2eStepId(Long e2eStepId) {
        this.e2eStepId = e2eStepId;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public Integer getOrderIndex() {
        return orderIndex;
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

    public Integer getActualStatusCode() {
        return actualStatusCode;
    }

    public void setActualStatusCode(Integer actualStatusCode) {
        this.actualStatusCode = actualStatusCode;
    }

    public String getActualResponseBody() {
        return actualResponseBody;
    }

    public void setActualResponseBody(String actualResponseBody) {
        this.actualResponseBody = actualResponseBody;
    }

    public String getActualResponseHeadersJson() {
        return actualResponseHeadersJson;
    }

    public void setActualResponseHeadersJson(String actualResponseHeadersJson) {
        this.actualResponseHeadersJson = actualResponseHeadersJson;
    }

    public Long getResponseTimeMs() {
        return responseTimeMs;
    }

    public void setResponseTimeMs(Long responseTimeMs) {
        this.responseTimeMs = responseTimeMs;
    }

    public String getExtractedValuesJson() {
        return extractedValuesJson;
    }

    public void setExtractedValuesJson(String extractedValuesJson) {
        this.extractedValuesJson = extractedValuesJson;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<AssertionResultEntity> getAssertionResults() {
        return assertionResults;
    }

    public void setAssertionResults(List<AssertionResultEntity> assertionResults) {
        this.assertionResults = assertionResults != null ? assertionResults : new ArrayList<>();
    }

    // Helper methods

    public void addAssertionResult(AssertionResultEntity assertionResult) {
        this.assertionResults.add(assertionResult);
    }

    /**
     * Returns true if all assertions passed.
     */
    public boolean allAssertionsPassed() {
        return assertionResults.stream().allMatch(AssertionResultEntity::isPassed);
    }

    /**
     * Calculates the duration of the step execution in milliseconds.
     */
    public Long getDurationMillis() {
        if (startedAt != null && completedAt != null) {
            return completedAt.toEpochMilli() - startedAt.toEpochMilli();
        }
        return null;
    }

    @Override
    public String toString() {
        return "E2eStepRunEntity{" +
                "id=" + getId() +
                ", e2eStepId=" + e2eStepId +
                ", stepName='" + stepName + '\'' +
                ", orderIndex=" + orderIndex +
                ", status=" + status +
                ", result=" + result +
                ", actualStatusCode=" + actualStatusCode +
                ", responseTimeMs=" + responseTimeMs +
                ", assertionResultsCount=" + assertionResults.size() +
                '}';
    }
}
