package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.infrastructure.persistence.entity.test.AssertionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Assertion (value object) ↔ AssertionEntity.
 */
@Mapper(componentModel = "spring")
public interface AssertionMapper {

    /**
     * Convert entity to domain.
     */
    Assertion toDomain(AssertionEntity entity);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(@MappingTarget AssertionEntity entity, Assertion domain);

    /**
     * Convert list of entities to list of domains.
     */
    List<Assertion> toDomainList(List<AssertionEntity> entities);

    /**
     * Helper to create entity from domain (creates new entity then updates it).
     */
    default AssertionEntity toEntity(Assertion domain) {
        if (domain == null) {
            return null;
        }
        AssertionEntity entity = new AssertionEntity();
        updateEntityFromDomain(entity, domain);
        return entity;
    }

    /**
     * Convert list of domains to list of entities.
     */
    default List<AssertionEntity> toEntityList(List<Assertion> domains) {
        if (domains == null) {
            return null;
        }
        return domains.stream()
            .map(this::toEntity)
            .collect(java.util.stream.Collectors.toList());
    }
}
