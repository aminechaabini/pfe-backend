package com.example.demo.core.application.parser.dto;

/**
 * Parsed REST API endpoint from OpenAPI/Swagger specification.
 */
public record ParsedRestEndpoint(
    String httpMethod,
    String path,
    String name,
    String summary,
    String operationId,
    String specDetails
) implements ParsedEndpoint {

    public ParsedRestEndpoint {
        if (httpMethod == null || httpMethod.isBlank()) {
            throw new IllegalArgumentException("HTTP method cannot be null or blank");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("Path cannot be null or blank");
        }
        if (name == null) {
            name = operationId != null ? operationId : httpMethod + " " + path;
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String summary() {
        return summary;
    }

    @Override
    public String operationId() {
        return operationId;
    }

    @Override
    public String specDetails() {
        return specDetails;
    }
}
