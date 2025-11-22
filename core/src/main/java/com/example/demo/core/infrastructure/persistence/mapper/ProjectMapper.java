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
@Mapper(componentModel = "spring", uses = {EndpointMapper.class, TestSuiteMapper.class, SpecSourceMapper.class})
public interface ProjectMapper {

    /**
     * Convert entity to domain using reconstitution.
     * Preserves full entity state including identity and timestamps.
     */
    default Project toDomain(ProjectEntity entity) {
        if (entity == null) {
            return null;
        }

        return Project.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getVariables(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain to entity.
     */

    ProjectEntity toEntity(Project domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDomain(@MappingTarget ProjectEntity entity, Project domain);

}
