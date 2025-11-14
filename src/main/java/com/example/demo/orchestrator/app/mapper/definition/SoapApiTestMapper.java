package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.JsonSerializationHelper;
import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.app.mapper.valueobject.AssertionMapper;
import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.request.HttpRequest;
import com.example.demo.orchestrator.domain.test.request.SoapRequest;
import com.example.demo.orchestrator.persistence.entity.test.SoapApiTestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for SoapApiTest domain <-> SoapApiTestEntity persistence.
 *
 * Similar to RestApiTestMapper but handles SoapRequest instead of RestRequest.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AssertionMapper.class}
)
public abstract class SoapApiTestMapper {

    @Autowired
    protected JsonSerializationHelper jsonHelper;

    /**
     * Convert domain SoapApiTest to persistence SoapApiTestEntity.
     */
    @Mapping(source = "request", target = "requestJson", qualifiedByName = "serializeRequest")
    public abstract SoapApiTestEntity toEntity(SoapApiTest domain);

    /**
     * Convert persistence SoapApiTestEntity to domain SoapApiTest.
     */
    @Mapping(source = "requestJson", target = "request", qualifiedByName = "deserializeRequest")
    public abstract SoapApiTest toDomain(SoapApiTestEntity entity);

    // ==================== Custom Mapping Methods ====================

    /**
     * Serialize SoapRequest to JSON string.
     */
    @Named("serializeRequest")
    protected String serializeRequest(HttpRequest<?> request) {
        return jsonHelper.serializeHttpRequest(request);
    }

    /**
     * Deserialize JSON string to SoapRequest.
     * Cast is safe because SoapApiTest only uses SoapRequest.
     */
    @Named("deserializeRequest")
    protected SoapRequest deserializeRequest(String json) {
        HttpRequest<?> request = jsonHelper.deserializeHttpRequest(json);
        return (SoapRequest) request;
    }
}
