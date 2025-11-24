package com.example.demo.common.context.dto.plan;

/**
 * AI output: Description of a single step in an E2E workflow test.
 * Lightweight preview shown to users before actual E2E test creation.
 */
public record PlannedStep(
    /**
     * Name of the step that will be created (max 100 chars).
     * Examples:
     * - "Create Shopping Cart"
     * - "Add Items to Cart"
     * - "Process Payment"
     */
    String stepName,

    /**
     * Description of what this step will do (max 500 chars).
     * Examples:
     * - "Creates a new shopping cart and extracts cart ID for subsequent steps"
     * - "Adds selected items to the cart and validates total price"
     * - "Processes payment using extracted cart ID and verifies payment confirmation"
     */
    String description
) {
    public PlannedStep {
        if (stepName == null || stepName.isBlank()) {
            throw new IllegalArgumentException("Step name required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Step description required");
        }
    }
}
