package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.domain.test.test_suite.TestSuiteRepository;
import com.example.demo.core.infrastructure.persistence.entity.test.TestSuiteEntity;
import com.example.demo.core.infrastructure.persistence.mapper.TestSuiteMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of TestSuiteRepository domain interface.
 *
 * This is an adapter that translates between:
 * - Domain objects (TestSuite)
 * - Persistence entities (TestSuiteEntity)
 *
 * Uses:
 * - JPA repository for database operations
 * - MapStruct mapper for entity ↔ domain conversions
 */
@Repository
public class TestSuiteRepositoryImpl implements TestSuiteRepository {

    private final com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRepository jpaRepository;
    private final TestSuiteMapper mapper;

    public TestSuiteRepositoryImpl(
            com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRepository jpaRepository,
            TestSuiteMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public TestSuite save(TestSuite testSuite) {
        TestSuiteEntity entity;

        if (testSuite.getId() == null) {
            // New test suite - create entity
            entity = mapper.toEntity(testSuite);
        } else {
            // Existing test suite - update entity
            entity = jpaRepository.findById(testSuite.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuite.getId()));
            mapper.updateEntityFromDomain(entity, testSuite);
        }

        TestSuiteEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<TestSuite> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<TestSuite> findByName(String name) {
        return jpaRepository.findByName(name)
                .map(mapper::toDomain);
    }

    @Override
    public List<TestSuite> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestSuite> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestSuite> findByProjectId(Long projectId) {
        return jpaRepository.findByProjectId(projectId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<TestSuite> search(String searchTerm) {
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
    public Optional<TestSuite> findByIdWithTestCases(Long id) {
        return jpaRepository.findByIdWithTestCases(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<TestSuite> findByIdWithEndpoint(Long id) {
        return jpaRepository.findByIdWithEndpoint(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<TestSuite> findByEndpointId(Long endpointId) {
        return jpaRepository.findByEndpointId(endpointId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TestSuite> findByTestCaseId(Long testCaseId) {
        return jpaRepository.findByTestCaseId(testCaseId)
                .map(mapper::toDomain);
    }

    @Override
    public long countTestCasesInSuite(Long testSuiteId) {
        return jpaRepository.countTestCasesInSuite(testSuiteId);
    }
}
