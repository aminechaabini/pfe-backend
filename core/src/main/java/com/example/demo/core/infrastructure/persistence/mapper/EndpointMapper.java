package com.example.demo.core.infrastructure.persistence.mapper;

import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.RestEndpoint;
import com.example.demo.core.domain.spec.SoapEndpoint;
import com.example.demo.core.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.core.infrastructure.persistence.entity.spec.RestEndpointEntity;
import com.example.demo.core.infrastructure.persistence.entity.spec.SoapEndpointEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {})
public interface EndpointMapper {

    // Polymorphic mapping - manual dispatching to concrete methods
    default Endpoint toDomain(EndpointEntity entity) {
        if (entity == null) {
            return null;
        }
        if (entity instanceof RestEndpointEntity) {
            return toDomain((RestEndpointEntity) entity);
        } else if (entity instanceof SoapEndpointEntity) {
            return toDomain((SoapEndpointEntity) entity);
        }
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    default SoapEndpoint toDomain(SoapEndpointEntity entity){
        if (entity == null) {
            return null;
        }

        return SoapEndpoint.reconstitute(
                entity.getId(),
                entity.getServiceName(),
                entity.getOperationName(),
                entity.getVersion(),
                entity.getSoapAction(),
                entity.getSummary(),
                entity.getOperationId(),
                entity.getSpecSource() != null ? entity.getSpecSource().getId() : null,
                entity.getProject() != null ? entity.getProject().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }


    default RestEndpoint toDomain(RestEndpointEntity entity){
        if (entity == null) {
            return null;
        }

        return RestEndpoint.reconstitute(
                entity.getId(),
                entity.getMethod(),
                entity.getPath(),
                entity.getSummary(),
                entity.getOperationId(),
                entity.getSpecSource() != null ? entity.getSpecSource().getId() : null,
                entity.getProject() != null ? entity.getProject().getId() : null,
                entity.getCreatedAt(),
                entity.getUpdatedAt()

        );
    }



    // Polymorphic mapping to entity - manual dispatching to concrete methods
    default EndpointEntity toEntity(Endpoint endpoint) {
        if (endpoint == null) {
            return null;
        }
        if (endpoint instanceof RestEndpoint) {
            return toEntity((RestEndpoint) endpoint);
        } else if (endpoint instanceof SoapEndpoint) {
            return toEntity((SoapEndpoint) endpoint);
        }
        throw new IllegalArgumentException("Unknown endpoint type: " + endpoint.getClass());
    }

    @Mapping(target = "specSource", ignore = true)  // Set by repository
    @Mapping(target = "project", ignore = true)     // Set by repository
    @Mapping(target = "testSuites", ignore = true)   // Managed by JPA
    SoapEndpointEntity toEntity(SoapEndpoint endpoint);

    @Mapping(target = "specSource", ignore = true)  // Set by repository
    @Mapping(target = "project", ignore = true)     // Set by repository
    @Mapping(target = "testSuites", ignore = true)   // Managed by JPA
    RestEndpointEntity toEntity(RestEndpoint endpoint);

    // ===============================
    // UPDATE ENTITY FROM DOMAIN
    // ===============================
    // Polymorphic update - manual dispatching to concrete methods
    default void updateEntityFromDomain(@MappingTarget EndpointEntity entity, Endpoint domain) {
        if (entity == null || domain == null) {
            return;
        }
        if (entity instanceof RestEndpointEntity && domain instanceof RestEndpoint) {
            updateEntityFromDomain((RestEndpointEntity) entity, (RestEndpoint) domain);
        } else if (entity instanceof SoapEndpointEntity && domain instanceof SoapEndpoint) {
            updateEntityFromDomain((SoapEndpointEntity) entity, (SoapEndpoint) domain);
        } else {
            throw new IllegalArgumentException("Entity type " + entity.getClass() +
                " does not match domain type " + domain.getClass());
        }
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "specSource", ignore = true)  // Set by repository
    @Mapping(target = "project", ignore = true)     // Set by repository
    @Mapping(target = "testSuites", ignore = true)   // Managed by JPA
    void updateEntityFromDomain(@MappingTarget RestEndpointEntity entity, RestEndpoint domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "specSource", ignore = true)  // Set by repository
    @Mapping(target = "project", ignore = true)     // Set by repository
    @Mapping(target = "testSuites", ignore = true)   // Managed by JPA
    void updateEntityFromDomain(@MappingTarget SoapEndpointEntity entity, SoapEndpoint domain);
}
