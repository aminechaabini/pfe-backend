package com.example.demo.orchestrator.infrastructure.persistence.jpa;

import com.example.demo.orchestrator.infrastructure.persistence.entity.test.TestSuiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TestSuiteEntity - manages test suite persistence operations.
 * Test suites are aggregate roots that group related test cases.
 *
 * Key Responsibilities:
 * - CRUD operations for test suites
 * - Query test suites by name, project, and endpoint
 * - Load test suites with test cases and endpoint
 */
@Repository
public interface TestSuiteRepository extends JpaRepository<TestSuiteEntity, Long> {

    /**
     * Finds a test suite by its exact name.
     *
     * @param name the test suite name
     * @return Optional containing the test suite if found
     */
    Optional<TestSuiteEntity> findByName(String name);

    /**
     * Checks if a test suite with the given name already exists.
     *
     * @param name the test suite name to check
     * @return true if a test suite with this name exists
     */
    boolean existsByName(String name);

    /**
     * Finds a test suite by ID and eagerly fetches its test cases.
     * Uses JOIN FETCH to avoid N+1 query problem.
     *
     * @param id the test suite ID
     * @return Optional containing the test suite with test cases loaded
     */
    @Query("SELECT ts FROM TestSuiteEntity ts LEFT JOIN FETCH ts.testCases WHERE ts.id = :id")
    Optional<TestSuiteEntity> findByIdWithTestCases(@Param("id") Long id);

    /**
     * Finds all test suites that belong to a specific project.
     *
     * @param projectId the project ID
     * @return list of test suites in the project
     */
    List<TestSuiteEntity> findByProjectId(Long projectId);

    /**
     * Finds all test suites ordered by creation date (newest first).
     *
     * @return list of test suites ordered by creation date descending
     */
    @Query("SELECT ts FROM TestSuiteEntity ts ORDER BY ts.createdAt DESC")
    List<TestSuiteEntity> findAllOrderByCreatedAtDesc();

    /**
     * Searches test suites by name or description (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching test suites
     */
    @Query("SELECT ts FROM TestSuiteEntity ts WHERE " +
           "LOWER(ts.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(ts.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<TestSuiteEntity> search(@Param("searchTerm") String searchTerm);

    /**
     * Counts the total number of test suites in the system.
     *
     * @return total count of test suites
     */
    @Query("SELECT COUNT(ts) FROM TestSuiteEntity ts")
    long countTestSuites();

    /**
     * Finds all test suites associated with a specific endpoint.
     *
     * @param endpointId the endpoint ID
     * @return list of test suites testing this endpoint
     */
    List<TestSuiteEntity> findByEndpointId(Long endpointId);

    /**
     * Finds a test suite by ID and eagerly fetches its endpoint.
     *
     * @param id the test suite ID
     * @return Optional containing the test suite with endpoint loaded
     */
    @Query("SELECT ts FROM TestSuiteEntity ts LEFT JOIN FETCH ts.endpoint WHERE ts.id = :id")
    Optional<TestSuiteEntity> findByIdWithEndpoint(@Param("id") Long id);

    /**
     * Finds test suites that contain a specific test case.
     *
     * @param testCaseId the test case ID
     * @return Optional containing the test suite if found
     */
    @Query("SELECT ts FROM TestSuiteEntity ts JOIN ts.testCases tc WHERE tc.id = :testCaseId")
    Optional<TestSuiteEntity> findByTestCaseId(@Param("testCaseId") Long testCaseId);

    /**
     * Counts the number of test cases in a test suite.
     *
     * @param testSuiteId the test suite ID
     * @return number of test cases
     */
    @Query("SELECT COUNT(tc) FROM TestSuiteEntity ts JOIN ts.testCases tc WHERE ts.id = :testSuiteId")
    long countTestCasesInSuite(@Param("testSuiteId") Long testSuiteId);
}
