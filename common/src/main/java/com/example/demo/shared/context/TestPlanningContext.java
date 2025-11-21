package com.example.demo.shared.context;

/**
 * Context for TestGenerationPlanner AI service.
 * Contains API spec and generation options to create a test plan.
 */
public record TestPlanningContext(
    String specType,           // "REST" or "SOAP"
    String specContent,        // OpenAPI/WSDL content
    boolean includeHappyPath,
    boolean includeValidation,
    boolean includeAuth,
    boolean includeErrors,
    boolean includeEdgeCases
) implements Context {
}
