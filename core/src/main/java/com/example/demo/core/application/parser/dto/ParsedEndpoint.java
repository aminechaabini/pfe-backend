package com.example.demo.core.application.parser.dto;

/**
 * Base interface for parsed endpoints from API specifications.
 * Implementations include ParsedRestEndpoint and ParsedSoapEndpoint.
 */
public sealed interface ParsedEndpoint
    permits ParsedRestEndpoint, ParsedSoapEndpoint {

    /**
     * Get the name/identifier for this endpoint.
     */
    String name();

    /**
     * Get the summary/description of this endpoint.
     */
    String summary();

    /**
     * Get the operation ID (may be null for some specs).
     */
    String operationId();

    /**
     * Get detailed specification data as JSON string.
     */
    String specDetails();
}
