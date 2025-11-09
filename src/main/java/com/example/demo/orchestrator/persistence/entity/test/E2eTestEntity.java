package com.example.demo.orchestrator.persistence.entity.test;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Persistence entity for End-to-End (E2E) tests.
 * Extends TestCaseEntity for multi-step workflow tests.
 *
 * Design Decisions:
 * - Steps stored in separate table as first-class entities
 * - @OrderColumn maintains step execution order
 * - No direct assertions - assertions belong to individual steps
 */
@Entity
@DiscriminatorValue("E2E")
public class E2eTestEntity extends TestCaseEntity {

    /**
     * Ordered list of steps that make up this E2E test.
     * Order is critical for execution flow.
     * Uses @OrderColumn to maintain sequence.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "e2e_test_id")
    @OrderColumn(name = "step_order")
    private List<E2eStepEntity> steps = new ArrayList<>();

    // Constructors

    public E2eTestEntity() {
        super();
    }

    public E2eTestEntity(String name, String description) {
        super(name, description);
    }

    // Getters and Setters

    public List<E2eStepEntity> getSteps() {
        return steps;
    }

    public void setSteps(List<E2eStepEntity> steps) {
        this.steps = steps != null ? steps : new ArrayList<>();
    }

    // Helper methods

    /**
     * Adds a step to the end of the step list.
     * Step order is maintained by JPA's @OrderColumn.
     */
    public void addStep(E2eStepEntity step) {
        this.steps.add(step);
    }

    /**
     * Adds a step at a specific position.
     */
    public void addStep(int index, E2eStepEntity step) {
        this.steps.add(index, step);
    }

    /**
     * Removes a step from the test.
     * The step will be deleted due to orphan removal.
     */
    public void removeStep(E2eStepEntity step) {
        this.steps.remove(step);
    }

    /**
     * Moves a step from one position to another.
     */
    public void moveStep(int fromIndex, int toIndex) {
        if (fromIndex >= 0 && fromIndex < steps.size() &&
            toIndex >= 0 && toIndex < steps.size()) {
            E2eStepEntity step = steps.remove(fromIndex);
            steps.add(toIndex, step);
        }
    }

    @Override
    public String getTestType() {
        return "E2E";
    }

    @Override
    public String toString() {
        return "E2eTestEntity{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", stepsCount=" + steps.size() +
                '}';
    }
}
