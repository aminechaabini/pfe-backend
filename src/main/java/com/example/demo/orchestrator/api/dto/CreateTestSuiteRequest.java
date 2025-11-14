package com.example.demo.orchestrator.api.dto;

/**
 * Request DTO for creating a TestSuite.
 */
public record CreateTestSuiteRequest(
        String name,
        String description
) {}
