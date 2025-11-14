package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.JsonSerializationHelper;
import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.app.mapper.valueobject.AssertionMapper;
import com.example.demo.orchestrator.domain.test.api.RestApiTest;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.RestRequest;
import com.example.demo.orchestrator.persistence.entity.test.RestApiTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for RestApiTest domain <-> RestApiTestEntity persistence.
 *
 * Challenge: Entity stores complex objects as JSON:
 * - request (RestRequest) → requestJson (String)
 * - assertions (List<Assertion>) → stored in separate table
 *
 * Solution: Use JsonSerializationHelper to serialize/deserialize HttpRequest.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AssertionMapper.class}
)
public abstract class RestApiTestMapper {

    @Autowired
    protected JsonSerializationHelper jsonHelper;

    /**
     * Convert domain RestApiTest to persistence RestApiTestEntity.
     */
    @Mapping(source = "request", target = "requestJson", qualifiedByName = "serializeRequest")
    public abstract RestApiTestEntity toEntity(RestApiTest domain);

    /**
     * Convert persistence RestApiTestEntity to domain RestApiTest.
     */
    @Mapping(source = "requestJson", target = "request", qualifiedByName = "deserializeRequest")
    public abstract RestApiTest toDomain(RestApiTestEntity entity);

    // ==================== Custom Mapping Methods ====================

    /**
     * Serialize RestRequest to JSON string.
     */
    @Named("serializeRequest")
    protected String serializeRequest(HttpRequest<?> request) {
        return jsonHelper.serializeHttpRequest(request);
    }

    /**
     * Deserialize JSON string to RestRequest.
     * Cast is safe because RestApiTest only uses RestRequest.
     */
    @Named("deserializeRequest")
    protected RestRequest deserializeRequest(String json) {
        HttpRequest<?> request = jsonHelper.deserializeHttpRequest(json);
        return (RestRequest) request;
    }
}
