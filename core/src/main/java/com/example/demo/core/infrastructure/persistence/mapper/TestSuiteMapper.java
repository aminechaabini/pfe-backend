package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for TestSuite ↔ TestSuiteEntity.
 *
 * Note: TestCases collection should be loaded separately to avoid circular references.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class, TestCaseMapper.class})
public interface TestSuiteMapper {

    /**
     * Convert entity to domain using reconstitution.
     * Preserves full entity state including identity and timestamps.
     */
    default TestSuite toDomain(TestSuiteEntity entity) {
        if (entity == null) {
            return null;
        }

        return TestSuite.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getVariables(),
            entity.getProject() != null ? entity.getProject().getId() : null,
            entity.getEndpoint() != null ? entity.getEndpoint().getId() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "project", ignore = true)   // Set by repository before save
    @Mapping(target = "endpoint", ignore = true)  // Set by repository before save
    TestSuiteEntity toEntity(TestSuite domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "endpoint", ignore = true)  // Set by repository before save
    void updateEntityFromDomain(@MappingTarget TestSuiteEntity entity, TestSuite domain);

}
