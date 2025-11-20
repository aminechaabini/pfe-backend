package com.example.demo.orchestrator.infrastructure.persistence.mapper;

import com.example.demo.orchestrator.domain.spec.Endpoint;
import com.example.demo.orchestrator.domain.spec.RestEndpoint;
import com.example.demo.orchestrator.domain.spec.SoapEndpoint;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.RestEndpointEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.SoapEndpointEntity;
import org.springframework.stereotype.Component;

/**
 * Polymorphic mapper for Endpoint hierarchy.
 *
 * Delegates to specific mappers based on endpoint type (REST/SOAP).
 */
@Component
public class EndpointMapper {

    private final RestEndpointMapper restEndpointMapper;
    private final SoapEndpointMapper soapEndpointMapper;

    public EndpointMapper(
            RestEndpointMapper restEndpointMapper,
            SoapEndpointMapper soapEndpointMapper) {
        this.restEndpointMapper = restEndpointMapper;
        this.soapEndpointMapper = soapEndpointMapper;
    }

    /**
     * Convert entity to domain (polymorphic).
     */
    public Endpoint toDomain(EndpointEntity entity) {
        if (entity == null) {
            return null;
        }

        return switch (entity) {
            case RestEndpointEntity restEntity -> restEndpointMapper.toDomain(restEntity);
            case SoapEndpointEntity soapEntity -> soapEndpointMapper.toDomain(soapEntity);
            default -> throw new IllegalArgumentException("Unknown endpoint type: " + entity.getClass());
        };
    }

    /**
     * Convert domain to entity (polymorphic).
     */
    public EndpointEntity toEntity(Endpoint domain) {
        if (domain == null) {
            return null;
        }

        return switch (domain) {
            case RestEndpoint restEndpoint -> restEndpointMapper.toEntity(restEndpoint);
            case SoapEndpoint soapEndpoint -> soapEndpointMapper.toEntity(soapEndpoint);
            default -> throw new IllegalArgumentException("Unknown endpoint type: " + domain.getClass());
        };
    }

    /**
     * Update existing entity from domain (polymorphic).
     */
    public void updateEntityFromDomain(EndpointEntity entity, Endpoint domain) {
        if (entity == null || domain == null) {
            return;
        }

        // Match entity and domain types
        switch (entity) {
            case RestEndpointEntity restEntity when domain instanceof RestEndpoint restDomain ->
                    restEndpointMapper.updateEntityFromDomain(restEntity, restDomain);
            case SoapEndpointEntity soapEntity when domain instanceof SoapEndpoint soapDomain ->
                    soapEndpointMapper.updateEntityFromDomain(soapEntity, soapDomain);
            default -> throw new IllegalArgumentException(
                    "Type mismatch: entity=" + entity.getClass() + ", domain=" + domain.getClass());
        }
    }
}
