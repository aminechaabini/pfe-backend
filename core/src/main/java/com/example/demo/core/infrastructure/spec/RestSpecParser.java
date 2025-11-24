package com.example.demo.core.infrastructure.spec;

import com.example.demo.core.application.parser.SpecParser;
import com.example.demo.core.application.parser.SpecParseException;
import com.example.demo.core.application.parser.dto.ParsedEndpoint;
import com.example.demo.core.application.parser.dto.ParsedRestEndpoint;
import com.example.demo.core.application.parser.dto.ParsedSpec;
import com.example.demo.core.domain.spec.SpecType;
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
import java.util.Map;

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

    public RestSpecParser() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ParsedSpec parse(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content cannot be null or blank");
        }

        SwaggerParseResult result = new OpenAPIV3Parser().readContents(content, null, null);
        OpenAPI openAPI = result.getOpenAPI();

        if (openAPI == null) {
            throw new SpecParseException("Failed to parse OpenAPI content: " + joinErrors(result));
        }

        String version = null;
        if (openAPI.getInfo() != null) {
            version = openAPI.getInfo().getVersion();
        }

        List<ParsedEndpoint> endpoints = new ArrayList<>();
        if (openAPI.getPaths() != null) {
            for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
                String path = pathEntry.getKey();
                PathItem item = pathEntry.getValue();
                if (item == null) continue;

                // readOperationsMap returns Map<PathItem.HttpMethod, Operation>
                for (Map.Entry<PathItem.HttpMethod, Operation> opEntry : item.readOperationsMap().entrySet()) {
                    PathItem.HttpMethod method = opEntry.getKey();
                    Operation operation = opEntry.getValue();
                    if (operation == null) continue;

                    String name = operation.getTags() != null && !operation.getTags().isEmpty()
                            ? operation.getTags().get(0)
                            : operation.getSummary();

                    String summary = operation.getSummary();
                    String operationId = operation.getOperationId();

                    String specDetails;
                    try {
                        specDetails = objectMapper.writeValueAsString(operation);
                    } catch (JsonProcessingException e) {
                        // fall back to a compact string to avoid failing the entire parse
                        specDetails = "{\"operationId\":\"" + (operationId != null ? operationId : "") + "\"}";
                    }

                    // Convert HTTP method to string
                    String httpMethod = method.name();

                    // Create ParsedRestEndpoint
                    ParsedRestEndpoint endpoint = new ParsedRestEndpoint(
                            httpMethod,
                            path,
                            name,
                            summary,
                            operationId,
                            specDetails
                    );

                    endpoints.add(endpoint);
                }
            }
        }

        return new ParsedSpec(version, endpoints);
    }

    @Override
    public boolean supports(SpecType specType) {
        return specType != null && specType.isRest();
    }

    /**
     * Helper: convert parse errors to single string for exception message.
     */
    private static String joinErrors(SwaggerParseResult result) {
        if (result == null || result.getMessages() == null || result.getMessages().isEmpty()) {
            return "unknown parse error";
        }
        return String.join("; ", result.getMessages());
    }
}
