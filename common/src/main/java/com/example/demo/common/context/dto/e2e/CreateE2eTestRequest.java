package com.example.demo.common.context.dto.e2e;

import java.util.List;

/**
 * AI output: Instructions for creating an E2E workflow test.
 * Maps to domain E2eTest object.
 *
 * E2E tests execute multiple steps sequentially, passing data between steps.
 * Steps can be REST or SOAP calls (mixed in same workflow).
 */
public record CreateE2eTestRequest(
    /**
     * E2E test name (max 40 chars).
     * Should describe the workflow being tested.
     * Examples:
     * - "Order Checkout Flow - Happy Path"
     * - "User Registration and Verification"
     * - "Payment Processing with SOAP Service"
     */
    String name,

    /**
     * Description of the user journey being tested (max 2000 chars).
     * Example: "User creates account, verifies email, logs in, creates order, processes payment"
     */
    String description,

    /**
     * Ordered list of steps in the workflow.
     * Steps execute sequentially (unless parallel execution is configured).
     * Must have at least 2 steps.
     */
    List<E2eStepData> steps,

    /**
     * Scenario type for categorization.
     * Examples: "happy_path", "error_scenario", "edge_case"
     * Optional - can be null.
     */
    String scenarioType
) {
    public CreateE2eTestRequest {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("E2E test name required");
        }
        if (steps == null || steps.size() < 2) {
            throw new IllegalArgumentException("E2E test needs at least 2 steps");
        }
    }
}
