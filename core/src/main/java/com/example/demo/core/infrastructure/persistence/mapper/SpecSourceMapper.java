package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.spec.SpecSource;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.core.infrastructure.persistence.entity.spec.SpecSourceEntity;
import com.example.demo.core.infrastructure.persistence.jpa.ProjectRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * MapStruct mapper for SpecSource ↔ SpecSourceEntity.
 *
 * SpecSource uses factory method create(), so toDomain() is manual.
 * Endpoints collection is managed separately.
 */
@Mapper(componentModel = "spring", uses = {EndpointMapper.class})
public abstract class SpecSourceMapper {

    @Autowired
    ProjectRepository projectRepository;

    /**
     * Convert entity to domain using reconstitution.
     * Preserves full entity state including identity and timestamps.
     */
    public SpecSource toDomain(SpecSourceEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpecSource.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getFileName(),
            entity.getSpecType(),
            entity.getContent(),
            entity.getVersion(), entity.getProject() != null ? entity.getProject().getId() : null,
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "specType", source = "specType")
    @Mapping(target = "project", expression = "java(mapProjectEntityFromId(domain.getProjectId()))")
    @Mapping(target = "endpoints", ignore = true)
    public abstract SpecSourceEntity toEntity(SpecSource domain);

    ProjectEntity mapProjectEntityFromId(Long id) {
        if (id == null) {
            return null;
        }
        return projectRepository.findById(id).orElse(null);
    }

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "content", ignore = true) // Immutable
    @Mapping(target = "endpoints", ignore = true)
    @Mapping(target = "project", ignore = true)
    public abstract void updateEntityFromDomain(@MappingTarget SpecSourceEntity entity, SpecSource domain);
}
