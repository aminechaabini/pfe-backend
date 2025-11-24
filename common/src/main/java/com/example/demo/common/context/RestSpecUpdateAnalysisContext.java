package com.example.demo.common.context;

import com.example.demo.common.context.dto.spec2suite.test.CreateRestApiTestRequest;
import java.util.List;

/**
 * Context for SpecUpdateAnalyzer AI service (REST).
 * Contains old and new OpenAPI specs plus existing tests to analyze impact.
 */
public record RestSpecUpdateAnalysisContext(
    String oldSpecContent,              // Previous OpenAPI spec
    String newSpecContent,              // New OpenAPI spec
    List<CreateRestApiTestRequest> existingTests  // Current tests to analyze
) implements Context {
}
