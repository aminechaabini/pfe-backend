package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.project.Project;
import com.example.demo.core.domain.project.ProjectRepository;
import com.example.demo.core.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.core.infrastructure.persistence.mapper.ProjectMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ProjectRepository domain interface.
 *
 * This is an adapter that translates between:
 * - Domain objects (Project)
 * - Persistence entities (ProjectEntity)
 *
 * Uses:
 * - JPA repository for database operations
 * - MapStruct mapper for entity ↔ domain conversions
 */
@Repository
public class ProjectRepositoryAdapter implements ProjectRepository {

    private final com.example.demo.core.infrastructure.persistence.jpa.ProjectRepository jpaRepository;
    private final ProjectMapper mapper;

    public ProjectRepositoryAdapter(
            com.example.demo.core.infrastructure.persistence.jpa.ProjectRepository jpaRepository,
             ProjectMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Project save(Project project) {
        ProjectEntity entity;

        if (project.getId() == null) {
            // New project - create entity
            entity = mapper.toEntity(project);
        } else {
            // Existing project - update entity
            entity = jpaRepository.findById(project.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Project not found: " + project.getId()));
            mapper.updateEntityFromDomain(entity, project);
        }

        ProjectEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Project> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Project> search(String searchTerm) {
        return jpaRepository.search(searchTerm).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<Project> findByIdWithTestSuites(Long id) {
        return jpaRepository.findByIdWithTestSuites(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByIdWithSpecSources(Long id) {
        return jpaRepository.findByIdWithSpecSources(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByIdWithEndpoints(Long id) {
        return jpaRepository.findByIdWithEndpoints(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByTestSuiteId(Long testSuiteId) {
        return jpaRepository.findByTestSuiteId(testSuiteId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findBySpecSourceId(Long specSourceId) {
        return jpaRepository.findBySpecSourceId(specSourceId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Project> findByEndpointId(Long endpointId) {
        return jpaRepository.findByEndpointId(endpointId)
                .map(mapper::toDomain);
    }
}
