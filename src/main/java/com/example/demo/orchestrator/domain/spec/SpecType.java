package com.example.demo.orchestrator.domain.spec;

/**
 * Supported API specification types.
 */
public enum SpecType {
    OPENAPI_3_0("OpenAPI 3.0", "yaml", "json"),
    OPENAPI_3_1("OpenAPI 3.1", "yaml", "json"),
    SWAGGER_2_0("Swagger 2.0", "yaml", "json"),
    WSDL_1_1("WSDL 1.1", "wsdl", "xml"),
    WSDL_2_0("WSDL 2.0", "wsdl", "xml");

    private final String displayName;
    private final String[] fileExtensions;

    SpecType(String displayName, String... fileExtensions) {
        this.displayName = displayName;
        this.fileExtensions = fileExtensions;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String[] getFileExtensions() {
        return fileExtensions;
    }

    /**
     * Check if this spec type is REST-based (OpenAPI/Swagger).
     */
    public boolean isRest() {
        return this == OPENAPI_3_0 || this == OPENAPI_3_1 || this == SWAGGER_2_0;
    }

    /**
     * Check if this spec type is SOAP-based (WSDL).
     */
    public boolean isSoap() {
        return this == WSDL_1_1 || this == WSDL_2_0;
    }

    /**
     * Get spec type from file name.
     */
    public static SpecType fromFileName(String fileName) {
        String lowerName = fileName.toLowerCase();

        if (lowerName.endsWith(".wsdl")) {
            return WSDL_1_1; // Default to 1.1
        }

        // Default to OpenAPI 3.0 for yaml/json
        return OPENAPI_3_0;
    }
}
