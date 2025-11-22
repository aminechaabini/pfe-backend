package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.e2e.E2eStep;
import com.example.demo.core.domain.test.e2e.E2eTest;
import com.example.demo.core.infrastructure.persistence.entity.test.E2eTestEntity;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MapStruct mapper for E2eTest ↔ E2eTestEntity.
 */
@Mapper(componentModel = "spring", uses = {E2eStepMapper.class})
public interface E2eTestMapper {

    /**
     * Convert entity to domain using reconstitution.
     * Maps steps using E2eStepMapper.
     */
    default E2eTest toDomain(E2eTestEntity entity, @Context E2eStepMapper stepMapper) {
        if (entity == null) {
            return null;
        }

        // Map steps
        List<E2eStep> steps = null;
        if (entity.getSteps() != null) {
            steps = entity.getSteps().stream()
                .map(stepMapper::toDomain)
                .collect(Collectors.toList());
        }

        return E2eTest.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            steps,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "steps", source = "steps")
    @Mapping(target = "testSuiteId", ignore = true) // Managed by JPA
    E2eTestEntity toEntity(E2eTest domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuiteId", ignore = true)
    @Mapping(target = "steps", ignore = true) // Handled in afterMapping
    void updateEntityFromDomain(@MappingTarget E2eTestEntity entity, E2eTest domain);

    /**
     * Rebuild steps collection maintaining order.
     */
    @AfterMapping
    default void updateSteps(@MappingTarget E2eTestEntity entity, E2eTest domain, @Context E2eStepMapper stepMapper) {
        entity.getSteps().clear();
        if (domain.getSteps() != null) {
            domain.getSteps().forEach(step -> {
                entity.addStep(stepMapper.toEntity(step));
            });
        }
    }
}
