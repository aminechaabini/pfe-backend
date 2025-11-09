package com.example.demo.orchestrator.domain.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class E2eTestRun extends TestCaseRun {
    
    private final List<E2eStepRun> e2eStepRuns = new ArrayList<>();

    public E2eTestRun() {
        super();
    }

    /**
     * Add a step run to this E2E test run.
     */
    public void addStepRun(E2eStepRun stepRun) {
        Objects.requireNonNull(stepRun, "E2E step run cannot be null");
        this.e2eStepRuns.add(stepRun);
    }

    /**
     * Get all step runs (unmodifiable view).
     */
    public List<E2eStepRun> getStepRuns() {
        return Collections.unmodifiableList(e2eStepRuns);
    }

    /**
     * Check if all steps passed their assertions.
     */
    public boolean allStepsPassed() {
        return !e2eStepRuns.isEmpty() && 
               e2eStepRuns.stream().allMatch(E2eStepRun::allAssertionsPassed);
    }

    /**
     * Get the number of steps that passed.
     */
    public long getPassedStepsCount() {
        return e2eStepRuns.stream().filter(E2eStepRun::allAssertionsPassed).count();
    }

    /**
     * Get the number of steps that failed.
     */
    public long getFailedStepsCount() {
        return e2eStepRuns.stream().filter(step -> !step.allAssertionsPassed()).count();
    }
}
