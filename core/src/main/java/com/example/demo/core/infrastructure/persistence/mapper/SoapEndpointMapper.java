package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.spec.SoapEndpoint;
import com.example.demo.core.infrastructure.persistence.entity.spec.SoapEndpointEntity;
import org.mapstruct.*;

/**
 * MapStruct mapper for SoapEndpoint ↔ SoapEndpointEntity.
 *
 * specDetails and testSuiteIds require custom handling via services.
 */
@Mapper(componentModel = "spring")
public interface SoapEndpointMapper {

    /**
     * Convert entity to domain.
     * Note: specDetails JSON and testSuiteIds are not mapped - handle in service layer.
     */
    @Mapping(target = "specDetails", ignore = true)
    @Mapping(target = "testSuiteIds", ignore = true)
    SoapEndpoint toDomain(SoapEndpointEntity entity);

    /**
     * Convert domain to entity.
     * Note: specDetails will be null, testSuites managed by JPA.
     */
    @Mapping(target = "specDetails", ignore = true)
    @Mapping(target = "testSuites", ignore = true)
    @Mapping(target = "specSource", ignore = true)
    SoapEndpointEntity toEntity(SoapEndpoint domain);

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
    void updateEntityFromDomain(@MappingTarget SoapEndpointEntity entity, SoapEndpoint domain);
}
