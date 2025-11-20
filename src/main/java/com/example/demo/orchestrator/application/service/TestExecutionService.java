package com.example.demo.orchestrator.application.service;

import com.example.demo.orchestrator.application.dto.execution.ExecuteTestCaseRequest;
import com.example.demo.orchestrator.application.dto.execution.ExecuteTestSuiteRequest;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.domain.project.ProjectRepository;
import com.example.demo.orchestrator.domain.run.TestCaseRun;
import com.example.demo.orchestrator.domain.run.TestCaseRunRepository;
import com.example.demo.orchestrator.domain.run.TestSuiteRun;
import com.example.demo.orchestrator.domain.run.TestSuiteRunRepository;
import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuiteRepository;
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
    // TODO: Inject TestRunner once infrastructure is implemented
    // private final TestRunner testRunner;

    public TestExecutionService(
            TestSuiteRepository testSuiteRepository,
            TestSuiteRunRepository testSuiteRunRepository,
            TestCaseRunRepository testCaseRunRepository,
            ProjectRepository projectRepository) {
        this.testSuiteRepository = testSuiteRepository;
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.projectRepository = projectRepository;
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

        // TODO: Execute tests asynchronously using TestRunner
        // Long finalSuiteRunId = suiteRun.getId();
        // executorService.submit(() -> {
        //     for (TestCase testCase : testSuite.getTestCases()) {
        //         TestCaseRun caseRun = testRunner.execute(testCase, resolvedVariables);
        //         caseRun = testCaseRunRepository.save(caseRun);
        //         suiteRun.addTestCaseRun(caseRun);
        //     }
        //     suiteRun.complete();
        //     testSuiteRunRepository.save(suiteRun);
        // });

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

        // TODO: Execute test using TestRunner
        // TestCaseRun caseRun = testRunner.execute(testCase, resolvedVariables);
        // caseRun = testCaseRunRepository.save(caseRun);
        // return caseRun.getId();

        // Placeholder until TestRunner is implemented
        throw new UnsupportedOperationException("Test execution not yet implemented");
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
