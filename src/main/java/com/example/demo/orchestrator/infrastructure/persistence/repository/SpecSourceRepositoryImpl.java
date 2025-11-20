package com.example.demo.orchestrator.infrastructure.persistence.repository;

import com.example.demo.orchestrator.domain.spec.SpecSource;
import com.example.demo.orchestrator.domain.spec.SpecSourceRepository;
import com.example.demo.orchestrator.domain.spec.SpecType;
import com.example.demo.orchestrator.infrastructure.persistence.entity.spec.SpecSourceEntity;
import com.example.demo.orchestrator.infrastructure.persistence.mapper.SpecSourceMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of SpecSourceRepository domain interface.
 *
 * This is an adapter that translates between:
 * - Domain objects (SpecSource)
 * - Persistence entities (SpecSourceEntity)
 *
 * Uses:
 * - JPA repository for database operations
 * - MapStruct mapper for entity ↔ domain conversions
 */
@Repository
public class SpecSourceRepositoryImpl implements SpecSourceRepository {

    private final com.example.demo.orchestrator.infrastructure.persistence.jpa.SpecSourceRepository jpaRepository;
    private final SpecSourceMapper mapper;

    public SpecSourceRepositoryImpl(
            com.example.demo.orchestrator.infrastructure.persistence.jpa.SpecSourceRepository jpaRepository,
            SpecSourceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public SpecSource save(SpecSource specSource) {
        SpecSourceEntity entity;

        if (specSource.getId() == null) {
            // New spec source - create entity
            entity = mapper.toEntity(specSource);
        } else {
            // Existing spec source - update entity
            entity = jpaRepository.findById(specSource.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Spec source not found: " + specSource.getId()));
            mapper.updateEntityFromDomain(entity, specSource);
        }

        SpecSourceEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SpecSource> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<SpecSource> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecSource> findByProjectId(Long projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SpecSource> findByProjectIdAndName(Long projectId, String name) {
        return jpaRepository.findByProjectIdAndName(projectId, name)
                .map(mapper::toDomain);
    }

    @Override
    public boolean existsByProjectIdAndName(Long projectId, String name) {
        return jpaRepository.existsByProjectIdAndName(projectId, name);
    }

    @Override
    public List<SpecSource> findBySpecType(SpecType specType) {
        return jpaRepository.findBySpecType(specType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecSource> findByProjectIdAndSpecType(Long projectId, SpecType specType) {
        return jpaRepository.findByProjectIdAndSpecType(projectId, specType).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecSource> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecSource> findByProjectIdOrderByCreatedAtDesc(Long projectId) {
        return jpaRepository.findByProjectIdOrderByCreatedAtDesc(projectId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecSource> search(String keyword) {
        return jpaRepository.search(keyword).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<SpecSource> findByIdWithEndpoints(Long id) {
        return jpaRepository.findByIdWithEndpoints(id)
                .map(mapper::toDomain);
    }

    @Override
    public long countByProjectId(Long projectId) {
        return jpaRepository.countByProjectId(projectId);
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
