package com.example.demo.llm_adapter.dto.analysis;

/**
 * AI output: Description of a breaking change in API specification.
 */
public record BreakingChange(
    /**
     * Endpoint or operation affected.
     * Example: "POST /api/users"
     */
    String endpoint,

    /**
     * Type of breaking change.
     * Examples: "removed_field", "changed_type", "removed_endpoint", "changed_response_structure"
     */
    String changeType,

    /**
     * Description of the breaking change (max 500 chars).
     * Example: "Field 'email' removed from request body schema"
     */
    String description,

    /**
     * Which existing tests will be affected (max 500 chars).
     * Example: "Tests: 'Create User - Valid Data', 'Create User - Missing Email' will fail"
     */
    String impactedTests
) {
    public BreakingChange {
        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException("Endpoint required");
        }
        if (changeType == null || changeType.isBlank()) {
            throw new IllegalArgumentException("Change type required");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description required");
        }
        if (impactedTests == null || impactedTests.isBlank()) {
            throw new IllegalArgumentException("Impacted tests required");
        }
    }
}
