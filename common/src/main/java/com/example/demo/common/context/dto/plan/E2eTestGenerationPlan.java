package com.example.demo.common.context.dto.plan;

import java.util.List;

/**
 * AI output: Blueprint for E2E workflow test generation.
 * Shown to users BEFORE actual test generation for review and approval.
 * Passed to generation service which builds context for E2eWorkflowGenerator.
 */
public record E2eTestGenerationPlan(
    /**
     * Name of the E2E test that will be created (max 100 chars).
     * Example: "Order Checkout Flow - Happy Path"
     */
    String testName,

    /**
     * Description of what this E2E test will validate (max 500 chars).
     * Example: "Tests complete order checkout workflow from cart creation to payment confirmation"
     */
    String testDescription,

    /**
     * Total number of steps in the E2E workflow.
     * Example: 5
     */
    int totalStepCount,

    /**
     * List of individual steps that will be created.
     * Each entry describes one step in the workflow.
     */
    List<PlannedStep> plannedSteps
) {
    public E2eTestGenerationPlan {
        if (testName == null || testName.isBlank()) {
            throw new IllegalArgumentException("Test name required");
        }
        if (testDescription == null || testDescription.isBlank()) {
            throw new IllegalArgumentException("Test description required");
        }
        if (totalStepCount < 2) {
            throw new IllegalArgumentException("E2E test needs at least 2 steps");
        }
        if (plannedSteps == null || plannedSteps.isEmpty()) {
            throw new IllegalArgumentException("At least one planned step required");
        }
        if (totalStepCount != plannedSteps.size()) {
            throw new IllegalArgumentException("Total step count must match planned steps size");
        }
    }
}
