package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.run.TestSuiteRun;
import com.example.demo.core.domain.run.TestSuiteRunRepository;
import com.example.demo.core.infrastructure.persistence.entity.run.TestSuiteRunEntity;
import com.example.demo.core.infrastructure.persistence.mapper.TestSuiteRunMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TestSuiteRunRepository domain interface.
 *
 * Adapter that translates between domain objects and persistence entities.
 */
@Repository
public class TestSuiteRunRepositoryAdapter implements TestSuiteRunRepository {

    private final com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRunRepository jpaRepository;
    private final TestSuiteRunMapper mapper;

    public TestSuiteRunRepositoryAdapter(
            com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRunRepository jpaRepository,
            TestSuiteRunMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TestSuiteRun save(TestSuiteRun run) {
        TestSuiteRunEntity entity;

        if (run.getId() == null) {
            // New run - create entity
            entity = mapper.toEntity(run);
        } else {
            // Existing run - update entity
            entity = jpaRepository.findById(run.getId())
                    .orElseThrow(() -> new IllegalArgumentException("TestSuiteRun not found: " + run.getId()));
            mapper.updateEntityFromDomain(entity, run);
        }

        TestSuiteRunEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TestSuiteRun> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<TestSuiteRun> findByTestSuiteIdOrderByStartTimeDesc(Long testSuiteId, int limit) {
        return jpaRepository.findLatestRunsByTestSuite(testSuiteId, PageRequest.of(0, limit))
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestSuiteRun> findByProjectIdOrderByStartTimeDesc(Long projectId, int limit) {
        // Note: This requires a query that joins through TestSuite to Project
        // For now, returning empty list as the JPA repository doesn't have this query yet
        // TODO: Add query to JPA repository
        return List.of();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
