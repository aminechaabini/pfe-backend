package com.example.demo.core.api;

import com.example.demo.core.api.dto.*;
import com.example.demo.core.app.service.ExecutionService;
import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestSuiteRun;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST API for Test Execution.
 *
 * Endpoints:
 * - POST   /api/tests/{id}/execute                - Execute single test
 * - POST   /api/suites/{id}/execute               - Execute test suite
 * - GET    /api/runs/{id}                         - Get run by entity ID
 * - GET    /api/runs/by-run-id/{runId}            - Get run by UUID
 * - GET    /api/tests/{id}/runs                   - Get test run history
 * - GET    /api/suite-runs/{id}                   - Get suite run details
 */
@RestController
@RequestMapping("/api")
public class ExecutionController {

    private final ExecutionService executionService;

    public ExecutionController(ExecutionService executionService) {
        this.executionService = executionService;
    }

    // ========================================================================
    // EXECUTE ENDPOINTS
    // ========================================================================

    /**
     * Execute a single test (async).
     *
     * POST /api/tests/{id}/execute
     *
     * @return runId (UUID) to track execution
     */
    @PostMapping("/tests/{id}/execute")
    public ResponseEntity<ExecutionResponse> executeTest(@PathVariable Long id) {
        String runId = executionService.executeTest(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ExecutionResponse(runId, "Test execution queued"));
    }

    /**
     * Execute a test suite (async).
     *
     * POST /api/suites/{id}/execute
     *
     * @return suiteRunId to track execution
     */
    @PostMapping("/suites/{id}/execute")
    public ResponseEntity<ExecutionResponse> executeSuite(@PathVariable Long id) {
        String suiteRunId = executionService.executeSuite(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ExecutionResponse(suiteRunId, "Suite execution queued"));
    }

    // ========================================================================
    // QUERY RUN STATUS ENDPOINTS
    // ========================================================================

    /**
     * Get run by entity ID.
     *
     * GET /api/runs/{id}
     */
    @GetMapping("/runs/{id}")
    public ResponseEntity<TestCaseRunResponse> getRun(@PathVariable Long id) {
        TestCaseRun run = executionService.getRun(id);
        return ResponseEntity.ok(toTestCaseRunResponse(run));
    }

    /**
     * Get run by UUID (runId returned from execute endpoint).
     *
     * GET /api/runs/by-run-id/{runId}
     */
    @GetMapping("/runs/by-run-id/{runId}")
    public ResponseEntity<TestCaseRunResponse> getRunByRunId(@PathVariable String runId) {
        TestCaseRun run = executionService.getRunByRunId(runId);
        return ResponseEntity.ok(toTestCaseRunResponse(run));
    }

    /**
     * Get test run history (recent N runs).
     *
     * GET /api/tests/{id}/runs?limit=10
     */
    @GetMapping("/tests/{id}/runs")
    public ResponseEntity<List<TestCaseRunResponse>> getTestRunHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int limit) {

        List<TestCaseRun> runs = executionService.getTestRunHistory(id, limit);
        List<TestCaseRunResponse> responses = runs.stream()
                .map(this::toTestCaseRunResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get suite run with all test case runs.
     *
     * GET /api/suite-runs/{id}
     */
    @GetMapping("/suite-runs/{id}")
    public ResponseEntity<TestSuiteRunResponse> getSuiteRun(@PathVariable Long id) {
        TestSuiteRun suiteRun = executionService.getSuiteRun(id);
        return ResponseEntity.ok(toTestSuiteRunResponse(suiteRun));
    }

    // ========================================================================
    // MAPPERS (Domain -> DTO)
    // ========================================================================

    private TestCaseRunResponse toTestCaseRunResponse(TestCaseRun run) {
        return new TestCaseRunResponse(
                run.getId(),
                run.getTestCase() != null ? run.getTestCase().getId() : null,
                run.getTestCase() != null ? run.getTestCase().getName() : "Unknown",
                run.getStatus().name(),
                run.getResult() != null ? run.getResult().name() : null,
                run.getCreatedAt(),
                run.getStartedAt(),
                run.getCompletedAt()
        );
    }

    private TestSuiteRunResponse toTestSuiteRunResponse(TestSuiteRun suiteRun) {
        List<TestCaseRunResponse> testCaseRuns = suiteRun.getTestCaseRuns().stream()
                .map(this::toTestCaseRunResponse)
                .collect(Collectors.toList());

        return new TestSuiteRunResponse(
                suiteRun.getId(),
                suiteRun.getTestSuite() != null ? suiteRun.getTestSuite().getId() : null,
                suiteRun.getTestSuite() != null ? suiteRun.getTestSuite().getName() : "Unknown",
                suiteRun.getStatus().name(),
                suiteRun.getResult() != null ? suiteRun.getResult().name() : null,
                testCaseRuns,
                suiteRun.getPassedTestCasesCount(),
                suiteRun.getFailedTestCasesCount(),
                suiteRun.getCreatedAt(),
                suiteRun.getStartedAt(),
                suiteRun.getCompletedAt()
        );
    }
}
