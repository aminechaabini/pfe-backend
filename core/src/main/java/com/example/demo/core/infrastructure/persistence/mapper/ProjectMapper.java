package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.project.Project;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for Project ↔ ProjectEntity.
 *
 * Since Project uses factory method and has no setters, toDomain() is implemented manually.
 * Collections (testSuites, specSources, endpoints) are managed separately by services.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper {

    /**
     * Convert entity to domain using factory method.
     * Manually implemented because Project has no public constructor.
     */
    default Project toDomain(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }
        Project project = Project.create(entity.getName(), entity.getDescription());
        // MapStruct cannot set id, createdAt, updatedAt as there are no setters
        // These must be set via reflection or left as is

        // Copy variables using domain method
        if (entity.getVariables() != null) {
            entity.getVariables().forEach(project::setVariable);
        }
        return project;
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "testSuites", ignore = true)
    @Mapping(target = "specSources", ignore = true)
    @Mapping(target = "endpoints", ignore = true)
    ProjectEntity toEntity(Project domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "testSuites", ignore = true)
    @Mapping(target = "specSources", ignore = true)
    @Mapping(target = "endpoints", ignore = true)
    @Mapping(target = "variables", ignore = true) // Handled in afterMapping
    void updateEntityFromDomain(@MappingTarget ProjectEntity entity, Project domain);

    /**
     * Update variables map.
     */
    @AfterMapping
    default void updateVariables(@MappingTarget ProjectEntity entity, Project domain) {
        entity.getVariables().clear();
        if (domain.getVariables() != null) {
            entity.getVariables().putAll(domain.getVariables());
        }
    }
}
