package com.example.demo.orchestrator.infrastructure.spec;

import com.example.demo.orchestrator.app.service.spec.*;
import com.example.demo.orchestrator.domain.spec.HttpMethod;
import com.example.demo.orchestrator.domain.spec.SpecType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.models.Swagger;
import io.swagger.models.reader.SwaggerParser;
import io.swagger.parser.OpenAPIParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.stereotype.Component;

import java.io.File;
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
public class RestSpecParser {

    public void parse(String content){
        SwaggerParseResult result = new OpenAPIParser().readContents(content, null, null);

        OpenAPI openAPI = result.getOpenAPI();

        if (result.getMessages() != null) result.getMessages().forEach(System.err::println); // validation errors and warnings

        if (openAPI != null) {
            if (openAPI.getPaths() == null) {
                System.out.println("No paths found.");
                return;
            }

            // Extract endpoints
            openAPI.getPaths().forEach((path, item) -> {
                if (item == null) return;

                item.readOperationsMap().forEach((httpMethod, operation) -> {
                    System.out.println(httpMethod.toString().toUpperCase() + " " + path);
                });
            });

            return;
        }

        System.out.println("Spec is not valid OpenAPI 3 (or failed to parse).");
    }

}

