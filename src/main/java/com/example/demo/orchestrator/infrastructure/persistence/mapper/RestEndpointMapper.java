package com.example.demo.orchestrator.infrastructure.persistence.mapper;

import com.example.demo.orchestrator.domain.spec.RestEndpoint;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.RestEndpointEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for RestEndpoint ↔ RestEndpointEntity.
 *
 * specDetails and testSuiteIds require custom handling via services.
 */
@Mapper(componentModel = "spring")
public interface RestEndpointMapper {

    /**
     * Convert entity to domain.
     * Note: specDetails JSON and testSuiteIds are not mapped - handle in service layer.
     */
    @Mapping(target = "specDetails", ignore = true)
    @Mapping(target = "testSuiteIds", ignore = true)
    RestEndpoint toDomain(RestEndpointEntity entity);

    /**
     * Convert domain to entity.
     * Note: specDetails will be null, testSuites managed by JPA.
     */
    @Mapping(target = "specDetails", ignore = true)
    @Mapping(target = "testSuites", ignore = true)
    @Mapping(target = "specSource", ignore = true)
    RestEndpointEntity toEntity(RestEndpoint domain);

    /**
     * Update existing entity from domain.
     * Note: ID, createdAt, specSource, testSuites are managed by JPA.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "specDetails", ignore = true)
    @Mapping(target = "testSuites", ignore = true)
    @Mapping(target = "specSource", ignore = true)
    @Mapping(target = "project", ignore = true)
    void updateEntityFromDomain(@MappingTarget RestEndpointEntity entity, RestEndpoint domain);
}
