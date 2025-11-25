package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.execution.ExecuteTestCaseRequest;
import com.example.demo.core.application.dto.execution.ExecuteTestSuiteRequest;
import com.example.demo.core.domain.project.Project;
import com.example.demo.core.domain.project.ProjectRepository;
import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestCaseRunRepository;
import com.example.demo.core.domain.run.TestSuiteRun;
import com.example.demo.core.domain.run.TestSuiteRunRepository;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.domain.test.test_suite.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application service for executing tests and managing test runs.
 *
 * Responsibilities:
 * - Execute test suites and test cases
 * - Resolve variables (project vars + suite vars + environment vars)
 * - Track test run history
 * - Provide test run results for analysis
 *
 * Uses domain repository interfaces (ports) - infrastructure provides implementations.
 */
@Service
@Transactional
public class TestExecutionService {

    private final TestSuiteRepository testSuiteRepository;
    private final TestSuiteRunRepository testSuiteRunRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final ProjectRepository projectRepository;
    private final com.example.demo.core.application.ports.TestExecutionPort testExecutionPort;

    public TestExecutionService(
            TestSuiteRepository testSuiteRepository,
            TestSuiteRunRepository testSuiteRunRepository,
            TestCaseRunRepository testCaseRunRepository,
            ProjectRepository projectRepository,
            com.example.demo.core.application.ports.TestExecutionPort testExecutionPort) {
        this.testSuiteRepository = testSuiteRepository;
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.projectRepository = projectRepository;
        this.testExecutionPort = testExecutionPort;
    }

    /**
     * Execute an entire test suite asynchronously.
     * Resolves variables: project vars + suite vars + environment vars.
     * Returns the run ID immediately, execution happens in background.
     *
     * @param request execution request with suite ID and environment variables
     * @return run ID for tracking execution status
     * @throws IllegalArgumentException if test suite not found
     */
    public Long executeTestSuite(ExecuteTestSuiteRequest request) {
        // Load test suite with test cases
        TestSuite testSuite = testSuiteRepository.findByIdWithTestCases(request.testSuiteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test suite not found: " + request.testSuiteId()));

        // Find the project to get project variables
        Project project = projectRepository.findByTestSuiteId(request.testSuiteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found for test suite: " + request.testSuiteId()));

        // Resolve all variables (priority: environment > suite > project)
        Map<String, String> resolvedVariables = resolveVariables(
                project.getVariables(),
                testSuite.getVariables(),
                request.environmentVariables()
        );

        // Create test suite run
        TestSuiteRun suiteRun = new TestSuiteRun();
        suiteRun.setTestSuite(testSuite);
        suiteRun.start();

        // Save run to get ID
        suiteRun = testSuiteRunRepository.save(suiteRun);

        // Execute tests asynchronously using TestExecutionPort
        Long finalSuiteRunId = suiteRun.getId();
        // TODO: Use ExecutorService for true async execution
        // For now, execute synchronously
        TestSuiteRun executedSuiteRun = testExecutionPort.executeTestSuite(testSuite, resolvedVariables);

        // Save updated run results
        for (TestCaseRun caseRun : executedSuiteRun.getTestCaseRuns()) {
            testCaseRunRepository.save(caseRun);
        }
        testSuiteRunRepository.save(executedSuiteRun);

        return suiteRun.getId();
    }

    /**
     * Execute a single test case.
     *
     * @param request execution request with test case ID and environment variables
     * @return run ID for tracking execution status
     * @throws IllegalArgumentException if test case not found
     */
    public Long executeTestCase(ExecuteTestCaseRequest request) {
        // Find test suite containing the test case
        TestSuite testSuite = testSuiteRepository.findByTestCaseId(request.testCaseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test case not found: " + request.testCaseId()));

        TestCase testCase = testSuite.findTestCaseById(request.testCaseId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test case not found: " + request.testCaseId()));

        // Find the project to get project variables
        Project project = projectRepository.findByTestSuiteId(testSuite.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found for test suite: " + testSuite.getId()));

        // Resolve all variables
        Map<String, String> resolvedVariables = resolveVariables(
                project.getVariables(),
                testSuite.getVariables(),
                request.environmentVariables()
        );

        // Execute test using TestExecutionPort
        TestCaseRun caseRun = testExecutionPort.executeTestCase(testCase, resolvedVariables);
        caseRun = testCaseRunRepository.save(caseRun);
        return caseRun.getId();
    }

    /**
     * Get a test suite run with all results.
     *
     * @param runId run ID
     * @return test suite run
     * @throws IllegalArgumentException if run not found
     */
    @Transactional(readOnly = true)
    public TestSuiteRun getTestSuiteRun(Long runId) {
        return testSuiteRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite run not found: " + runId));
    }

    /**
     * Get a test case run with full details (request, response, assertions).
     * CRUCIAL for failure analysis - needs all the details.
     *
     * @param runId run ID
     * @return test case run with full details
     * @throws IllegalArgumentException if run not found
     */
    @Transactional(readOnly = true)
    public TestCaseRun getTestCaseRun(Long runId) {
        return testCaseRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Test case run not found: " + runId));
    }

    /**
     * Get run history for a test suite.
     *
     * @param testSuiteId test suite ID
     * @param limit maximum number of runs to return
     * @return list of test suite runs ordered by start time desc
     */
    @Transactional(readOnly = true)
    public List<TestSuiteRun> getRunHistory(Long testSuiteId, int limit) {
        return testSuiteRunRepository.findByTestSuiteIdOrderByStartTimeDesc(testSuiteId, limit);
    }

    /**
     * Resolve variables with priority: environment > suite > project.
     * Later values override earlier ones.
     *
     * @param projectVars project-level variables
     * @param suiteVars suite-level variables
     * @param envVars environment-specific variables
     * @return merged variable map
     */
    private Map<String, String> resolveVariables(
            Map<String, String> projectVars,
            Map<String, String> suiteVars,
            Map<String, String> envVars) {

        Map<String, String> resolved = new HashMap<>();

        // Start with project variables (lowest priority)
        if (projectVars != null) {
            resolved.putAll(projectVars);
        }

        // Override with suite variables (medium priority)
        if (suiteVars != null) {
            resolved.putAll(suiteVars);
        }

        // Override with environment variables (highest priority)
        if (envVars != null) {
            resolved.putAll(envVars);
        }

        return resolved;
    }
}
