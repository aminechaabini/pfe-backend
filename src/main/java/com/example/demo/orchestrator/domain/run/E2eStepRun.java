package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.e2e.E2eStep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the execution of a single E2E step.
 * Extends Run to properly track execution lifecycle (status, timestamps, etc.)
 */
public class E2eStepRun extends Run {

    private E2eStep e2eStep;
    private final List<AssertionResult> assertionResults = new ArrayList<>();

    public E2eStepRun() {
        super();
    }

    public E2eStep getE2eStep() {
        return e2eStep;
    }

    public void setE2eStep(E2eStep e2eStep) {
        this.e2eStep = Objects.requireNonNull(e2eStep, "E2E step cannot be null");
    }

    /**
     * Add an assertion result to this step run.
     * Can only add results when the run is in progress.
     *
     * @param result the assertion result to add
     * @throws IllegalStateException if run is not in progress
     */
    public void addAssertionResult(AssertionResult result) {
        Objects.requireNonNull(result, "Assertion result cannot be null");
        if (getStatus() != RunStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                String.format("Cannot add assertion results when run status is %s. Expected: %s",
                    getStatus(), RunStatus.IN_PROGRESS)
            );
        }
        this.assertionResults.add(result);
    }

    /**
     * Get all assertion results (unmodifiable view).
     */
    public List<AssertionResult> getAssertionResults() {
        return Collections.unmodifiableList(assertionResults);
    }

    /**
     * Check if all assertions in this step passed.
     */
    public boolean allAssertionsPassed() {
        return !assertionResults.isEmpty() &&
               assertionResults.stream().allMatch(AssertionResult::ok);
    }

    /**
     * Get the number of passed assertions.
     */
    public long getPassedAssertionsCount() {
        return assertionResults.stream().filter(AssertionResult::ok).count();
    }

    /**
     * Get the number of failed assertions.
     */
    public long getFailedAssertionsCount() {
        return assertionResults.stream().filter(result -> !result.ok()).count();
    }
}
