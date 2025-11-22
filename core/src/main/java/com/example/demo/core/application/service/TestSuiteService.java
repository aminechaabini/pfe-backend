package com.example.demo.core.application.service;

import com.example.demo.core.application.dto.project.SetVariableRequest;
import com.example.demo.core.application.dto.suite.CreateTestSuiteRequest;
import com.example.demo.core.application.dto.suite.UpdateTestSuiteRequest;
import com.example.demo.core.domain.project.Project;
import com.example.demo.core.domain.project.ProjectRepository;
import com.example.demo.core.domain.spec.Endpoint;
import com.example.demo.core.domain.spec.EndpointRepository;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.core.domain.test.test_suite.TestSuiteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Application service for managing test suites.
 *
 * Responsibilities:
 * - Test suite CRUD operations
 * - Test case management within suites
 * - Variable management
 * - Coordinate test suite aggregate
 *
 * Uses domain repository interfaces (ports) - infrastructure provides implementations.
 */
@Service
@Transactional
public class TestSuiteService {

    private final TestSuiteRepository testSuiteRepository;
    private final ProjectRepository projectRepository;
    private final EndpointRepository endpointRepository;

    public TestSuiteService(
            TestSuiteRepository testSuiteRepository,
            ProjectRepository projectRepository,
            EndpointRepository endpointRepository) {
        this.testSuiteRepository = testSuiteRepository;
        this.projectRepository = projectRepository;
        this.endpointRepository = endpointRepository;
    }

    /**
     * Create a new test suite and add it to the project.
     *
     * @param request test suite creation data
     * @return created test suite
     * @throws IllegalArgumentException if project not found or suite name exists
     */
    public TestSuite createTestSuite(CreateTestSuiteRequest request) {
        // Verify project exists
        Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Project not found: " + request.projectId()));

        // Get endpoint if specified (null for E2E tests)
        Endpoint endpoint = null;
        if (request.endpointId() != null) {
            endpoint = endpointRepository.findById(request.endpointId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Endpoint not found: " + request.endpointId()));
        }

        // Prepare initial variables
        Map<String, String> variables = request.initialVariables() != null
                ? request.initialVariables()
                : new HashMap<>();

        // Create test suite domain object
        TestSuite testSuite = new TestSuite(
                request.name(),
                request.description(),
                request.projectId()
        );



        // Save test suite
        testSuite = testSuiteRepository.save(testSuite);

        // Add to project
        project.addSuite(testSuite);
        projectRepository.save(project);

        return testSuite;
    }

    /**
     * Get a test suite by ID.
     *
     * @param id test suite ID
     * @return test suite
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public TestSuite getTestSuite(Long id) {
        return testSuiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + id));
    }

    /**
     * Get a test suite with all test cases loaded.
     *
     * @param id test suite ID
     * @return test suite with test cases
     * @throws IllegalArgumentException if not found
     */
    @Transactional(readOnly = true)
    public TestSuite getTestSuiteWithTestCases(Long id) {
        return testSuiteRepository.findByIdWithTestCases(id)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + id));
    }

    /**
     * Get all test suites for a project.
     *
     * @param projectId project ID
     * @return list of test suites
     */
    @Transactional(readOnly = true)
    public List<TestSuite> getProjectTestSuites(Long projectId) {
        return testSuiteRepository.findByProjectId(projectId);
    }

    /**
     * Get all test suites associated with an endpoint.
     *
     * @param endpointId endpoint ID
     * @return list of test suites
     */
    @Transactional(readOnly = true)
    public List<TestSuite> getEndpointTestSuites(Long endpointId) {
        return testSuiteRepository.findByEndpointId(endpointId);
    }

    /**
     * Update a test suite.
     * Only non-null fields are updated.
     *
     * @param id test suite ID
     * @param request update data
     * @return updated test suite
     * @throws IllegalArgumentException if not found
     */
    public TestSuite updateTestSuite(Long id, UpdateTestSuiteRequest request) {
        TestSuite testSuite = testSuiteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + id));

        // Update name if provided
        if (request.name() != null) {
            testSuite.rename(request.name());
        }

        // Update description if provided
        if (request.description() != null) {
            testSuite.updateDescription(request.description());
        }

        return testSuiteRepository.save(testSuite);
    }

    /**
     * Delete a test suite.
     * This will cascade delete all test cases in the suite.
     *
     * @param id test suite ID
     * @throws IllegalArgumentException if not found
     */
    public void deleteTestSuite(Long id) {
        if (!testSuiteRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("Test suite not found: " + id);
        }
        testSuiteRepository.deleteById(id);
    }

    /**
     * Add a test case to the suite.
     * Used after AI generation or manual creation.
     *
     * @param testSuiteId test suite ID
     * @param testCase test case to add
     * @return updated test suite
     * @throws IllegalArgumentException if suite not found
     */
    public TestSuite addTestCase(Long testSuiteId, TestCase testCase) {
        TestSuite testSuite = testSuiteRepository.findByIdWithTestCases(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        // Use domain method - handles validation
        testSuite.addTestCase(testCase);

        return testSuiteRepository.save(testSuite);
    }

    /**
     * Remove a test case from the suite.
     *
     * @param testSuiteId test suite ID
     * @param testCaseId test case ID
     * @return updated test suite
     * @throws IllegalArgumentException if suite or test case not found
     */
    public TestSuite removeTestCase(Long testSuiteId, Long testCaseId) {
        TestSuite testSuite = testSuiteRepository.findByIdWithTestCases(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        TestCase testCase = testSuite.findTestCaseById(testCaseId)
                .orElseThrow(() -> new IllegalArgumentException("Test case not found: " + testCaseId));

        // Use domain method
        testSuite.removeTestCase(testCase);

        return testSuiteRepository.save(testSuite);
    }

    /**
     * Set or update a test suite variable.
     *
     * @param testSuiteId test suite ID
     * @param request variable data
     * @return updated test suite
     * @throws IllegalArgumentException if suite not found or validation fails
     */
    public TestSuite setVariable(Long testSuiteId, SetVariableRequest request) {
        TestSuite testSuite = testSuiteRepository.findById(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        // Use domain method - handles validation
        testSuite.setVariable(request.name(), request.value());

        return testSuiteRepository.save(testSuite);
    }

    /**
     * Remove a test suite variable.
     *
     * @param testSuiteId test suite ID
     * @param variableName variable name
     * @return updated test suite
     * @throws IllegalArgumentException if suite or variable not found
     */
    public TestSuite removeVariable(Long testSuiteId, String variableName) {
        TestSuite testSuite = testSuiteRepository.findById(testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException("Test suite not found: " + testSuiteId));

        // Use domain method - handles validation
        testSuite.removeVariable(variableName);

        return testSuiteRepository.save(testSuite);
    }
}
