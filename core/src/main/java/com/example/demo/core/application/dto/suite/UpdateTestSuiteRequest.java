package com.example.demo.core.application.dto.suite;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing test suite.
 * Null fields are ignored (not updated).
 */
public record UpdateTestSuiteRequest(
        @Size(max = 40, message = "Test suite name must be at most 40 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description
) {
}
