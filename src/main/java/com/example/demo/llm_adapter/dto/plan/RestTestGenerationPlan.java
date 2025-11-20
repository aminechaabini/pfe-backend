package com.example.demo.llm_adapter.dto.plan;

import java.util.List;

/**
 * AI output: Blueprint for REST test suite generation from OpenAPI specification.
 * Shown to users BEFORE actual test generation for review and approval.
 * Passed to generation service which builds context for SpecToSuiteGenerator.
 */
public record RestTestGenerationPlan(
    /**
     * Name of the REST test suite that will be created (max 100 chars).
     * Example: "User Management API Test Suite"
     */
    String suiteName,

    /**
     * Description of what this suite will test (max 500 chars).
     * Example: "Comprehensive tests for user CRUD operations, authentication, and profile management"
     */
    String suiteDescription,

    /**
     * Total number of REST API tests that will be generated.
     * Example: 24
     */
    int totalTestCount,

    /**
     * List of individual REST tests that will be created.
     * Each entry describes one test to be generated.
     */
    List<PlannedTest> plannedTests
) {
    public RestTestGenerationPlan {
        if (suiteName == null || suiteName.isBlank()) {
            throw new IllegalArgumentException("Suite name required");
        }
        if (suiteDescription == null || suiteDescription.isBlank()) {
            throw new IllegalArgumentException("Suite description required");
        }
        if (totalTestCount < 0) {
            throw new IllegalArgumentException("Total test count cannot be negative");
        }
        if (plannedTests == null || plannedTests.isEmpty()) {
            throw new IllegalArgumentException("At least one planned test required");
        }
        if (totalTestCount != plannedTests.size()) {
            throw new IllegalArgumentException("Total test count must match planned tests size");
        }
    }
}
