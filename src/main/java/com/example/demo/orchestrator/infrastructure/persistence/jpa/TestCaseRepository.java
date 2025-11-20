package com.example.demo.orchestrator.infrastructure.persistence.jpa;

import com.example.demo.orchestrator.infrastructure.persistence.entity.test.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Query repository for TestCaseEntity - provides cross-cutting queries.
 * This is primarily a read-only repository for searching and filtering test cases.
 *
 * Note: Test case lifecycle is managed through TestSuiteRepository.
 * This repository provides queries that span multiple test suites.
 *
 * Key Responsibilities:
 * - Search test cases across all test suites
 * - Filter test cases by type (REST, SOAP, E2E)
 * - Analytics on test definitions
 * - Find tests by characteristics
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCaseEntity, Long> {

    /**
     * Finds all test cases in a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return list of test cases in the suite
     */
    @Query("SELECT tc FROM TestCaseEntity tc WHERE tc.testSuiteId = :testSuiteId")
    List<TestCaseEntity> findByTestSuiteId(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds all test cases of a specific type using discriminator.
     * Use TYPE() function to filter by entity class.
     *
     * @param type the entity class (e.g., RestApiTestEntity.class)
     * @return list of test cases of this type
     */
    @Query("SELECT tc FROM TestCaseEntity tc WHERE TYPE(tc) = :type")
    List<TestCaseEntity> findByType(@Param("type") Class<? extends TestCaseEntity> type);

    /**
     * Finds all REST API tests.
     * Convenience method that filters by discriminator value.
     *
     * @return list of REST API tests
     */
    @Query("SELECT tc FROM RestApiTestEntity tc")
    List<RestApiTestEntity> findAllRestApiTests();

    /**
     * Finds all SOAP API tests.
     * Convenience method that filters by discriminator value.
     *
     * @return list of SOAP API tests
     */
    @Query("SELECT tc FROM SoapApiTestEntity tc")
    List<SoapApiTestEntity> findAllSoapApiTests();

    /**
     * Finds all E2E tests.
     * Convenience method that filters by discriminator value.
     *
     * @return list of E2E tests
     */
    @Query("SELECT tc FROM E2eTestEntity tc")
    List<E2eTestEntity> findAllE2eTests();

    /**
     * Counts test cases by type.
     *
     * @param type the entity class
     * @return count of test cases of this type
     */
    @Query("SELECT COUNT(tc) FROM TestCaseEntity tc WHERE TYPE(tc) = :type")
    long countByType(@Param("type") Class<? extends TestCaseEntity> type);

    /**
     * Counts total test cases across all test suites.
     *
     * @return total count of test cases
     */
    @Query("SELECT COUNT(tc) FROM TestCaseEntity tc")
    long countAllTestCases();

    /**
     * Searches test cases by name (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching test cases
     */
    @Query("SELECT tc FROM TestCaseEntity tc WHERE " +
           "LOWER(tc.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TestCaseEntity> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Searches test cases by name or description (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching test cases
     */
    @Query("SELECT tc FROM TestCaseEntity tc WHERE " +
           "LOWER(tc.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(tc.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TestCaseEntity> search(@Param("searchTerm") String searchTerm);

    /**
     * Finds test cases ordered by creation date (newest first).
     *
     * @return list of test cases ordered by creation date
     */
    @Query("SELECT tc FROM TestCaseEntity tc ORDER BY tc.createdAt DESC")
    List<TestCaseEntity> findAllOrderByCreatedAtDesc();

    /**
     * Counts test cases in a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return count of test cases
     */
    @Query("SELECT COUNT(tc) FROM TestCaseEntity tc WHERE tc.testSuiteId = :testSuiteId")
    long countByTestSuiteId(@Param("testSuiteId") Long testSuiteId);

    /**
     * Gets distribution of test types.
     * Returns counts grouped by test type (REST_API, SOAP_API, E2E).
     *
     * @return list of [TestType, Count] pairs
     */
    @Query(value = "SELECT test_type, COUNT(*) FROM test_cases GROUP BY test_type", nativeQuery = true)
    List<Object[]> getTestTypeDistribution();

    /**
     * Finds REST API tests in a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return list of REST API tests
     */
    @Query("SELECT tc FROM RestApiTestEntity tc WHERE tc.testSuiteId = :testSuiteId")
    List<RestApiTestEntity> findRestApiTestsByTestSuiteId(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds E2E tests in a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return list of E2E tests
     */
    @Query("SELECT tc FROM E2eTestEntity tc WHERE tc.testSuiteId = :testSuiteId")
    List<E2eTestEntity> findE2eTestsByTestSuiteId(@Param("testSuiteId") Long testSuiteId);
}
