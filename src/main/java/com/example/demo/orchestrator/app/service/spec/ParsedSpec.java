package com.example.demo.orchestrator.app.service.spec;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value object representing the result of parsing an API specification.
 *
 * This is a neutral, infrastructure-agnostic format that bridges
 * the gap between external spec formats and our domain model.
 */
public class ParsedSpec {

    private final String version;
    private final List<ParsedEndpoint> endpoints;

    public ParsedSpec(String version, List<ParsedEndpoint> endpoints) {
        this.version = version;
        this.endpoints = Collections.unmodifiableList(
            Objects.requireNonNull(endpoints, "Endpoints cannot be null")
        );
    }

    public String getVersion() {
        return version;
    }

    public List<ParsedEndpoint> getEndpoints() {
        return endpoints;
    }

    public int getEndpointCount() {
        return endpoints.size();
    }

    public boolean isEmpty() {
        return endpoints.isEmpty();
    }

    @Override
    public String toString() {
        return "ParsedSpec{" +
                "version='" + version + '\'' +
                ", endpointCount=" + endpoints.size() +
                '}';
    }
}
