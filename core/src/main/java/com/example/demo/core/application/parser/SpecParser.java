package com.example.demo.core.application.parser;

import com.example.demo.core.application.parser.dto.ParsedSpec;
import com.example.demo.core.domain.spec.SpecType;

/**
 * Port interface for parsing API specifications.
 *
 * <p>Implementations parse different spec formats (OpenAPI, Swagger, WSDL)
 * and extract structured data (version, endpoints, operations).
 *
 * <p>This is a port in hexagonal architecture - domain/application layer
 * defines what it needs, infrastructure provides implementations.
 */
public interface SpecParser {

    /**
     * Parse an API specification and extract structured data.
     *
     * @param specContent the raw specification content (YAML, JSON, or XML)
     * @return parsed specification with version and endpoints
     * @throws SpecParsingException if parsing fails
     */
    ParsedSpec parse(String specContent);

    /**
     * Check if this parser supports the given spec type.
     *
     * @param specType the specification type
     * @return true if this parser can handle the spec type
     */
    boolean supports(SpecType specType);
}
