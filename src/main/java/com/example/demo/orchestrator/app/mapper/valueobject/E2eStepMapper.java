package com.example.demo.orchestrator.app.mapper.valueobject;

import com.example.demo.orchestrator.app.mapper.config.JsonSerializationHelper;
import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.test.e2e.E2eStep;
import com.example.demo.orchestrator.domain.test.e2e.ExtractorItem;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.body.Body;
import com.example.demo.orchestrator.persistence.entity.test.E2eStepEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * MapStruct mapper for E2eStep domain <-> E2eStepEntity persistence.
 *
 * Challenge: Entity stores complex objects as JSON strings:
 * - httpRequest → httpRequestJson
 * - extractorItems → extractorsJson
 * - assertions → stored in separate table (use AssertionMapper)
 *
 * Solution: Use JsonSerializationHelper for JSON conversion.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AssertionMapper.class}
)
public abstract class E2eStepMapper {

    @Autowired
    protected JsonSerializationHelper jsonHelper;

    /**
     * Convert domain E2eStep to persistence E2eStepEntity.
     */
    @Mapping(source = "httpRequest", target = "httpRequestJson", qualifiedByName = "serializeHttpRequest")
    @Mapping(source = "extractorItems", target = "extractorsJson", qualifiedByName = "serializeExtractors")
    public abstract E2eStepEntity toEntity(E2eStep domain);

    /**
     * Convert persistence E2eStepEntity to domain E2eStep.
     */
    @Mapping(source = "httpRequestJson", target = "httpRequest", qualifiedByName = "deserializeHttpRequest")
    @Mapping(source = "extractorsJson", target = "extractorItems", qualifiedByName = "deserializeExtractors")
    public abstract E2eStep toDomain(E2eStepEntity entity);

    /**
     * Convert list of domain E2eSteps to list of E2eStepEntities.
     */
    public abstract List<E2eStepEntity> toEntityList(List<E2eStep> domainList);

    /**
     * Convert list of E2eStepEntities to list of domain E2eSteps.
     */
    public abstract List<E2eStep> toDomainList(List<E2eStepEntity> entityList);

    // ==================== Custom Mapping Methods ====================

    /**
     * Serialize HttpRequest to JSON string.
     */
    @Named("serializeHttpRequest")
    protected String serializeHttpRequest(HttpRequest<Body> request) {
        return jsonHelper.serializeHttpRequest(request);
    }

    /**
     * Deserialize JSON string to HttpRequest.
     */
    @Named("deserializeHttpRequest")
    protected HttpRequest<?> deserializeHttpRequest(String json) {
        return jsonHelper.deserializeHttpRequest(json);
    }

    /**
     * Serialize list of ExtractorItems to JSON string.
     */
    @Named("serializeExtractors")
    protected String serializeExtractors(List<ExtractorItem> extractors) {
        return jsonHelper.serializeExtractors(extractors);
    }

    /**
     * Deserialize JSON string to list of ExtractorItems.
     */
    @Named("deserializeExtractors")
    protected List<ExtractorItem> deserializeExtractors(String json) {
        return jsonHelper.deserializeExtractors(json);
    }
}
