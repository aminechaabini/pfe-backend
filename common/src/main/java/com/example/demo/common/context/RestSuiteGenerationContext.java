package com.example.demo.common.context;

import com.example.demo.common.context.dto.plan.RestTestGenerationPlan;

/**
 * Context for SpecToSuiteGenerator AI service (REST).
 * Contains OpenAPI spec and approved test plan to generate REST test suite.
 */
public record RestSuiteGenerationContext(
    String specContent,                      // OpenAPI content
    RestTestGenerationPlan approvedPlan      // Approved plan from TestGenerationPlanner
) implements Context {
}
