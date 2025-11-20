package com.example.demo.orchestrator.domain.run;

import java.util.Optional;

/**
 * Repository interface for TestCaseRun entities.
 *
 * This is a domain interface (port in hexagonal architecture).
 * Infrastructure will provide the implementation (adapter).
 */
public interface TestCaseRunRepository {

    /**
     * Save a test case run (create or update).
     *
     * @param run the test case run to save
     * @return the saved test case run with ID assigned
     */
    TestCaseRun save(TestCaseRun run);

    /**
     * Find a test case run by its ID.
     * CRUCIAL: This must include full details (request, response, assertions)
     * for failure analysis.
     *
     * @param id the run ID
     * @return Optional containing the run if found
     */
    Optional<TestCaseRun> findById(Long id);

    /**
     * Delete a test case run by ID.
     *
     * @param id the run ID
     */
    void deleteById(Long id);
}
