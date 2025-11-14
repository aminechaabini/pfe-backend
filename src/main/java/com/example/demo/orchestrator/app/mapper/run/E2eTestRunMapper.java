package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.run.E2eTestRun;
import com.example.demo.orchestrator.persistence.entity.run.E2eTestRunEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for E2eTestRun domain <-> E2eTestRunEntity persistence.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {E2eStepRunMapper.class}
)
public interface E2eTestRunMapper {

    E2eTestRunEntity toEntity(E2eTestRun domain);
    E2eTestRun toDomain(E2eTestRunEntity entity);
}
