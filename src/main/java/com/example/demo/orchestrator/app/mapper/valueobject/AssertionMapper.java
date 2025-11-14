package com.example.demo.orchestrator.app.mapper.valueobject;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.persistence.entity.test.AssertionEntity;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for Assertion domain <-> AssertionEntity persistence.
 *
 * Assertion is a simple value object (record) with 3 fields:
 * - type: AssertionType enum
 * - target: String (e.g., "$.user.name", "status")
 * - expected: String (expected value)
 *
 * This is a straightforward 1:1 mapping with no special logic needed.
 */
@Mapper(config = MapStructConfig.class)
public interface AssertionMapper {

    /**
     * Convert domain Assertion to persistence AssertionEntity.
     */
    AssertionEntity toEntity(Assertion domain);

    /**
     * Convert persistence AssertionEntity to domain Assertion.
     */
    Assertion toDomain(AssertionEntity entity);

    /**
     * Convert list of domain Assertions to list of AssertionEntities.
     */
    List<AssertionEntity> toEntityList(List<Assertion> domainList);

    /**
     * Convert list of AssertionEntities to list of domain Assertions.
     */
    List<Assertion> toDomainList(List<AssertionEntity> entityList);
}
