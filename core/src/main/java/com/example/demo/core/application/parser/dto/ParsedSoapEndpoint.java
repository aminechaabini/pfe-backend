package com.example.demo.core.application.parser.dto;

/**
 * Parsed SOAP endpoint from WSDL specification.
 */
public record ParsedSoapEndpoint(
    String serviceName,
    String operationName,
    String summary,
    String specDetails
) implements ParsedEndpoint {

    public ParsedSoapEndpoint {
        if (serviceName == null || serviceName.isBlank()) {
            throw new IllegalArgumentException("Service name cannot be null or blank");
        }
        if (operationName == null || operationName.isBlank()) {
            throw new IllegalArgumentException("Operation name cannot be null or blank");
        }
        if (specDetails == null) {
            specDetails = "{}";
        }
    }

    /**
     * Constructor without specDetails (for backward compatibility).
     */
    public ParsedSoapEndpoint(String serviceName, String operationName, String summary) {
        this(serviceName, operationName, summary, "{}");
    }

    @Override
    public String name() {
        return operationName;
    }

    @Override
    public String summary() {
        return summary;
    }

    @Override
    public String operationId() {
        return serviceName + "." + operationName;
    }

    @Override
    public String specDetails() {
        return specDetails;
    }
}
