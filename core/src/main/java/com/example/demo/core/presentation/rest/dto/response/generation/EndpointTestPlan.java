package com.example.demo.core.presentation.rest.dto.response.generation;

import java.util.List;

/**
 * Test generation plan for a single endpoint.
 */
public record EndpointTestPlan(
        Long endpointId,
        String endpointName,
        Integer plannedTestCount,
        List<String> testScenarios
) {
}
