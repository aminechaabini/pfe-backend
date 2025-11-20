package com.example.demo.orchestrator.dto;

import com.example.demo.llm_adapter.dto.plan.SoapTestGenerationPlan;

/**
 * Context for SpecToSuiteGenerator AI service (SOAP).
 * Contains WSDL spec and approved test plan to generate SOAP test suite.
 */
public record SoapSuiteGenerationContext(
    String specContent,                      // WSDL content
    SoapTestGenerationPlan testPlan          // Approved plan from TestGenerationPlanner
) implements Context {
}
