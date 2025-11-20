package com.example.demo.llm_adapter.dto.plan;

/**
 * AI output: Description of a single test that will be generated.
 * Lightweight preview shown to users before actual test creation.
 */
public record PlannedTest(
    /**
     * Name of the test that will be created (max 100 chars).
     * Examples:
     * - "Create User - Valid Data"
     * - "Get User By ID - Not Found"
     * - "Update User Profile - Missing Required Field"
     */
    String testName,

    /**
     * Description of what this test will validate (max 500 chars).
     * Examples:
     * - "Validates successful user creation with all required fields"
     * - "Verifies 404 response when user ID doesn't exist"
     * - "Ensures proper validation error when email is missing"
     */
    String description
) {
    public PlannedTest {
        if (testName == null || testName.isBlank()) {
            throw new IllegalArgumentException("Test name required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Test description required");
        }
    }
}
