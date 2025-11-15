package com.example.demo.orchestrator.app.service.spec;

import com.example.demo.orchestrator.domain.spec.SpecType;

/**
 * Port (interface) for parsing API specifications.
 *
 * This is the contract that the application layer defines.
 * Infrastructure layer provides the actual implementations.
 *
 * Implementations exist for:
 * - REST specs (OpenAPI, Swagger)
 * - SOAP specs (WSDL)
 */
public interface SpecParser {

    /**
     * Parse an API specification and extract endpoints.
     *
     * @param content the raw spec content (YAML, JSON, or XML)
     * @param specType the type of specification
     * @return parsed specification with extracted endpoints
     * @throws SpecParseException if parsing fails
     */
    ParsedSpec parse(String content, SpecType specType);
}
