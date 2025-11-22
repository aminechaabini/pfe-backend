package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.e2e.E2eStep;
import com.example.demo.core.domain.test.e2e.ExtractorItem;
import com.example.demo.core.domain.test.request.HttpRequest;
import com.example.demo.core.infrastructure.persistence.entity.test.E2eStepEntity;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for E2eStep ↔ E2eStepEntity.
 *
 * Handles JSON serialization of HttpRequest and extractors using JsonConverter.
 */
@Mapper(componentModel = "spring", uses = {AssertionMapper.class, JsonConverter.class})
public abstract class E2eStepMapper {

    protected JsonConverter jsonConverter;
    protected AssertionMapper assertionMapper;

    /**
     * Convert entity to domain using reconstitution.
     * Deserializes JSON fields and preserves full state.
     */
    public E2eStep toDomain(E2eStepEntity entity) {
        if (entity == null) {
            return null;
        }

        // Deserialize request using JsonConverter
        HttpRequest httpRequest = jsonConverter.jsonToHttpRequest(entity.getHttpRequestJson());

        // Deserialize extractors using JsonConverter
        List<ExtractorItem> extractors = jsonConverter.jsonToExtractorItems(entity.getExtractorsJson());

        // Map assertions
        List<Assertion> assertions = null;
        if (entity.getAssertions() != null) {
            assertions = entity.getAssertions().stream()
                .map(assertionMapper::toDomain)
                .collect(Collectors.toList());
        }

        return E2eStep.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getOrderIndex(),
            httpRequest,
            assertions,
            extractors,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
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
        // Serialize request using JsonConverter
        entity.setHttpRequestJson(jsonConverter.httpRequestToJson(domain.getHttpRequest()));

        // Serialize extractors using JsonConverter
        entity.setExtractorsJson(jsonConverter.extractorItemsToJson(domain.getExtractorItems()));

        // Map assertions
        if (domain.getAssertions() != null) {
            domain.getAssertions().forEach(assertion -> {
                entity.addAssertion(assertionMapper.toEntity(assertion));
            });
        }
    }
}
