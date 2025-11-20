package com.example.demo.orchestrator.api.dto.response.generation;

import java.util.List;

/**
 * API response DTO for test generation preview/plan.
 */
public record TestGenerationPlanResponse(
        Integer estimatedTestCount,
        List<EndpointTestPlan> endpointPlans
) {
}
