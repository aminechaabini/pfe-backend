package com.example.demo.orchestrator.infrastructure.persistence.mapper;

import com.example.demo.orchestrator.domain.test.e2e.E2eStep;
import com.example.demo.orchestrator.domain.test.e2e.ExtractorItem;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.E2eStepEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for E2eStep ↔ E2eStepEntity.
 *
 * Handles JSON serialization of HttpRequest and extractors.
 */
@Mapper(componentModel = "spring", uses = {AssertionMapper.class})
public abstract class E2eStepMapper {

    @Autowired
    protected ObjectMapper objectMapper;

    /**
     * Convert entity to domain.
     */
    @Mapping(target = "httpRequest", ignore = true) // Handled in afterMapping
    @Mapping(target = "extractorItems", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", source = "assertions")
    public abstract E2eStep toDomain(E2eStepEntity entity);

    /**
     * Deserialize request and extractors from JSON.
     */
    @AfterMapping
    protected void deserializeJsonFields(@MappingTarget E2eStep step, E2eStepEntity entity) {
        // Deserialize request
        if (entity.getHttpRequestJson() != null && !entity.getHttpRequestJson().isBlank()) {
            try {
                @SuppressWarnings("unchecked")
                HttpRequest request = objectMapper.readValue(
                    entity.getHttpRequestJson(),
                    HttpRequest.class
                );
                step.setHttpRequest(request);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize HttpRequest from JSON", e);
            }
        }

        // Deserialize extractors
        if (entity.getExtractorsJson() != null && !entity.getExtractorsJson().isBlank()) {
            try {
                ExtractorItem[] extractors = objectMapper.readValue(
                    entity.getExtractorsJson(),
                    ExtractorItem[].class
                );
                for (ExtractorItem extractor : extractors) {
                    step.addExtractor(extractor);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize ExtractorItem from JSON", e);
            }
        }
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "httpRequestJson", ignore = true) // Handled in afterMapping
    @Mapping(target = "extractorsJson", ignore = true) // Handled in afterMapping
    @Mapping(target = "assertions", ignore = true) // Handled in afterMapping
    @Mapping(target = "e2eTestId", ignore = true) // Managed by JPA
    public abstract E2eStepEntity toEntity(E2eStep domain);

    /**
     * Serialize request and extractors to JSON and map assertions.
     */
    @AfterMapping
    protected void serializeJsonFieldsAndAssertions(@MappingTarget E2eStepEntity entity, E2eStep domain) {
        // Serialize request
        if (domain.getHttpRequest() != null) {
            try {
                String requestJson = objectMapper.writeValueAsString(domain.getHttpRequest());
                entity.setHttpRequestJson(requestJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize HttpRequest to JSON", e);
            }
        }

        // Serialize extractors
        if (domain.getExtractorItems() != null && !domain.getExtractorItems().isEmpty()) {
            try {
                String extractorsJson = objectMapper.writeValueAsString(domain.getExtractorItems());
                entity.setExtractorsJson(extractorsJson);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to serialize ExtractorItem to JSON", e);
            }
        }

        // Map assertions
        if (domain.getAssertions() != null) {
            domain.getAssertions().forEach(assertion -> {
                entity.addAssertion(toAssertionEntity(assertion));
            });
        }
    }

    @Autowired
    protected AssertionMapper assertionMapper;

    protected com.example.demo.orchestrator.infrastructure.persistence.entity.test.AssertionEntity toAssertionEntity(
            com.example.demo.orchestrator.domain.test.assertion.Assertion assertion) {
        return assertionMapper.toEntity(assertion);
    }
}
