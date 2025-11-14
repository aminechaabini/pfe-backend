package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.app.mapper.valueobject.AssertionResultMapper;
import com.example.demo.orchestrator.domain.run.ApiTestRun;
import com.example.demo.orchestrator.persistence.entity.run.ApiTestRunEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for ApiTestRun domain <-> ApiTestRunEntity persistence.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AssertionResultMapper.class}
)
public interface ApiTestRunMapper {

    ApiTestRunEntity toEntity(ApiTestRun domain);
    ApiTestRun toDomain(ApiTestRunEntity entity);
}
