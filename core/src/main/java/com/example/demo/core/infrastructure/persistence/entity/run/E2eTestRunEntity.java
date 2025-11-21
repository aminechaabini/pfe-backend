package com.example.demo.core.infrastructure.persistence.entity.run;

import com.example.demo.core.domain.run.RunStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for E2E test execution runs.
 * Aggregates results from individual step executions.
 *
 * Design Decisions:
 * - Owns E2eStepRun entities (cascade all operations)
 * - Order maintained by @OrderColumn
 * - Overall result determined by step results
 */
@Entity
@DiscriminatorValue("E2E_TEST")
public class E2eTestRunEntity extends TestCaseRunEntity {

    /**
     * Ordered list of step execution results.
     * Order matches the step sequence in the E2E test.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "e2e_test_run_id")
    @OrderColumn(name = "step_order")
    private List<E2eStepRunEntity> stepRuns = new ArrayList<>();

    // Constructors

    public E2eTestRunEntity() {
        super();
    }

    public E2eTestRunEntity(Long testCaseId, String testCaseName, RunStatus status) {
        super(testCaseId, testCaseName, status);
    }

    // Getters and Setters

    public List<E2eStepRunEntity> getStepRuns() {
        return stepRuns;
    }

    public void setStepRuns(List<E2eStepRunEntity> stepRuns) {
        this.stepRuns = stepRuns != null ? stepRuns : new ArrayList<>();
    }

    // Helper methods

    public void addStepRun(E2eStepRunEntity stepRun) {
        this.stepRuns.add(stepRun);
    }

    /**
     * Returns true if all steps passed.
     */
    public boolean allStepsPassed() {
        return stepRuns.stream()
                .allMatch(sr -> sr.getResult() != null &&
                        sr.getResult().name().equals("SUCCESS"));
    }

    /**
     * Returns the count of passed steps.
     */
    public long getPassedStepsCount() {
        return stepRuns.stream()
                .filter(sr -> sr.getResult() != null &&
                        sr.getResult().name().equals("SUCCESS"))
                .count();
    }

    /**
     * Returns the count of failed steps.
     */
    public long getFailedStepsCount() {
        return stepRuns.stream()
                .filter(sr -> sr.getResult() != null &&
                        sr.getResult().name().equals("FAILURE"))
                .count();
    }

    /**
     * Returns the total response time across all steps.
     */
    public long getTotalResponseTimeMs() {
        return stepRuns.stream()
                .filter(sr -> sr.getResponseTimeMs() != null)
                .mapToLong(E2eStepRunEntity::getResponseTimeMs)
                .sum();
    }

    @Override
    public String getRunType() {
        return "E2E_TEST";
    }

    @Override
    public String toString() {
        return "E2eTestRunEntity{" +
                "id=" + getId() +
                ", testCaseId=" + getTestCaseId() +
                ", status=" + getStatus() +
                ", result=" + getResult() +
                ", stepRunsCount=" + stepRuns.size() +
                '}';
    }
}
