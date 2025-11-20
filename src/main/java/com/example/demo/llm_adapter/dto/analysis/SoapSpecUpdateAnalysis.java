package com.example.demo.llm_adapter.dto.analysis;

import java.util.List;

/**
 * AI output: Analysis of SOAP API specification changes and their impact on tests.
 */
public record SoapSpecUpdateAnalysis(
    /**
     * List of breaking changes detected in the WSDL spec.
     */
    List<BreakingChange> breakingChanges,

    /**
     * List of new operations added.
     * Example: ["ProcessPayment", "RefundTransaction"]
     */
    List<String> newEndpoints,

    /**
     * List of operations removed.
     * Example: ["LegacyPaymentMethod"]
     */
    List<String> removedEndpoints,

    /**
     * Tests affected by the spec changes with details on impact and suggested actions.
     */
    List<AffectedTest> affectedTests,

    /**
     * Overall recommendations and summary (max 2000 chars).
     * Example: "WSDL namespace changed affecting all tests. Schema changes in 4 operations
     * require test regeneration. Recommend regenerating entire suite."
     */
    String recommendations
) {
    public SoapSpecUpdateAnalysis {
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
