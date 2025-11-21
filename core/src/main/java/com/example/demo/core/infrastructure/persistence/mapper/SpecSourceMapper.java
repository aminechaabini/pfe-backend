package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.spec.SpecSource;
import com.example.demo.core.infrastructure.persistence.entity.spec.SpecSourceEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for SpecSource ↔ SpecSourceEntity.
 *
 * SpecSource uses factory method create(), so toDomain() is manual.
 * Endpoints collection is managed separately.
 */
@Mapper(componentModel = "spring")
public interface SpecSourceMapper {

    /**
     * Convert entity to domain using factory method.
     * Note: Factory method signature is create(name, fileName, specType, content).
     * fileName is required but may not be in entity, using name as fallback.
     */
    default SpecSource toDomain(SpecSourceEntity entity) {
        if (entity == null) {
            return null;
        }
        // Factory method needs: name, fileName, specType, content
        // fileName may not exist in entity, use name as fallback
        String fileName = entity.getName() + ".spec";
        SpecSource specSource = SpecSource.create(
            entity.getName(),
            fileName,
            entity.getSpecType(),
            entity.getContent()
        );
        // Set version if present
        if (entity.getVersion() != null) {
            specSource.setVersion(entity.getVersion());
        }
        // No setters for id, createdAt, updatedAt in domain
        return specSource;
    }

    /**
     * Convert domain to entity.
     */
    @Mapping(target = "specType", source = "specType")
    @Mapping(target = "endpoints", ignore = true)
    @Mapping(target = "project", ignore = true)
    SpecSourceEntity toEntity(SpecSource domain);

    /**
     * Update existing entity from domain.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "content", ignore = true) // Immutable
    @Mapping(target = "endpoints", ignore = true)
    @Mapping(target = "project", ignore = true)
    void updateEntityFromDomain(@MappingTarget SpecSourceEntity entity, SpecSource domain);
}
