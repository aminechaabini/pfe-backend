package com.example.demo.orchestrator.app.mapper.run;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.app.mapper.valueobject.AssertionResultMapper;
import com.example.demo.orchestrator.domain.run.E2eStepRun;
import com.example.demo.orchestrator.persistence.entity.run.E2eStepRunEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for E2eStepRun domain <-> E2eStepRunEntity persistence.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {AssertionResultMapper.class}
)
public interface E2eStepRunMapper {

    E2eStepRunEntity toEntity(E2eStepRun domain);
    E2eStepRun toDomain(E2eStepRunEntity entity);
    List<E2eStepRun> toDomainList(List<E2eStepRunEntity> entityList);
    List<E2eStepRunEntity> toEntityList(List<E2eStepRun> domainList);
}
