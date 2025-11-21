package com.example.demo.core.domain.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ApiTestRun extends TestCaseRun {

    private final List<AssertionResult> assertionResults = new ArrayList<>();

    public ApiTestRun() {
        super();
    }

    /**
     * Add an assertion result to this test run.
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
     * Check if all assertions passed.
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
