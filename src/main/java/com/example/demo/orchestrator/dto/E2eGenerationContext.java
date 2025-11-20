package com.example.demo.orchestrator.dto;

/**
 * Context for E2eWorkflowGenerator AI service.
 * Contains workflow description and endpoint information to generate E2E test.
 */
public record E2eGenerationContext(
    String workflowName,
    String workflowDescription,
    String endpointSequence,    // Ordered list of endpoints in the workflow
    String schemas,             // Request/response schemas for the endpoints
    String scenarioType         // "happy_path", "error_scenario", "edge_case"
) implements Context {
}
