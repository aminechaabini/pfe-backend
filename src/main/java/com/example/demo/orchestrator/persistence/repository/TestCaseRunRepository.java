package com.example.demo.orchestrator.persistence.repository;

import com.example.demo.orchestrator.domain.run.RunResult;
import com.example.demo.orchestrator.domain.run.RunStatus;
import com.example.demo.orchestrator.persistence.entity.run.ApiTestRunEntity;
import com.example.demo.orchestrator.persistence.entity.run.E2eTestRunEntity;
import com.example.demo.orchestrator.persistence.entity.run.TestCaseRunEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Query repository for TestCaseRunEntity - provides analytics and cross-suite queries.
 * This is primarily used for reporting and analytics across test executions.
 *
 * Note: Test case runs are created through TestSuiteRunRepository.
 * This repository provides analytical queries spanning multiple test suites.
 *
 * Key Responsibilities:
 * - Track execution history for specific test cases
 * - Calculate success rates and performance metrics
 * - Find failing tests and patterns
 * - Generate analytics for reporting
 */
@Repository
public interface TestCaseRunRepository extends JpaRepository<TestCaseRunEntity, Long> {

    /**
     * Finds all execution runs for a specific test case.
     * Useful for tracking historical performance of a single test.
     *
     * @param testCaseId the test case ID
     * @return list of all runs for this test case
     */
    @Query("SELECT r FROM TestCaseRunEntity r WHERE r.testCaseId = :testCaseId ORDER BY r.createdAt DESC")
    List<TestCaseRunEntity> findByTestCaseId(@Param("testCaseId") Long testCaseId);

    /**
     * Finds recent execution runs for a specific test case.
     *
     * @param testCaseId the test case ID
     * @param pageable pagination information
     * @return list of recent runs (paginated)
     */
    @Query("SELECT r FROM TestCaseRunEntity r WHERE r.testCaseId = :testCaseId ORDER BY r.createdAt DESC")
    List<TestCaseRunEntity> findRecentByTestCaseId(@Param("testCaseId") Long testCaseId, Pageable pageable);

    /**
     * Finds all runs with a specific result.
     *
     * @param result the run result (SUCCESS, FAILURE, CANCELLED)
     * @return list of runs with this result
     */
    List<TestCaseRunEntity> findByResult(RunResult result);

    /**
     * Finds failed test runs after a specific date.
     * Useful for finding recent failures.
     *
     * @param result the run result
     * @param since the start date
     * @return list of failed runs
     */
    List<TestCaseRunEntity> findByResultAndCreatedAtAfter(RunResult result, Instant since);

    /**
     * Finds all runs by type (API or E2E).
     *
     * @param type the entity class
     * @return list of runs of this type
     */
    @Query("SELECT r FROM TestCaseRunEntity r WHERE TYPE(r) = :type")
    List<TestCaseRunEntity> findByType(@Param("type") Class<? extends TestCaseRunEntity> type);

    /**
     * Finds all API test runs.
     *
     * @return list of API test runs
     */
    @Query("SELECT r FROM ApiTestRunEntity r")
    List<ApiTestRunEntity> findAllApiTestRuns();

    /**
     * Finds all E2E test runs.
     *
     * @return list of E2E test runs
     */
    @Query("SELECT r FROM E2eTestRunEntity r")
    List<E2eTestRunEntity> findAllE2eTestRuns();

    /**
     * Calculates the success rate for a specific test case.
     * Returns the percentage of successful runs.
     *
     * @param testCaseId the test case ID
     * @return success rate as a decimal (0.0 to 1.0), null if no completed runs
     */
    @Query("SELECT CAST(SUM(CASE WHEN r.result = 'SUCCESS' THEN 1 ELSE 0 END) AS double) / COUNT(r) " +
           "FROM TestCaseRunEntity r " +
           "WHERE r.testCaseId = :testCaseId AND r.result IS NOT NULL")
    Double getSuccessRate(@Param("testCaseId") Long testCaseId);

    /**
     * Finds the slowest test cases by average duration.
     * Useful for identifying performance bottlenecks.
     *
     * @param pageable pagination (e.g., top 10)
     * @return list of [TestCaseId, TestCaseName, AvgDuration] tuples
     */
    @Query("SELECT r.testCaseId, r.testCaseName, " +
           "AVG(EXTRACT(EPOCH FROM (r.completedAt - r.startedAt)) * 1000) " +
           "FROM TestCaseRunEntity r " +
           "WHERE r.status = 'COMPLETED' " +
           "AND r.startedAt IS NOT NULL " +
           "AND r.completedAt IS NOT NULL " +
           "GROUP BY r.testCaseId, r.testCaseName " +
           "ORDER BY AVG(EXTRACT(EPOCH FROM (r.completedAt - r.startedAt))) DESC")
    List<Object[]> findSlowestTests(Pageable pageable);

    /**
     * Finds tests that fail most frequently.
     *
     * @param pageable pagination (e.g., top 10)
     * @return list of [TestCaseId, TestCaseName, FailureCount] tuples
     */
    @Query("SELECT r.testCaseId, r.testCaseName, COUNT(r) " +
           "FROM TestCaseRunEntity r " +
           "WHERE r.result = 'FAILURE' " +
           "GROUP BY r.testCaseId, r.testCaseName " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findMostFailingTests(Pageable pageable);

    /**
     * Gets run statistics by result across all test cases.
     *
     * @return list of [RunResult, Count] pairs
     */
    @Query("SELECT r.result, COUNT(r) FROM TestCaseRunEntity r " +
           "WHERE r.result IS NOT NULL " +
           "GROUP BY r.result")
    List<Object[]> getOverallRunStatistics();

    /**
     * Counts completed runs for a specific test case.
     *
     * @param testCaseId the test case ID
     * @return count of completed runs
     */
    @Query("SELECT COUNT(r) FROM TestCaseRunEntity r " +
           "WHERE r.testCaseId = :testCaseId AND r.status = 'COMPLETED'")
    long countCompletedRuns(@Param("testCaseId") Long testCaseId);

    /**
     * Counts successful runs for a specific test case.
     *
     * @param testCaseId the test case ID
     * @return count of successful runs
     */
    @Query("SELECT COUNT(r) FROM TestCaseRunEntity r " +
           "WHERE r.testCaseId = :testCaseId AND r.result = 'SUCCESS'")
    long countSuccessfulRuns(@Param("testCaseId") Long testCaseId);

    /**
     * Counts failed runs for a specific test case.
     *
     * @param testCaseId the test case ID
     * @return count of failed runs
     */
    @Query("SELECT COUNT(r) FROM TestCaseRunEntity r " +
           "WHERE r.testCaseId = :testCaseId AND r.result = 'FAILURE'")
    long countFailedRuns(@Param("testCaseId") Long testCaseId);

    /**
     * Finds test case runs within a test suite run.
     *
     * @param testSuiteRunId the test suite run ID
     * @return list of test case runs
     */
    @Query("SELECT r FROM TestCaseRunEntity r WHERE r.testSuiteRunId = :testSuiteRunId")
    List<TestCaseRunEntity> findByTestSuiteRunId(@Param("testSuiteRunId") Long testSuiteRunId);

    /**
     * Gets average response time for API tests.
     * Only includes ApiTestRunEntity instances.
     *
     * @return average response time in milliseconds, null if no API test runs
     */
    @Query("SELECT AVG(r.responseTimeMs) FROM ApiTestRunEntity r WHERE r.responseTimeMs IS NOT NULL")
    Double getAverageApiResponseTime();

    /**
     * Finds API test runs that exceeded a response time threshold.
     * Useful for identifying slow API tests.
     *
     * @param thresholdMs the response time threshold in milliseconds
     * @return list of slow API test runs
     */
    @Query("SELECT r FROM ApiTestRunEntity r " +
           "WHERE r.responseTimeMs > :thresholdMs " +
           "ORDER BY r.responseTimeMs DESC")
    List<ApiTestRunEntity> findSlowApiTests(@Param("thresholdMs") Long thresholdMs);

    /**
     * Gets statistics on assertion results across all test runs.
     * Returns counts of passed vs failed assertions.
     *
     * @return list of [Passed (boolean), Count] pairs
     */
    @Query("SELECT ar.passed, COUNT(ar) FROM ApiTestRunEntity r " +
           "JOIN r.assertionResults ar " +
           "GROUP BY ar.passed")
    List<Object[]> getAssertionStatistics();
}
