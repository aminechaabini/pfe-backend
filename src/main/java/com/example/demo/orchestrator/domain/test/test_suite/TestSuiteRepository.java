package com.example.demo.orchestrator.domain.test.test_suite;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TestSuite aggregate root.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 *
 * Works with domain objects only - no knowledge of persistence details.
 */
public interface TestSuiteRepository {

    /**
     * Save a test suite (create or update).
     *
     * @param testSuite the test suite to save
     * @return the saved test suite with ID assigned
     */
    TestSuite save(TestSuite testSuite);

    /**
     * Find a test suite by its ID.
     *
     * @param id the test suite ID
     * @return Optional containing the test suite if found
     */
    Optional<TestSuite> findById(Long id);

    /**
     * Find a test suite by its exact name.
     *
     * @param name the test suite name
     * @return Optional containing the test suite if found
     */
    Optional<TestSuite> findByName(String name);

    /**
     * Find all test suites.
     *
     * @return list of all test suites
     */
    List<TestSuite> findAll();

    /**
     * Find all test suites ordered by creation date (newest first).
     *
     * @return list of test suites ordered by creation date descending
     */
    List<TestSuite> findAllOrderByCreatedAtDesc();

    /**
     * Find all test suites that belong to a specific project.
     *
     * @param projectId the project ID
     * @return list of test suites in the project
     */
    List<TestSuite> findByProjectId(Long projectId);

    /**
     * Search test suites by name or description (case-insensitive).
     *
     * @param searchTerm the search term
     * @return list of matching test suites
     */
    List<TestSuite> search(String searchTerm);

    /**
     * Check if a test suite with the given name already exists.
     *
     * @param name the test suite name to check
     * @return true if a test suite with this name exists
     */
    boolean existsByName(String name);

    /**
     * Delete a test suite by ID.
     *
     * @param id the test suite ID
     */
    void deleteById(Long id);

    /**
     * Count total number of test suites.
     *
     * @return total count of test suites
     */
    long count();

    /**
     * Find a test suite by ID and eagerly load its test cases.
     *
     * @param id the test suite ID
     * @return Optional containing the test suite with test cases loaded
     */
    Optional<TestSuite> findByIdWithTestCases(Long id);

    /**
     * Find a test suite by ID and eagerly load its endpoint.
     *
     * @param id the test suite ID
     * @return Optional containing the test suite with endpoint loaded
     */
    Optional<TestSuite> findByIdWithEndpoint(Long id);

    /**
     * Find all test suites associated with a specific endpoint.
     *
     * @param endpointId the endpoint ID
     * @return list of test suites testing this endpoint
     */
    List<TestSuite> findByEndpointId(Long endpointId);

    /**
     * Find the test suite that contains a specific test case.
     *
     * @param testCaseId the test case ID
     * @return Optional containing the test suite if found
     */
    Optional<TestSuite> findByTestCaseId(Long testCaseId);

    /**
     * Count the number of test cases in a test suite.
     *
     * @param testSuiteId the test suite ID
     * @return number of test cases
     */
    long countTestCasesInSuite(Long testSuiteId);
}
