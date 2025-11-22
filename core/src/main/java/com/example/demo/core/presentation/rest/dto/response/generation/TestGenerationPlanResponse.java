package com.example.demo.core.presentation.rest.dto.response.generation;

import java.util.List;

/**
 * API response DTO for test generation preview/plan.
 */
public record TestGenerationPlanResponse(
        Integer estimatedTestCount,
        List<EndpointTestPlan> endpointPlans
) {
}
