package com.example.demo.orchestrator.api.mapper;

import com.example.demo.orchestrator.api.dto.response.spec.EndpointResponse;
import com.example.demo.orchestrator.domain.spec.Endpoint;
import com.example.demo.orchestrator.domain.spec.RestEndpoint;
import com.example.demo.orchestrator.domain.spec.SoapEndpoint;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper: Endpoint (domain) → EndpointResponse (API DTO).
 * Handles polymorphic mapping for REST and SOAP endpoints.
 */
@Mapper(componentModel = "spring")
public interface EndpointResponseMapper {

    /**
     * Map endpoint to response (polymorphic).
     */
    default EndpointResponse toResponse(Endpoint domain) {
        if (domain == null) {
            return null;
        }

        return switch (domain) {
            case RestEndpoint rest -> new EndpointResponse(
                    rest.getId(),
                    rest.getType(),
                    rest.getName(),
                    rest.getSummary(),
                    rest.getOperationId(),
                    rest.getMethod(),
                    rest.getPath(),
                    null, null, null,
                    rest.getCreatedAt()
            );
            case SoapEndpoint soap -> new EndpointResponse(
                    soap.getId(),
                    soap.getType(),
                    soap.getName(),
                    soap.getSummary(),
                    soap.getOperationId(),
                    null, null,
                    soap.getServiceName(),
                    soap.getOperationName(),
                    soap.getSoapAction(),
                    soap.getCreatedAt()
            );
            default -> throw new IllegalArgumentException("Unknown endpoint type: " + domain.getClass());
        };
    }

    default List<EndpointResponse> toResponseList(List<Endpoint> domains) {
        if (domains == null) {
            return null;
        }
        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
}
