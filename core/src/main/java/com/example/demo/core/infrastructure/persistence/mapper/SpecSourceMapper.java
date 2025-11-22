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
@Mapper(componentModel = "spring", uses = {EndpointMapper.class})
public interface SpecSourceMapper {

    /**
     * Convert entity to domain using reconstitution.
     * Preserves full entity state including identity and timestamps.
     */
    default SpecSource toDomain(SpecSourceEntity entity) {
        if (entity == null) {
            return null;
        }

        return SpecSource.reconstitute(
            entity.getId(),
            entity.getName(),
            entity.getFileName(),
            entity.getSpecType(),
            entity.getContent(),
            entity.getVersion(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
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
