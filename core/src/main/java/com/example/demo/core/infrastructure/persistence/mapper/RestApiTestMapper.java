package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.request.RestRequest;
import com.example.demo.core.infrastructure.persistence.entity.test.RestApiTestEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for RestApiTest ↔ RestApiTestEntity.
 *
 * Handles JSON serialization of RestRequest.
 */
@Mapper(componentModel = "spring", uses = {AssertionMapper.class})
public abstract class RestApiTestMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Convert entity to domain.
     */
    @Mapping(target = "request", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", source = "assertions")
    public abstract RestApiTest toDomain(RestApiTestEntity entity);

    /**
     * Deserialize request from JSON.
     */
    @AfterMapping
    protected void deserializeRequest(@MappingTarget RestApiTest test, RestApiTestEntity entity) {
        if (entity.getRequestJson() != null && !entity.getRequestJson().isBlank()) {
            try {
                RestRequest request = objectMapper.readValue(entity.getRequestJson(), RestRequest.class);
                test.setRequest(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize RestRequest from JSON", e);
            }
        }
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "requestJson", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", ignore = true) // Handled in afterMapping
    @Mapping(target = "testSuiteId", ignore = true) // Managed by JPA
    public abstract RestApiTestEntity toEntity(RestApiTest domain);

    /**
     * Serialize request to JSON and map assertions.
     */
    @AfterMapping
    protected void serializeRequestAndAssertions(@MappingTarget RestApiTestEntity entity, RestApiTest domain) {
        // Serialize request
        if (domain.getRequest() != null) {
            try {
                String requestJson = objectMapper.writeValueAsString(domain.getRequest());
                entity.setRequestJson(requestJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize RestRequest to JSON", e);
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
    public abstract void updateEntityFromDomain(@MappingTarget RestApiTestEntity entity, RestApiTest domain);

    /**
     * Update request JSON and assertions.
     */
    @AfterMapping
    protected void updateRequestAndAssertions(@MappingTarget RestApiTestEntity entity, RestApiTest domain) {
        // Update request JSON
        if (domain.getRequest() != null) {
            try {
                String requestJson = objectMapper.writeValueAsString(domain.getRequest());
                entity.setRequestJson(requestJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize RestRequest to JSON", e);
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
