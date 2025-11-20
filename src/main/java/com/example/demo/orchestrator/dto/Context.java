package com.example.demo.orchestrator.dto;

/**
 * Base marker interface for all AI service contexts.
 * Each AI service receives a specific context type with the data it needs.
 */
public sealed interface Context permits
    TestPlanningContext,
    RestSuiteGenerationContext,
    SoapSuiteGenerationContext,
    RestFailureAnalysisContext,
    SoapFailureAnalysisContext,
    E2eFailureAnalysisContext,
    E2eGenerationContext,
    RestSpecUpdateAnalysisContext,
    SoapSpecUpdateAnalysisContext {
}
