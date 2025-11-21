package com.example.demo.core.api.dto;

/**
 * Request DTO for creating a TestSuite.
 */
public record CreateTestSuiteRequest(
        String name,
        String description
) {}
