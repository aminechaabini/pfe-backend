package com.example.demo.core.application.dto.generation;

/**
 * Options for AI test generation.
 */
public record GenerationOptions(
        Integer maxTestsPerEndpoint,
        Boolean includeEdgeCases,
        Boolean includeErrorCases,
        String testingStrategy  // "comprehensive", "smoke", "regression"
) {
    public static GenerationOptions defaults() {
        return new GenerationOptions(5, true, true, "comprehensive");
    }
}
