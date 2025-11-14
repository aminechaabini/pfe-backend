package com.example.demo.orchestrator.app.mapper.valueobject;

import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.run.AssertionResult;
import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.persistence.entity.run.AssertionResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for AssertionResult domain <-> AssertionResultEntity persistence.
 *
 * Challenge: The domain and entity have different structures:
 *
 * Domain (normalized):
 * - assertion: Assertion object
 * - ok: boolean
 * - message: String
 *
 * Entity (denormalized):
 * - assertionId: Long (FK to assertion)
 * - assertionType: AssertionType
 * - target: String
 * - expectedValue: String
 * - actualValue: String
 * - passed: boolean
 * - errorMessage: String
 *
 * The entity stores assertion fields directly for analytics queries,
 * while the domain references the assertion object.
 */
@Mapper(config = MapStructConfig.class)
public interface AssertionResultMapper {

    /**
     * Convert domain AssertionResult to persistence AssertionResultEntity.
     *
     * Maps:
     * - assertion.type() → assertionType
     * - assertion.target() → target
     * - assertion.expected() → expectedValue
     * - ok → passed
     * - message → errorMessage
     */
    @Mapping(source = "assertion.type", target = "assertionType")
    @Mapping(source = "assertion.target", target = "target")
    @Mapping(source = "assertion.expected", target = "expectedValue")
    @Mapping(source = "ok", target = "passed")
    @Mapping(source = "message", target = "errorMessage")
    @Mapping(target = "assertionId", ignore = true)  // Set separately in service layer
    @Mapping(target = "actualValue", ignore = true)  // Set separately in service layer
    AssertionResultEntity toEntity(AssertionResult domain);

    /**
     * Convert persistence AssertionResultEntity to domain AssertionResult.
     *
     * Reconstructs the Assertion object from denormalized fields.
     */
    @Mapping(target = "assertion", expression = "java(new Assertion(entity.getAssertionType(), entity.getTarget(), entity.getExpectedValue()))")
    @Mapping(source = "passed", target = "ok")
    @Mapping(source = "errorMessage", target = "message")
    AssertionResult toDomain(AssertionResultEntity entity);

    /**
     * Convert list of domain AssertionResults to list of AssertionResultEntities.
     */
    List<AssertionResultEntity> toEntityList(List<AssertionResult> domainList);

    /**
     * Convert list of AssertionResultEntities to list of domain AssertionResults.
     */
    List<AssertionResult> toDomainList(List<AssertionResultEntity> entityList);
}
