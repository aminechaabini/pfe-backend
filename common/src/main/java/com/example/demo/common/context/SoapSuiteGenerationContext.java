package com.example.demo.common.context;

import com.example.demo.common.context.dto.plan.SoapTestGenerationPlan;

/**
 * Context for SpecToSuiteGenerator AI service (SOAP).
 * Contains WSDL spec and approved test plan to generate SOAP test suite.
 */
public record SoapSuiteGenerationContext(
    String specContent,                      // WSDL content
    SoapTestGenerationPlan approvedPlan      // Approved plan from TestGenerationPlanner
) implements Context {
}
