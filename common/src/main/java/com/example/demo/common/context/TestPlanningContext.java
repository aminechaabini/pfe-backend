package com.example.demo.common.context;

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
    public TestPlanningContext(String specType, String specContent, boolean includeHappyPath, boolean includeValidation, boolean includeAuth, boolean includeErrors, boolean includeEdgeCases) {
        this.specType = specType;
        this.specContent = specContent;
        this.includeHappyPath = includeHappyPath;
        this.includeValidation = includeValidation;
        this.includeAuth = includeAuth;
        this.includeErrors = includeErrors;
        this.includeEdgeCases = includeEdgeCases;
    }

    @Override
    public String specType() {
        return specType;
    }

    @Override
    public String specContent() {
        return specContent;
    }

    @Override
    public boolean includeHappyPath() {
        return includeHappyPath;
    }

    @Override
    public boolean includeValidation() {
        return includeValidation;
    }

    @Override
    public boolean includeAuth() {
        return includeAuth;
    }

    @Override
    public boolean includeErrors() {
        return includeErrors;
    }

    @Override
    public boolean includeEdgeCases() {
        return includeEdgeCases;
    }
}
