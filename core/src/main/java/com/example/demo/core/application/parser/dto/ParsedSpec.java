package com.example.demo.core.application.parser.dto;

import java.util.List;

/**
 * Parsed API specification data.
 *
 * <p>Contains the version and extracted endpoints from a spec.
 * This is a DTO used to transfer parsed data from parser to service layer.
 */
public record ParsedSpec(
        String version,
        List<ParsedEndpoint> endpoints
) {
    public ParsedSpec {
        if (endpoints == null) {
            throw new IllegalArgumentException("Endpoints list cannot be null");
        }
        // Make defensive copy to ensure immutability
        endpoints = List.copyOf(endpoints);
    }

    /**
     * Create a parsed spec with no version information.
     */
    public static ParsedSpec withoutVersion(List<ParsedEndpoint> endpoints) {
        return new ParsedSpec(null, endpoints);
    }
}
