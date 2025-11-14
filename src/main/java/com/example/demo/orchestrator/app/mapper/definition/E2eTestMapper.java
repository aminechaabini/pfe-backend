package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.app.mapper.valueobject.E2eStepMapper;
import com.example.demo.orchestrator.domain.test.e2e.E2eTest;
import com.example.demo.orchestrator.persistence.entity.test.E2eTestEntity;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for E2eTest domain <-> E2eTestEntity persistence.
 *
 * E2E tests are straightforward - they contain a list of steps.
 * The E2eStepMapper handles the step-level mapping.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {E2eStepMapper.class}
)
public interface E2eTestMapper {

    /**
     * Convert domain E2eTest to persistence E2eTestEntity.
     * Steps are mapped automatically by E2eStepMapper.
     */
    E2eTestEntity toEntity(E2eTest domain);

    /**
     * Convert persistence E2eTestEntity to domain E2eTest.
     * Steps are mapped automatically by E2eStepMapper.
     */
    E2eTest toDomain(E2eTestEntity entity);
}
