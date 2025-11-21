package com.example.demo.core.app.service;

import com.example.demo.core.app.mapper.run.TestSuiteRunMapper;
import com.example.demo.core.app.service.exception.EntityNotFoundException;
import com.example.demo.core.domain.run.TestSuiteRun;
import com.example.demo.core.infrastructure.persistence.entity.run.TestSuiteRunEntity;
import com.example.demo.core.infrastructure.persistence.jpa.ProjectRepository;
import com.example.demo.core.infrastructure.persistence.jpa.TestCaseRepository;
import com.example.demo.core.infrastructure.persistence.jpa.TestCaseRunRepository;
import com.example.demo.core.infrastructure.persistence.jpa.TestSuiteRunRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for generating reports and metrics (MVP version).
 *
 * Responsibilities:
 * - Project, test, and suite metrics
 * - Execution reports
 * - Top/bottom lists
 *
 * MVP Methods (4 total):
 * - getProjectMetrics
 * - getTestMetrics
 * - getSuiteRunReport
 * - getTopFailingTests
 */
@Service
@Transactional(readOnly = true)
public class ReportingService {

    private final ProjectRepository projectRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final TestSuiteRunRepository testSuiteRunRepository;
    private final TestSuiteRunMapper testSuiteRunMapper;

    public ReportingService(
            ProjectRepository projectRepository,
            TestCaseRepository testCaseRepository,
            TestCaseRunRepository testCaseRunRepository,
            TestSuiteRunRepository testSuiteRunRepository,
            TestSuiteRunMapper testSuiteRunMapper) {
        this.projectRepository = projectRepository;
        this.testCaseRepository = testCaseRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.testSuiteRunMapper = testSuiteRunMapper;
    }

    // ========================================================================
    // METRICS
    // ========================================================================

    /**
     * Get project metrics.
     *
     * @param projectId project ID
     * @return project metrics
     * @throws EntityNotFoundException if project not found
     */
    public ProjectMetrics getProjectMetrics(Long projectId) {
        // Verify project exists
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project", projectId);
        }

        // Count test suites in project
        long totalSuites = testCaseRepository.findByTestSuiteId(projectId).size();

        // Count total tests (this is simplified - would need proper query)
        long totalTests = testCaseRepository.countAllTestCases();

        // Get run statistics from test suite runs
        // For MVP, we'll use simplified calculations
        long totalRuns = testCaseRunRepository.count();
        long passedRuns = testCaseRunRepository.countByType(com.example.demo.orchestrator.persistence.entity.run.ApiTestRunEntity.class); // Placeholder
        long failedRuns = totalRuns - passedRuns;

        double passRate = totalRuns > 0 ? (double) passedRuns / totalRuns : 0.0;

        return new ProjectMetrics(
                totalSuites,
                totalTests,
                totalRuns,
                passRate,
                0.0  // Average duration - would need proper calculation
        );
    }

    /**
     * Get test metrics.
     *
     * @param testId test ID
     * @return test metrics
     * @throws EntityNotFoundException if test not found
     */
    public TestMetrics getTestMetrics(Long testId) {
        // Verify test exists
        if (!testCaseRepository.existsById(testId)) {
            throw new EntityNotFoundException("TestCase", testId);
        }

        // Get run history for this test
        long totalRuns = testCaseRunRepository.countByTestCaseId(testId);
        long successfulRuns = testCaseRunRepository.countSuccessfulRuns(testId);
        long failedRuns = testCaseRunRepository.countFailedRuns(testId);

        double passRate = testCaseRunRepository.getSuccessRate(testId);
        if (passRate == null) {
            passRate = 0.0;
        }

        // Average response time (for API tests)
        Double avgResponseTime = testCaseRunRepository.getAverageApiResponseTime();
        if (avgResponseTime == null) {
            avgResponseTime = 0.0;
        }

        return new TestMetrics(
                totalRuns,
                passRate,
                avgResponseTime
        );
    }

    /**
     * Get suite execution report.
     *
     * @param suiteRunId suite run ID
     * @return suite execution report
     * @throws EntityNotFoundException if suite run not found
     */
    public SuiteExecutionReport getSuiteRunReport(Long suiteRunId) {
        TestSuiteRunEntity entity = testSuiteRunRepository.findById(suiteRunId)
                .orElseThrow(() -> new EntityNotFoundException("TestSuiteRun", suiteRunId));

        TestSuiteRun suiteRun = testSuiteRunMapper.toDomain(entity);

        // Get test case runs
        var testCaseRuns = testCaseRunRepository.findByTestSuiteRunId(suiteRunId);

        long totalTests = testCaseRuns.size();
        long passedTests = testCaseRuns.stream()
                .filter(run -> run.getResult() != null && run.getResult().name().equals("SUCCESS"))
                .count();
        long failedTests = totalTests - passedTests;

        return new SuiteExecutionReport(
                suiteRunId,
                entity.getTestSuite().getName(),
                totalTests,
                passedTests,
                failedTests
        );
    }

    /**
     * Get top failing tests.
     *
     * @param limit number of tests to return
     * @return list of test failure summaries
     */
    public List<TestFailureSummary> getTopFailingTests(int limit) {
        List<Object[]> results = testCaseRunRepository.findMostFailingTests(PageRequest.of(0, limit));

        return results.stream()
                .map(row -> new TestFailureSummary(
                        (Long) row[0],      // testCaseId
                        (String) row[1],    // testCaseName
                        ((Number) row[2]).longValue()  // failureCount
                ))
                .collect(Collectors.toList());
    }

    // ========================================================================
    // DTOs (Inner classes for MVP - would normally be in separate files)
    // ========================================================================

    /**
     * Project metrics DTO.
     */
    public record ProjectMetrics(
            long totalSuites,
            long totalTests,
            long totalRuns,
            double passRate,
            double avgDuration
    ) {}

    /**
     * Test metrics DTO.
     */
    public record TestMetrics(
            long totalRuns,
            double passRate,
            double avgResponseTime
    ) {}

    /**
     * Suite execution report DTO.
     */
    public record SuiteExecutionReport(
            Long suiteRunId,
            String suiteName,
            long totalTests,
            long passedTests,
            long failedTests
    ) {}

    /**
     * Test failure summary DTO.
     */
    public record TestFailureSummary(
            Long testId,
            String testName,
            long failureCount
    ) {}
}
