package com.example.demo.orchestrator.dto;

import com.example.demo.llm_adapter.dto.CreateRestApiTestRequest;
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
