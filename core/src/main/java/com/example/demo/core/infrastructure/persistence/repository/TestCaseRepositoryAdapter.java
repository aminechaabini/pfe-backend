package com.example.demo.core.infrastructure.persistence.repository;

import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.api.SoapApiTest;
import com.example.demo.core.domain.test.e2e.E2eTest;
import com.example.demo.core.infrastructure.persistence.entity.test.*;
import com.example.demo.core.infrastructure.persistence.mapper.TestCaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter for TestCase queries and analytics.
 *
 * This is a read-focused repository for searching and filtering test cases
 * across test suites. The primary lifecycle management happens through
 * TestSuiteRepository.
 *
 * Provides:
 * - Cross-suite test case searches
 * - Filtering by test type
 * - Analytics on test definitions
 */
@Repository
public class TestCaseRepositoryAdapter {

    private final com.example.demo.core.infrastructure.persistence.jpa.TestCaseRepository jpaRepository;
    private final TestCaseMapper mapper;

    public TestCaseRepositoryAdapter(
            com.example.demo.core.infrastructure.persistence.jpa.TestCaseRepository jpaRepository,
            TestCaseMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    /**
     * Find test case by ID.
     */
    public Optional<TestCase> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    /**
     * Find all test cases in a test suite.
     */
    public List<TestCase> findByTestSuiteId(Long testSuiteId) {
        return jpaRepository.findByTestSuiteId(testSuiteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find all REST API tests.
     */
    public List<RestApiTest> findAllRestApiTests() {
        return jpaRepository.findAllRestApiTests().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find all SOAP API tests.
     */
    public List<SoapApiTest> findAllSoapApiTests() {
        return jpaRepository.findAllSoapApiTests().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find all E2E tests.
     */
    public List<E2eTest> findAllE2eTests() {
        return jpaRepository.findAllE2eTests().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Search test cases by name or description.
     */
    public List<TestCase> search(String searchTerm) {
        return jpaRepository.search(searchTerm).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Search test cases by name only.
     */
    public List<TestCase> searchByName(String searchTerm) {
        return jpaRepository.searchByName(searchTerm).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find all test cases ordered by creation date (newest first).
     */
    public List<TestCase> findAllOrderByCreatedAtDesc() {
        return jpaRepository.findAllOrderByCreatedAtDesc().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find REST API tests in a specific test suite.
     */
    public List<RestApiTest> findRestApiTestsByTestSuiteId(Long testSuiteId) {
        return jpaRepository.findRestApiTestsByTestSuiteId(testSuiteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Find E2E tests in a specific test suite.
     */
    public List<E2eTest> findE2eTestsByTestSuiteId(Long testSuiteId) {
        return jpaRepository.findE2eTestsByTestSuiteId(testSuiteId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Count all test cases.
     */
    public long countAllTestCases() {
        return jpaRepository.countAllTestCases();
    }

    /**
     * Count test cases in a test suite.
     */
    public long countByTestSuiteId(Long testSuiteId) {
        return jpaRepository.countByTestSuiteId(testSuiteId);
    }

    /**
     * Count test cases by type.
     */
    public long countByType(Class<? extends TestCaseEntity> type) {
        return jpaRepository.countByType(type);
    }

    /**
     * Get test type distribution.
     * Returns list of [TestType, Count] pairs.
     */
    public List<Object[]> getTestTypeDistribution() {
        return jpaRepository.getTestTypeDistribution();
    }

    /**
     * Delete a test case by ID.
     */
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    /**
     * Save a test case.
     * Note: This is primarily for updates, creation typically happens through TestSuiteRepository.
     */
    public TestCase save(TestCase testCase) {
        TestCaseEntity entity;

        if (testCase.getId() == null) {
            // New test case
            entity = mapper.toEntity(testCase);
        } else {
            // Update existing
            entity = jpaRepository.findById(testCase.getId())
                    .orElseThrow(() -> new IllegalArgumentException("TestCase not found: " + testCase.getId()));
            mapper.updateEntityFromDomain(entity, testCase);
        }

        TestCaseEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
