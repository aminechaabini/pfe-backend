package com.example.demo.orchestrator.infrastructure.spec;

import com.example.demo.orchestrator.app.service.spec.*;
import com.example.demo.orchestrator.domain.spec.HttpMethod;
import com.example.demo.orchestrator.domain.spec.SpecType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser for REST-based API specifications.
 * Handles OpenAPI 3.0, OpenAPI 3.1, and Swagger 2.0.
 *
 * Uses the swagger-parser library which auto-detects versions
 * and handles conversion between formats automatically.
 */
@Component
public class RestSpecParser implements SpecParser {

    private final ObjectMapper objectMapper;

    public RestSpecParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ParsedSpec parse(String content, SpecType specType) {
        validateSpecType(specType);

        SwaggerParseResult result = new OpenAPIV3Parser().readContents(content);

        validateParseResult(result);

        OpenAPI openAPI = result.getOpenAPI();
        String version = extractVersion(openAPI);
        List<ParsedEndpoint> endpoints = extractEndpoints(openAPI);

        return new ParsedSpec(version, endpoints);
    }

    private void validateSpecType(SpecType specType) {
        if (!specType.isRest()) {
            throw new IllegalArgumentException(
                "RestSpecParser only handles REST-based specs (OpenAPI/Swagger), got: " + specType
            );
        }
    }

    private void validateParseResult(SwaggerParseResult result) {
        if (result.getMessages() != null && !result.getMessages().isEmpty()) {
            String errors = String.join("; ", result.getMessages());
            throw new SpecParseException("Failed to parse spec: " + errors);
        }

        if (result.getOpenAPI() == null) {
            throw new SpecParseException("Failed to parse spec: result is null");
        }
    }

    private String extractVersion(OpenAPI openAPI) {
        if (openAPI.getInfo() != null && openAPI.getInfo().getVersion() != null) {
            return openAPI.getInfo().getVersion();
        }
        return "1.0.0"; // Default version
    }

    private List<ParsedEndpoint> extractEndpoints(OpenAPI openAPI) {
        List<ParsedEndpoint> endpoints = new ArrayList<>();

        if (openAPI.getPaths() == null) {
            return endpoints;
        }

        openAPI.getPaths().forEach((path, pathItem) -> {
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                ParsedRestEndpoint endpoint = createParsedEndpoint(path, httpMethod, operation);
                endpoints.add(endpoint);
            });
        });

        return endpoints;
    }

    private ParsedRestEndpoint createParsedEndpoint(String path,
                                                     PathItem.HttpMethod httpMethod,
                                                     Operation operation) {
        HttpMethod method = convertHttpMethod(httpMethod);
        String summary = operation.getSummary() != null ? operation.getSummary() : "";
        String operationId = operation.getOperationId();

        ParsedRestEndpoint endpoint = new ParsedRestEndpoint(method, path, summary, operationId);
        endpoint.setDetailsAsJson(serializeOperation(operation));

        return endpoint;
    }

    private HttpMethod convertHttpMethod(PathItem.HttpMethod swaggerMethod) {
        return HttpMethod.valueOf(swaggerMethod.name());
    }

    private String serializeOperation(Operation operation) {
        try {
            return objectMapper.writeValueAsString(operation);
        } catch (JsonProcessingException e) {
            // If serialization fails, return minimal JSON
            return "{}";
        }
    }
}
