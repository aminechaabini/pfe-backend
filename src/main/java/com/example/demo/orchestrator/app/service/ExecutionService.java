package com.example.demo.orchestrator.app.service;

import com.example.demo.Runner.RunResultCallback;
import com.example.demo.Runner.RunnerService;
import com.example.demo.orchestrator.app.mapper.config.CycleAvoidingMappingContext;
import com.example.demo.orchestrator.app.mapper.definition.TestCaseMapper;
import com.example.demo.orchestrator.app.mapper.definition.TestSuiteMapper;
import com.example.demo.orchestrator.app.mapper.run.RunRequestMapper;
import com.example.demo.orchestrator.app.mapper.run.TestCaseRunMapper;
import com.example.demo.orchestrator.app.mapper.run.TestSuiteRunMapper;
import com.example.demo.orchestrator.app.service.exception.EntityNotFoundException;
import com.example.demo.orchestrator.domain.project.Project;
import com.example.demo.orchestrator.domain.run.TestCaseRun;
import com.example.demo.orchestrator.domain.run.TestSuiteRun;
import com.example.demo.orchestrator.domain.test.TestCase;
import com.example.demo.orchestrator.domain.test.api.RestApiTest;
import com.example.demo.orchestrator.domain.test.api.SoapApiTest;
import com.example.demo.orchestrator.domain.test.e2e.E2eTest;
import com.example.demo.orchestrator.domain.test.test_suite.TestSuite;
import com.example.demo.orchestrator.persistence.entity.project.ProjectEntity;
import com.example.demo.orchestrator.persistence.entity.run.TestCaseRunEntity;
import com.example.demo.orchestrator.persistence.entity.run.TestSuiteRunEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestCaseEntity;
import com.example.demo.orchestrator.persistence.entity.test.TestSuiteEntity;
import com.example.demo.orchestrator.persistence.repository.ProjectRepository;
import com.example.demo.orchestrator.persistence.repository.TestCaseRepository;
import com.example.demo.orchestrator.persistence.repository.TestCaseRunRepository;
import com.example.demo.orchestrator.persistence.repository.TestSuiteRepository;
import com.example.demo.orchestrator.persistence.repository.TestSuiteRunRepository;
import com.example.demo.shared.request.ApiRunRequest;
import com.example.demo.shared.request.E2eRunRequest;
import com.example.demo.shared.request.RunRequest;
import com.example.demo.shared.result.ApiRunResult;
import com.example.demo.shared.result.E2eRunResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for executing tests and managing execution results (MVP version).
 *
 * Responsibilities:
 * - Trigger test execution (single test or suite)
 * - Query run status and history
 * - Handle execution callbacks from Runner
 *
 * MVP Methods (7 total):
 * - executeTest, executeSuite
 * - getRun, getRunByRunId
 * - getTestRunHistory, getSuiteRun
 * - handleRunResult (callback)
 */
@Service
@Transactional
public class ExecutionService {

    private static final Logger log = LoggerFactory.getLogger(ExecutionService.class);

    private final RunnerService runnerService;
    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final ProjectRepository projectRepository;
    private final TestCaseRunRepository testCaseRunRepository;
    private final TestSuiteRunRepository testSuiteRunRepository;
    private final RunRequestMapper runRequestMapper;
    private final TestCaseMapper testCaseMapper;
    private final TestSuiteMapper testSuiteMapper;
    private final TestCaseRunMapper testCaseRunMapper;
    private final TestSuiteRunMapper testSuiteRunMapper;

    // Track runId -> run entity ID for callback handling
    private final Map<String, Long> runIdMap = new HashMap<>();

    public ExecutionService(
            RunnerService runnerService,
            TestCaseRepository testCaseRepository,
            TestSuiteRepository testSuiteRepository,
            ProjectRepository projectRepository,
            TestCaseRunRepository testCaseRunRepository,
            TestSuiteRunRepository testSuiteRunRepository,
            RunRequestMapper runRequestMapper,
            TestCaseMapper testCaseMapper,
            TestSuiteMapper testSuiteMapper,
            TestCaseRunMapper testCaseRunMapper,
            TestSuiteRunMapper testSuiteRunMapper) {
        this.runnerService = runnerService;
        this.testCaseRepository = testCaseRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.projectRepository = projectRepository;
        this.testCaseRunRepository = testCaseRunRepository;
        this.testSuiteRunRepository = testSuiteRunRepository;
        this.runRequestMapper = runRequestMapper;
        this.testCaseMapper = testCaseMapper;
        this.testSuiteMapper = testSuiteMapper;
        this.testCaseRunMapper = testCaseRunMapper;
        this.testSuiteRunMapper = testSuiteRunMapper;
    }

    // ========================================================================
    // EXECUTE OPERATIONS
    // ========================================================================

    /**
     * Execute a single test (async).
     * Creates a run entity, submits to runner queue, returns run ID immediately.
     *
     * @param testId test ID
     * @return run ID (UUID)
     * @throws EntityNotFoundException if test not found
     */
    public String executeTest(Long testId) {
        // Fetch test entity
        TestCaseEntity testEntity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new EntityNotFoundException("TestCase", testId));

        // Fetch test suite and project to get variables
        TestSuiteEntity suite = null;
        ProjectEntity project = null;
        if (testEntity.getTestSuiteId() != null) {
            suite = testSuiteRepository.findById(testEntity.getTestSuiteId()).orElse(null);
            if (suite != null && !suite.getProjects().isEmpty()) {
                // Get first project (for MVP - a suite should typically belong to one project)
                project = suite.getProjects().iterator().next();
            }
        }

        // Merge variables
        Map<String, String> variables = mergeVariables(project, suite);

        // Generate unique run ID
        String runId = UUID.randomUUID().toString();

        // Create run entity
        TestCaseRun runDomain = createTestCaseRunDomain(testEntity);
        runDomain.start();

        TestCaseRunEntity runEntity = testCaseRunMapper.toEntity(runDomain);
        runEntity.setTestCaseId(testId);
        runEntity.setTestCaseName(testEntity.getName());
        runEntity = testCaseRunRepository.save(runEntity);

        // Track runId -> entity ID
        runIdMap.put(runId, runEntity.getId());

        // Build run request
        RunRequest request = buildRunRequest(testEntity, runId, variables);

        // Submit to runner with callback
        runnerService.submit(request, result -> handleRunResult(result));

        log.info("Submitted test {} for execution with runId {}", testId, runId);
        return runId;
    }

    /**
     * Execute a test suite (async).
     * Creates suite run and individual test case runs, submits all to runner queue.
     *
     * @param suiteId test suite ID
     * @return suite run ID (UUID)
     * @throws EntityNotFoundException if suite not found
     */
    public String executeSuite(Long suiteId) {
        // Fetch suite
        TestSuiteEntity suiteEntity = testSuiteRepository.findByIdWithTestCases(suiteId)
                .orElseThrow(() -> new EntityNotFoundException("TestSuite", suiteId));

        // Fetch project for variables
        ProjectEntity project = null;
        if (!suiteEntity.getProjects().isEmpty()) {
            project = suiteEntity.getProjects().iterator().next();
        }

        Map<String, String> variables = mergeVariables(project, suiteEntity);

        // Create suite run
        TestSuiteRun suiteRunDomain = new TestSuiteRun();
        TestSuite suite = testSuiteMapper.toDomain(suiteEntity, new CycleAvoidingMappingContext());
        suiteRunDomain.setTestSuite(suite);
        suiteRunDomain.start();

        TestSuiteRunEntity suiteRunEntity = testSuiteRunMapper.toEntity(suiteRunDomain);
        suiteRunEntity = testSuiteRunRepository.save(suiteRunEntity);

        String suiteRunId = suiteRunEntity.getId().toString();

        // Get all test cases in suite
        List<TestCaseEntity> testCases = testCaseRepository.findByTestSuiteId(suiteId);

        // Submit each test
        for (TestCaseEntity testEntity : testCases) {
            String runId = UUID.randomUUID().toString();

            // Create test case run
            TestCaseRun runDomain = createTestCaseRunDomain(testEntity);
            runDomain.start();

            TestCaseRunEntity runEntity = testCaseRunMapper.toEntity(runDomain);
            runEntity.setTestCaseId(testEntity.getId());
            runEntity.setTestCaseName(testEntity.getName());
            runEntity.setTestSuiteRunId(suiteRunEntity.getId());
            runEntity = testCaseRunRepository.save(runEntity);

            // Track runId -> entity ID
            runIdMap.put(runId, runEntity.getId());

            // Build and submit request
            RunRequest request = buildRunRequest(testEntity, runId, variables);
            runnerService.submit(request, result -> handleRunResult(result));
        }

        log.info("Submitted suite {} ({} tests) for execution with suiteRunId {}",
                 suiteId, testCases.size(), suiteRunId);
        return suiteRunId;
    }

    // ========================================================================
    // QUERY OPERATIONS
    // ========================================================================

    /**
     * Get a run by entity ID.
     *
     * @param runId run entity ID
     * @return test case run
     * @throws EntityNotFoundException if run not found
     */
    @Transactional(readOnly = true)
    public TestCaseRun getRun(Long runId) {
        TestCaseRunEntity entity = testCaseRunRepository.findById(runId)
                .orElseThrow(() -> new EntityNotFoundException("Run", runId));

        return testCaseRunMapper.toDomain(entity);
    }

    /**
     * Get a run by UUID (returned from executeTest).
     *
     * @param runId run UUID
     * @return test case run
     * @throws EntityNotFoundException if run not found
     */
    @Transactional(readOnly = true)
    public TestCaseRun getRunByRunId(String runId) {
        Long entityId = runIdMap.get(runId);
        if (entityId == null) {
            throw new EntityNotFoundException("Run with runId " + runId + " not found");
        }

        return getRun(entityId);
    }

    /**
     * Get test run history (most recent N runs).
     *
     * @param testId test ID
     * @param limit number of runs to return
     * @return list of recent runs
     * @throws EntityNotFoundException if test not found
     */
    @Transactional(readOnly = true)
    public List<TestCaseRun> getTestRunHistory(Long testId, int limit) {
        // Verify test exists
        if (!testCaseRepository.existsById(testId)) {
            throw new EntityNotFoundException("TestCase", testId);
        }

        List<TestCaseRunEntity> entities = testCaseRunRepository.findRecentByTestCaseId(
                testId, PageRequest.of(0, limit));

        return entities.stream()
                .map(testCaseRunMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Get suite run with all test case runs.
     *
     * @param suiteRunId suite run ID
     * @return test suite run
     * @throws EntityNotFoundException if suite run not found
     */
    @Transactional(readOnly = true)
    public TestSuiteRun getSuiteRun(Long suiteRunId) {
        TestSuiteRunEntity entity = testSuiteRunRepository.findById(suiteRunId)
                .orElseThrow(() -> new EntityNotFoundException("TestSuiteRun", suiteRunId));

        TestSuiteRun suiteRun = testSuiteRunMapper.toDomain(entity);

        // Load test case runs
        List<TestCaseRunEntity> testCaseRunEntities = testCaseRunRepository.findByTestSuiteRunId(suiteRunId);
        testCaseRunEntities.forEach(tcre -> {
            TestCaseRun tcr = testCaseRunMapper.toDomain(tcre);
            suiteRun.addTestCaseRun(tcr);
        });

        return suiteRun;
    }

    // ========================================================================
    // CALLBACK HANDLING
    // ========================================================================

    /**
     * Handle run result from RunnerService callback.
     * Updates run entity with result data.
     *
     * @param result run result
     */
    @Transactional
    public void handleRunResult(com.example.demo.shared.result.RunResult result) {
        Long entityId = runIdMap.get(result.runId());
        if (entityId == null) {
            log.error("No run entity found for runId: {}", result.runId());
            return;
        }

        TestCaseRunEntity runEntity = testCaseRunRepository.findById(entityId).orElse(null);
        if (runEntity == null) {
            log.error("Run entity {} not found in database", entityId);
            return;
        }

        // Update run status based on result
        TestCaseRun runDomain = testCaseRunMapper.toDomain(runEntity);

        if ("PASS".equals(result.status())) {
            runDomain.completeWithSuccess();
        } else {
            runDomain.completeWithFailure();
        }

        // Store result data (implementation depends on entity structure)
        // For MVP, just update status
        TestCaseRunEntity updated = testCaseRunMapper.toEntity(runDomain);
        testCaseRunRepository.save(updated);

        // Clean up tracking map
        runIdMap.remove(result.runId());

        log.info("Updated run {} with status {}", result.runId(), result.status());
    }

    // ========================================================================
    // HELPER METHODS
    // ========================================================================

    /**
     * Merge project and suite variables (suite overrides project).
     */
    private Map<String, String> mergeVariables(ProjectEntity project, TestSuiteEntity suite) {
        Map<String, String> merged = new HashMap<>();

        if (project != null && project.getVariables() != null) {
            merged.putAll(project.getVariables());
        }

        if (suite != null && suite.getVariables() != null) {
            merged.putAll(suite.getVariables());
        }

        return merged;
    }

    /**
     * Build run request from test entity.
     */
    private RunRequest buildRunRequest(TestCaseEntity testEntity, String runId, Map<String, String> variables) {
        TestCase domain = testCaseMapper.toDomain(testEntity);

        return switch (domain) {
            case RestApiTest rest -> runRequestMapper.toApiRunRequest(rest, runId, variables);
            case SoapApiTest soap -> runRequestMapper.toApiRunRequest(soap, runId, variables);
            case E2eTest e2e -> runRequestMapper.toE2eRunRequest(e2e, runId, variables);
            default -> throw new IllegalArgumentException("Unsupported test type: " + domain.getClass());
        };
    }

    /**
     * Create domain run object for test entity.
     */
    private TestCaseRun createTestCaseRunDomain(TestCaseEntity testEntity) {
        // For MVP, create a basic TestCaseRun
        // In full implementation, this would create specific run types (ApiTestRun, E2eTestRun)
        return new TestCaseRun() {
            // Anonymous subclass for MVP - full implementation would have typed runs
        };
    }
}
