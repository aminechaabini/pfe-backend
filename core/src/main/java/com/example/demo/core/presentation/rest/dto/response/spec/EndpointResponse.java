package com.example.demo.core.presentation.rest.dto.response.spec;

import com.example.demo.core.domain.spec.EndpointType;
import com.example.demo.core.domain.spec.HttpMethod;

import java.time.Instant;

/**
 * API response DTO for Endpoint.
 * Contains both REST and SOAP fields (only relevant ones will be populated).
 */
public record EndpointResponse(
        Long id,
        EndpointType type,
        String name,
        String summary,
        String operationId,

        // REST-specific fields (null for SOAP)
        HttpMethod method,
        String path,

        // SOAP-specific fields (null for REST)
        String serviceName,
        String operationName,
        String soapAction,

        Instant createdAt
) {
}
