package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.EndpointRepository;
import com.example.demo.core.domain.spec.HttpMethod;
import com.example.demo.core.domain.spec.RestEndpoint;
import com.example.demo.core.domain.spec.SoapEndpoint;
import com.example.demo.core.infrastructure.persistence.entity.spec.EndpointEntity;
import com.example.demo.core.infrastructure.persistence.mapper.EndpointMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of EndpointRepository domain interface.
 *
 * This is an adapter that translates between:
 * - Domain objects (Endpoint, RestEndpoint, SoapEndpoint)
 * - Persistence entities (EndpointEntity, RestEndpointEntity, SoapEndpointEntity)
 *
 * Uses:
 * - JPA repository for database operations
 * - Polymorphic mapper for entity ↔ domain conversions
 */
@Repository
public class EndpointRepositoryImpl implements EndpointRepository {

    private final com.example.demo.core.infrastructure.persistence.jpa.EndpointRepository jpaRepository;
    private final EndpointMapper mapper;

    public EndpointRepositoryImpl(
            com.example.demo.core.infrastructure.persistence.jpa.EndpointRepository jpaRepository,
            EndpointMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Endpoint save(Endpoint endpoint) {
        EndpointEntity entity;

        if (endpoint.getId() == null) {
            // New endpoint - create entity
            entity = mapper.toEntity(endpoint);
        } else {
            // Existing endpoint - update entity
            entity = jpaRepository.findById(endpoint.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Endpoint not found: " + endpoint.getId()));
            mapper.updateEntityFromDomain(entity, endpoint);
        }

        EndpointEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Endpoint> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Endpoint> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Endpoint> findByProjectId(Long projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Endpoint> findBySpecSourceId(Long specSourceId) {
        return jpaRepository.findBySpecSourceId(specSourceId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RestEndpoint> findRestEndpointByProject(Long projectId, HttpMethod method, String path) {
        return jpaRepository.findRestEndpointByProject(projectId, method, path)
                .map(entity -> (RestEndpoint) mapper.toDomain(entity));
    }

    @Override
    public Optional<SoapEndpoint> findSoapEndpointByProject(Long projectId, String serviceName, String operationName) {
        return jpaRepository.findSoapEndpointByProject(projectId, serviceName, operationName)
                .map(entity -> (SoapEndpoint) mapper.toDomain(entity));
    }

    @Override
    public Optional<Endpoint> findByProjectIdAndUniqueKey(Long projectId, String uniqueKey) {
        return jpaRepository.findByProjectIdAndUniqueKey(projectId, uniqueKey)
                .map(mapper::toDomain);
    }

    @Override
    public List<RestEndpoint> findRestEndpointsByProjectId(Long projectId) {
        return jpaRepository.findRestEndpointsByProjectId(projectId).stream()
                .map(entity -> (RestEndpoint) mapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public List<SoapEndpoint> findSoapEndpointsByProjectId(Long projectId) {
        return jpaRepository.findSoapEndpointsByProjectId(projectId).stream()
                .map(entity -> (SoapEndpoint) mapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public List<RestEndpoint> findRestEndpointsByMethod(Long projectId, HttpMethod method) {
        return jpaRepository.findRestEndpointsByMethod(projectId, method).stream()
                .map(entity -> (RestEndpoint) mapper.toDomain(entity))
                .collect(Collectors.toList());
    }

    @Override
    public List<Endpoint> searchByKeyword(Long projectId, String keyword) {
        return jpaRepository.searchByKeyword(projectId, keyword).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByProjectId(Long projectId) {
        return jpaRepository.countByProjectId(projectId);
    }

    @Override
    public long countBySpecSourceId(Long specSourceId) {
        return jpaRepository.countBySpecSourceId(specSourceId);
    }

    @Override
    public long countRestEndpointsByProjectId(Long projectId) {
        return jpaRepository.countRestEndpointsByProjectId(projectId);
    }

    @Override
    public long countSoapEndpointsByProjectId(Long projectId) {
        return jpaRepository.countSoapEndpointsByProjectId(projectId);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
