package com.example.demo.shared.context.dto.analysis;

import java.util.List;

/**
 * AI output: Analysis of REST API specification changes and their impact on tests.
 */
public record RestSpecUpdateAnalysis(
    /**
     * List of breaking changes detected in the OpenAPI spec.
     */
    List<BreakingChange> breakingChanges,

    /**
     * List of new endpoints added.
     * Example: ["POST /api/products", "GET /api/products/{id}"]
     */
    List<String> newEndpoints,

    /**
     * List of endpoints removed.
     * Example: ["DELETE /api/legacy/users"]
     */
    List<String> removedEndpoints,

    /**
     * Tests affected by the spec changes with details on impact and suggested actions.
     */
    List<AffectedTest> affectedTests,

    /**
     * Overall recommendations and summary (max 2000 chars).
     * Example: "3 breaking changes detected affecting 8 tests. Recommend regenerating
     * all affected tests using spec2suite. 2 tests should be deleted for removed endpoints."
     */
    String recommendations
) {
    public RestSpecUpdateAnalysis {
        if (breakingChanges == null) {
            breakingChanges = List.of();
        }
        if (newEndpoints == null) {
            newEndpoints = List.of();
        }
        if (removedEndpoints == null) {
            removedEndpoints = List.of();
        }
        if (affectedTests == null) {
            affectedTests = List.of();
        }
        if (recommendations == null || recommendations.isBlank()) {
            throw new IllegalArgumentException("Recommendations required");
        }
    }
}
