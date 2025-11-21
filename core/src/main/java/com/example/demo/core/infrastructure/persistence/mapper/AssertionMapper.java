package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.infrastructure.persistence.entity.test.AssertionEntity;
import org.mapstruct.Mapper;

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
     * Convert domain to entity.
     */
    AssertionEntity toEntity(Assertion domain);
}
