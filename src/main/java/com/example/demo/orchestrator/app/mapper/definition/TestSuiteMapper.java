package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for TestSuite domain <-> TestSuiteEntity persistence.
 *
 * Challenge: Circular references
 * - TestSuiteEntity has List<ProjectEntity> projects
 * - ProjectEntity has List<TestSuiteEntity> testSuites
 *
 * Solution: Use @DecoratedWith to handle circular references manually.
 * The decorator will use CycleAvoidingMappingContext to track already-mapped instances.
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {TestCaseMapper.class}
)
@DecoratedWith(TestSuiteMapperDecorator.class)
public interface TestSuiteMapper {

    /**
     * Convert persistence TestSuiteEntity to domain TestSuite.
     *
     * @Context is required to pass the CycleAvoidingMappingContext.
     */
    @Mapping(target = "testCases", ignore = true)  // Handled in decorator
    TestSuite toDomain(TestSuiteEntity entity, @Context CycleAvoidingMappingContext context);

    /**
     * Convert domain TestSuite to persistence TestSuiteEntity.
     *
     * @Context is required to pass the CycleAvoidingMappingContext.
     */
    @Mapping(target = "testCases", ignore = true)  // Handled in decorator
    @Mapping(target = "projects", ignore = true)   // Not mapped in suite->entity direction
    TestSuiteEntity toEntity(TestSuite domain, @Context CycleAvoidingMappingContext context);
}
