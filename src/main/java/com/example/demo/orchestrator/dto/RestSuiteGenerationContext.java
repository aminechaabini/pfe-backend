package com.example.demo.orchestrator.dto;

import com.example.demo.llm_adapter.dto.plan.RestTestGenerationPlan;

/**
 * Context for SpecToSuiteGenerator AI service (REST).
 * Contains OpenAPI spec and approved test plan to generate REST test suite.
 */
public record RestSuiteGenerationContext(
    String specContent,                      // OpenAPI content
    RestTestGenerationPlan testPlan          // Approved plan from TestGenerationPlanner
) implements Context {
}
