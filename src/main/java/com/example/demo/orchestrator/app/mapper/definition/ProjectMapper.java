package com.example.demo.orchestrator.app.mapper.definition;

import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.app.mapper.config.MapStructConfig;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.persistence.entity.project.ProjectEntity;
import org.mapstruct.Context;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for Project domain <-> ProjectEntity persistence.
 *
 * Challenges:
 * 1. Domain uses factory method Project.create() instead of constructor
 * 2. Circular references: Project ↔ TestSuite (bidirectional many-to-many)
 *
 * Solution: Use @DecoratedWith to handle both challenges manually.
 * The decorator will:
 * - Call Project.create() factory method
 * - Use CycleAvoidingMappingContext to track already-mapped instances
 */
@Mapper(
    config = MapStructConfig.class,
    uses = {TestSuiteMapper.class}
)
@DecoratedWith(ProjectMapperDecorator.class)
public interface ProjectMapper {

    /**
     * Convert persistence ProjectEntity to domain Project.
     *
     * @Context is required to pass the CycleAvoidingMappingContext.
     */
    @Mapping(target = "testSuites", ignore = true)  // Handled in decorator
    Project toDomain(ProjectEntity entity, @Context CycleAvoidingMappingContext context);

    /**
     * Convert domain Project to persistence ProjectEntity.
     *
     * @Context is required to pass the CycleAvoidingMappingContext.
     */
    @Mapping(target = "testSuites", ignore = true)  // Handled in decorator
    ProjectEntity toEntity(Project domain, @Context CycleAvoidingMappingContext context);
}
