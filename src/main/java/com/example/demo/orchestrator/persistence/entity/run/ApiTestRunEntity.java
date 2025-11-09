package com.example.demo.orchestrator.persistence.entity.run;

import com.example.demo.orchestrator.domain.run.RunStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for API test execution runs (REST/SOAP).
 * Records the actual HTTP response and assertion results.
 *
 * Design Decisions:
 * - Stores actual response data for debugging
 * - Assertion results in separate table for analytics
 * - Response stored as JSON for flexibility
 */
@Entity
@DiscriminatorValue("API_TEST")
public class ApiTestRunEntity extends TestCaseRunEntity {

    /**
     * Actual HTTP status code received
     */
    @Column(name = "actual_status_code")
    private Integer actualStatusCode;

    /**
     * Actual response body received, stored as text
     */
    @Column(columnDefinition = "TEXT", name = "actual_response_body")
    private String actualResponseBody;

    /**
     * Actual response headers received, stored as JSON
     * Format: Map<String, List<String>>
     */
    @Column(columnDefinition = "TEXT", name = "actual_response_headers")
    private String actualResponseHeadersJson;

    /**
     * Response time in milliseconds
     */
    @Column(name = "response_time_ms")
    private Long responseTimeMs;

    /**
     * Individual assertion results.
     * Stored in separate table for analytics.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "test_case_run_id")
    private List<AssertionResultEntity> assertionResults = new ArrayList<>();

    // Constructors

    public ApiTestRunEntity() {
        super();
    }

    public ApiTestRunEntity(Long testCaseId, String testCaseName, RunStatus status) {
        super(testCaseId, testCaseName, status);
    }

    // Getters and Setters

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
     * Returns the count of passed assertions.
     */
    public long getPassedAssertionsCount() {
        return assertionResults.stream().filter(AssertionResultEntity::isPassed).count();
    }

    /**
     * Returns the count of failed assertions.
     */
    public long getFailedAssertionsCount() {
        return assertionResults.stream().filter(ar -> !ar.isPassed()).count();
    }

    @Override
    public String getRunType() {
        return "API_TEST";
    }

    @Override
    public String toString() {
        return "ApiTestRunEntity{" +
                "id=" + getId() +
                ", testCaseId=" + getTestCaseId() +
                ", status=" + getStatus() +
                ", result=" + getResult() +
                ", actualStatusCode=" + actualStatusCode +
                ", responseTimeMs=" + responseTimeMs +
                ", assertionResultsCount=" + assertionResults.size() +
                '}';
    }
}
