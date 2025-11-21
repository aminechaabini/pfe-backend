package com.example.demo.core.domain.test.e2e;

import com.example.demo.core.domain.test.TestCase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * End-to-end test composed of multiple sequential steps.
 */
public class E2eTest extends TestCase {
    
    private final List<E2eStep> steps = new ArrayList<>();

    public E2eTest(String name, String description) {
        super(name, description);
    }

    /**
     * Get all steps (unmodifiable view).
     */
    public List<E2eStep> getSteps() {
        return Collections.unmodifiableList(steps);
    }

    /**
     * Add a step to the end of the test.
     */
    public void addStep(E2eStep step) {
        Objects.requireNonNull(step, "E2E step cannot be null");
        this.steps.add(step);
        touch();
    }

    /**
     * Insert a step at a specific index.
     */
    public void insertStep(int index, E2eStep step) {
        Objects.requireNonNull(step, "E2E step cannot be null");
        if (index < 0 || index > steps.size()) {
            throw new IndexOutOfBoundsException("Invalid step index: " + index);
        }
        this.steps.add(index, step);
        touch();
    }

    /**
     * Remove a step.
     */
    public boolean removeStep(E2eStep step) {
        boolean result = this.steps.remove(step);
        if (result) touch();
        return result;
    }

    /**
     * Remove a step at a specific index.
     */
    public E2eStep removeStep(int index) {
        E2eStep removed = this.steps.remove(index);
        touch();
        return removed;
    }

    /**
     * Clear all steps.
     */
    public void clearSteps() {
        if (!this.steps.isEmpty()) {
            this.steps.clear();
            touch();
        }
    }

    /**
     * Get the number of steps.
     */
    public int getStepCount() {
        return steps.size();
    }

    /**
     * Get a step by index.
     */
    public E2eStep getStep(int index) {
        return steps.get(index);
    }
}
