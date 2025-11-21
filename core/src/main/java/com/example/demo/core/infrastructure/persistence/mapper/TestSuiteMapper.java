package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for TestSuite ↔ TestSuiteEntity.
 *
 * Note: TestCases collection should be loaded separately to avoid circular references.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class})
public interface TestSuiteMapper {

    /**
     * Convert entity to domain.
     */
    @Mapping(target = "testCases", ignore = true) // Load separately
    @Mapping(target = "endpoint", ignore = true) // No getter/setter in domain
    TestSuite toDomain(TestSuiteEntity entity);

    /**
     * Copy variables after mapping.
     */
    @AfterMapping
    default void copyVariables(@MappingTarget TestSuite domain, TestSuiteEntity entity) {
        if (entity.getVariables() != null) {
            entity.getVariables().forEach(domain::setVariable);
        }
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "testCases", ignore = true) // Managed by JPA relationship
    @Mapping(target = "endpoint", ignore = true) // No getter/setter in domain
    TestSuiteEntity toEntity(TestSuite domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "testCases", ignore = true)
    @Mapping(target = "endpoint", ignore = true)
    void updateEntityFromDomain(@MappingTarget TestSuiteEntity entity, TestSuite domain);

    /**
     * Update variables map.
     */
    @AfterMapping
    default void updateVariables(@MappingTarget TestSuiteEntity entity, TestSuite domain) {
        entity.getVariables().clear();
        if (domain.getVariables() != null) {
            entity.getVariables().putAll(domain.getVariables());
        }
    }
}
