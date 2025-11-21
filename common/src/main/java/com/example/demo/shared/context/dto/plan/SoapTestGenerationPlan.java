package com.example.demo.shared.context.dto.plan;

import java.util.List;

/**
 * AI output: Blueprint for SOAP test suite generation from WSDL specification.
 * Shown to users BEFORE actual test generation for review and approval.
 * Passed to generation service which builds context for SpecToSuiteGenerator.
 */
public record SoapTestGenerationPlan(
    /**
     * Name of the SOAP test suite that will be created (max 100 chars).
     * Example: "Payment Processing Service Test Suite"
     */
    String suiteName,

    /**
     * Description of what this suite will test (max 500 chars).
     * Example: "Tests for payment processing operations including authorization, capture, and refund"
     */
    String suiteDescription,

    /**
     * Total number of SOAP API tests that will be generated.
     * Example: 18
     */
    int totalTestCount,

    /**
     * List of individual SOAP tests that will be created.
     * Each entry describes one test to be generated.
     */
    List<PlannedTest> plannedTests
) {
    public SoapTestGenerationPlan {
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
