package com.example.demo.orchestrator.infrastructure.persistence.mapper;

import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.SoapApiTestEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for SoapApiTest ↔ SoapApiTestEntity.
 *
 * Handles JSON serialization of SoapRequest.
 */
@Mapper(componentModel = "spring", uses = {AssertionMapper.class})
public abstract class SoapApiTestMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Convert entity to domain.
     */
    @Mapping(target = "request", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", source = "assertions")
    public abstract SoapApiTest toDomain(SoapApiTestEntity entity);

    /**
     * Deserialize request from JSON.
     */
    @AfterMapping
    protected void deserializeRequest(@MappingTarget SoapApiTest test, SoapApiTestEntity entity) {
        if (entity.getRequestJson() != null && !entity.getRequestJson().isBlank()) {
            try {
                SoapRequest request = objectMapper.readValue(entity.getRequestJson(), SoapRequest.class);
                test.setRequest(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize SoapRequest from JSON", e);
            }
        }
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "requestJson", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", ignore = true) // Handled in afterMapping
    @Mapping(target = "testSuiteId", ignore = true) // Managed by JPA
    public abstract SoapApiTestEntity toEntity(SoapApiTest domain);

    /**
     * Serialize request to JSON and map assertions.
     */
    @AfterMapping
    protected void serializeRequestAndAssertions(@MappingTarget SoapApiTestEntity entity, SoapApiTest domain) {
        // Serialize request
        if (domain.getRequest() != null) {
            try {
                String requestJson = objectMapper.writeValueAsString(domain.getRequest());
                entity.setRequestJson(requestJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize SoapRequest to JSON", e);
            }
        }

        // Map assertions
        if (domain.getAssertions() != null) {
            domain.getAssertions().forEach(assertion -> {
                entity.addAssertion(assertionMapper.toEntity(assertion));
            });
        }
    }

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteId", ignore = true)
    @Mapping(target = "requestJson", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", ignore = true) // Handled in afterMapping
    public abstract void updateEntityFromDomain(@MappingTarget SoapApiTestEntity entity, SoapApiTest domain);

    /**
     * Update request JSON and assertions.
     */
    @AfterMapping
    protected void updateRequestAndAssertions(@MappingTarget SoapApiTestEntity entity, SoapApiTest domain) {
        // Update request JSON
        if (domain.getRequest() != null) {
            try {
                String requestJson = objectMapper.writeValueAsString(domain.getRequest());
                entity.setRequestJson(requestJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize SoapRequest to JSON", e);
            }
        } else {
            entity.setRequestJson(null);
        }

        // Clear and rebuild assertions
        entity.getAssertions().clear();
        if (domain.getAssertions() != null) {
            domain.getAssertions().forEach(assertion -> {
                entity.addAssertion(assertionMapper.toEntity(assertion));
            });
        }
    }

    @Autowired
    protected AssertionMapper assertionMapper;
}
