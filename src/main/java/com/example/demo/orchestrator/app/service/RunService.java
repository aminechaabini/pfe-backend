package com.example.demo.orchestrator.app.service;


import com.example.demo.orchestrator.app.mapper.run.RunRequestMapper;
import com.example.demo.orchestrator.infrastructure.persistence.entity.project.ProjectEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.E2eTestEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.RestApiTestEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.SoapApiTestEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.TestCaseEntity;
import com.example.demo.orchestrator.infrastructure.persistence.entity.test.TestSuiteEntity;
import com.example.demo.orchestrator.infrastructure.persistence.jpa.ProjectRepository;
import com.example.demo.orchestrator.infrastructure.persistence.jpa.TestCaseRepository;
import com.example.demo.orchestrator.infrastructure.persistence.jpa.TestSuiteRepository;
import com.example.demo.orchestrator.persistence.run.Run;
import com.example.demo.orchestrator.dto.run.TriggerRunRequest;
import com.example.demo.orchestrator.infra.RunRepository;
import com.example.demo.shared.events.ApiRunRequest;
import com.example.demo.shared.events.E2eRunRequest;
import com.example.demo.shared.events.RunResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class RunService {

    private final RunRepository runRepository;
    private final Runner runner;
    private final TestCaseRepository testCaseRepository;
    private final TestSuiteRepository testSuiteRepository;
    private final ProjectRepository projectRepository;
    private final RunRequestMapper runRequestMapper;

    public RunService(
            RunRepository runRepository,
            Runner runner,
            TestCaseRepository testCaseRepository,
            TestSuiteRepository testSuiteRepository,
            ProjectRepository projectRepository,
            RunRequestMapper runRequestMapper) {
        this.runRepository = runRepository;
        this.runner = runner;
        this.testCaseRepository = testCaseRepository;
        this.testSuiteRepository = testSuiteRepository;
        this.projectRepository = projectRepository;
        this.runRequestMapper = runRequestMapper;
    }

    /**
     * Triggers a test run based on the request.
     * Executes the test synchronously and returns the result.
     * Handles both API tests (REST/SOAP) and E2E tests.
     *
     * @param request contains projectId, suiteId (optional), and testId to execute
     * @return RunResult with test execution details
     */
    public RunResult trigger(TriggerRunRequest request) {
        // 1. Fetch test entity
        Long testId = Long.parseLong(request.testId());
        TestCaseEntity testEntity = testCaseRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found: " + testId));

        // 2. Fetch project and suite to get variables
        Long projectId = Long.parseLong(request.projectId());
        ProjectEntity project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        TestSuiteEntity suite = null;
        if (testEntity.getTestSuiteId() != null) {
            suite = testSuiteRepository.findById(testEntity.getTestSuiteId())
                    .orElse(null);
        }

        // 3. Merge variables (project vars, then suite vars override)
        Map<String, String> mergedVariables = mergeVariables(project, suite);

        // 4. Generate unique run ID
        String runId = UUID.randomUUID().toString();

        // 5. Execute test based on type (API vs E2E)
        RunResult result;
        if (testEntity instanceof E2eTestEntity e2eTest) {
            // E2E test: multiple sequential steps
            E2eRunRequest e2eRunRequest = runRequestMapper.toE2eRunRequest(e2eTest, runId, mergedVariables);
            result = runner.runE2e(e2eRunRequest);
        } else if (testEntity instanceof RestApiTestEntity restTest) {
            // Single REST API test
            ApiRunRequest apiRunRequest = runRequestMapper.toApiRunRequest(restTest, runId, mergedVariables);
            result = runner.runApi(apiRunRequest);
        } else if (testEntity instanceof SoapApiTestEntity soapTest) {
            // Single SOAP API test
            ApiRunRequest apiRunRequest = runRequestMapper.toApiRunRequest(soapTest, runId, mergedVariables);
            result = runner.runApi(apiRunRequest);
        } else {
            throw new IllegalArgumentException(
                    "Unsupported test type: " + testEntity.getClass().getSimpleName());
        }

        // 6. Save run result to DB (optional)
        // Run run = new Run(runId, testId, result.status(), result);
        // runRepository.save(run);

        return result;
    }

    /**
     * Merges project and suite variables.
     * Suite variables override project variables with the same key.
     *
     * @param project the project entity (required)
     * @param suite the test suite entity (optional)
     * @return merged variables map
     */
    private Map<String, String> mergeVariables(ProjectEntity project, TestSuiteEntity suite) {
        Map<String, String> merged = new HashMap<>();

        // Start with project variables
        if (project.getVariables() != null) {
            merged.putAll(project.getVariables());
        }

        // Suite variables override project variables
        if (suite != null && suite.getVariables() != null) {
            merged.putAll(suite.getVariables());
        }

        return merged;
    }

    public Optional<Run> findById(Long runId) {
        return runRepository.findById(runId);
    }

    public List<Run> findAll() {
        return runRepository.findAll();
    }
}
