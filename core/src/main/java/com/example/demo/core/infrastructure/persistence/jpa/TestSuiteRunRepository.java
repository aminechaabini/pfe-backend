package com.example.demo.core.infrastructure.persistence.jpa;

import com.example.demo.core.domain.run.RunResult;
import com.example.demo.core.domain.run.RunStatus;
import com.example.demo.core.infrastructure.persistence.entity.run.TestSuiteRunEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository for TestSuiteRunEntity - manages test execution history.
 * Test suite runs are aggregate roots that record execution results.
 *
 * Key Responsibilities:
 * - CRUD operations for test runs
 * - Query execution history by test suite
 * - Filter runs by status and result
 * - Generate execution statistics and metrics
 * - Clean up old execution history
 */
@Repository
public interface TestSuiteRunRepository extends JpaRepository<TestSuiteRunEntity, Long> {

    /**
     * Finds all runs for a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return list of all runs for this test suite
     */
    @Query("SELECT r FROM TestSuiteRunEntity r WHERE r.testSuite.id = :testSuiteId ORDER BY r.createdAt DESC")
    List<TestSuiteRunEntity> findByTestSuiteId(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds the most recent runs for a specific test suite.
     * Use Pageable to limit the number of results.
     *
     * Example: findLatestRunsByTestSuite(testSuiteId, PageRequest.of(0, 10))
     *
     * @param testSuiteId the test suite ID
     * @param pageable pagination information
     * @return list of recent runs (paginated)
     */
    @Query("SELECT r FROM TestSuiteRunEntity r WHERE r.testSuite.id = :testSuiteId ORDER BY r.createdAt DESC")
    List<TestSuiteRunEntity> findLatestRunsByTestSuite(@Param("testSuiteId") Long testSuiteId, Pageable pageable);

    /**
     * Finds the most recent run for a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return Optional containing the most recent run if exists
     */
    @Query("SELECT r FROM TestSuiteRunEntity r WHERE r.testSuite.id = :testSuiteId ORDER BY r.createdAt DESC")
    Optional<TestSuiteRunEntity> findLatestRunByTestSuite(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds all runs with a specific status.
     * Useful for finding currently running or pending executions.
     *
     * @param status the run status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     * @return list of runs with this status
     */
    List<TestSuiteRunEntity> findByStatus(RunStatus status);

    /**
     * Finds all runs with a specific result.
     *
     * @param result the run result (SUCCESS, FAILURE, CANCELLED)
     * @return list of runs with this result
     */
    List<TestSuiteRunEntity> findByResult(RunResult result);

    /**
     * Finds runs with a specific result created after a certain date.
     * Useful for finding recent failures.
     *
     * @param result the run result
     * @param since the start date
     * @return list of matching runs
     */
    List<TestSuiteRunEntity> findByResultAndCreatedAtAfter(RunResult result, Instant since);

    /**
     * Finds failed runs for a specific test suite after a certain date.
     *
     * @param testSuiteId the test suite ID
     * @param result the run result
     * @param since the start date
     * @return list of failed runs
     */
    @Query("SELECT r FROM TestSuiteRunEntity r WHERE r.testSuite.id = :testSuiteId " +
           "AND r.result = :result AND r.createdAt > :since ORDER BY r.createdAt DESC")
    List<TestSuiteRunEntity> findByTestSuiteAndResultAfter(
            @Param("testSuiteId") Long testSuiteId,
            @Param("result") RunResult result,
            @Param("since") Instant since);

    /**
     * Gets aggregate statistics for a test suite's runs.
     * Returns counts grouped by result (SUCCESS, FAILURE, CANCELLED).
     *
     * @param testSuiteId the test suite ID
     * @return list of [RunResult, Count] pairs
     */
    @Query("SELECT r.result, COUNT(r) FROM TestSuiteRunEntity r " +
           "WHERE r.testSuite.id = :testSuiteId AND r.result IS NOT NULL " +
           "GROUP BY r.result")
    List<Object[]> getRunStatistics(@Param("testSuiteId") Long testSuiteId);

    /**
     * Calculates the success rate for a test suite.
     * Returns percentage of successful runs.
     *
     * @param testSuiteId the test suite ID
     * @return success rate as a decimal (0.0 to 1.0), null if no completed runs
     */
    @Query("SELECT CAST(SUM(CASE WHEN r.result = 'SUCCESS' THEN 1 ELSE 0 END) AS double) / COUNT(r) " +
           "FROM TestSuiteRunEntity r " +
           "WHERE r.testSuite.id = :testSuiteId AND r.result IS NOT NULL")
    Double getSuccessRate(@Param("testSuiteId") Long testSuiteId);

    /**
     * Finds all runs created within a specific time range.
     *
     * @param startDate the start of the range
     * @param endDate the end of the range
     * @return list of runs in this time range
     */
    @Query("SELECT r FROM TestSuiteRunEntity r " +
           "WHERE r.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY r.createdAt DESC")
    List<TestSuiteRunEntity> findByCreatedAtBetween(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);

    /**
     * Counts completed runs for a specific test suite.
     *
     * @param testSuiteId the test suite ID
     * @return count of completed runs
     */
    @Query("SELECT COUNT(r) FROM TestSuiteRunEntity r " +
           "WHERE r.testSuite.id = :testSuiteId AND r.status = 'COMPLETED'")
    long countCompletedRuns(@Param("testSuiteId") Long testSuiteId);

    /**
     * Deletes runs older than a specified date.
     * Useful for cleaning up old execution history.
     *
     * @param beforeDate the cutoff date
     * @return number of deleted runs
     */
    int deleteByCreatedAtBefore(Instant beforeDate);

    /**
     * Finds runs that are stuck in IN_PROGRESS status for too long.
     * Useful for finding and cleaning up zombie runs.
     *
     * @param status the status to check
     * @param threshold the time threshold
     * @return list of stuck runs
     */
    @Query("SELECT r FROM TestSuiteRunEntity r " +
           "WHERE r.status = :status AND r.startedAt < :threshold")
    List<TestSuiteRunEntity> findStuckRuns(
            @Param("status") RunStatus status,
            @Param("threshold") Instant threshold);

    /**
     * Gets average execution duration for a test suite.
     * Returns average time in milliseconds.
     *
     * @param testSuiteId the test suite ID
     * @return average duration in milliseconds, null if no completed runs
     */
    @Query("SELECT AVG(r.completedAt - r.startedAt) FROM TestSuiteRunEntity r " +
           "WHERE r.testSuite.id = :testSuiteId " +
           "AND r.status = 'COMPLETED' " +
           "AND r.startedAt IS NOT NULL " +
           "AND r.completedAt IS NOT NULL")
    Double getAverageDuration(@Param("testSuiteId") Long testSuiteId);
}
