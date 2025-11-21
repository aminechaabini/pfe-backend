package com.example.demo.core.domain.run;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TestSuiteRun aggregate root.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 */
public interface TestSuiteRunRepository {

    /**
     * Save a test suite run (create or update).
     *
     * @param run the test suite run to save
     * @return the saved test suite run with ID assigned
     */
    TestSuiteRun save(TestSuiteRun run);

    /**
     * Find a test suite run by its ID.
     *
     * @param id the run ID
     * @return Optional containing the run if found
     */
    Optional<TestSuiteRun> findById(Long id);

    /**
     * Find all runs for a specific test suite, ordered by start time desc.
     *
     * @param testSuiteId the test suite ID
     * @param limit maximum number of runs to return
     * @return list of test suite runs
     */
    List<TestSuiteRun> findByTestSuiteIdOrderByStartTimeDesc(Long testSuiteId, int limit);

    /**
     * Find all runs for a project, ordered by start time desc.
     *
     * @param projectId the project ID
     * @param limit maximum number of runs to return
     * @return list of test suite runs
     */
    List<TestSuiteRun> findByProjectIdOrderByStartTimeDesc(Long projectId, int limit);

    /**
     * Delete a test suite run by ID.
     *
     * @param id the run ID
     */
    void deleteById(Long id);
}
