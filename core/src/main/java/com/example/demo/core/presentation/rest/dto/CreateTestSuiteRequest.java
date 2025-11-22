package com.example.demo.core.presentation.rest.dto;

/**
 * Request DTO for creating a TestSuite.
 */
public record CreateTestSuiteRequest(
        String name,
        String description
) {}
