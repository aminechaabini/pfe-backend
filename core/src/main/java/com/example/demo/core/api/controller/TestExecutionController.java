package com.example.demo.core.api.controller;

import com.example.demo.core.application.dto.execution.ExecuteTestCaseRequest;
import com.example.demo.core.application.dto.execution.ExecuteTestSuiteRequest;
import com.example.demo.core.application.service.TestExecutionService;
import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestSuiteRun;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API controller for test execution.
 *
 * <p>Endpoints:
 * - POST /api/test-suites/{id}/execute - Execute a test suite
 * - POST /api/test-cases/{id}/execute - Execute a single test case
 * - GET /api/test-suite-runs/{id} - Get test suite run results
 * - GET /api/test-case-runs/{id} - Get test case run results
 * - GET /api/test-suites/{id}/runs - Get run history for a test suite
 */
@RestController
@RequestMapping("/api")
public class TestExecutionController {

    private final TestExecutionService testExecutionService;

    public TestExecutionController(TestExecutionService testExecutionService) {
        this.testExecutionService = testExecutionService;
    }

    /**
     * Execute a test suite.
     *
     * POST /api/test-suites/{id}/execute
     *
     * Request body:
     * {
     *   "testSuiteId": 1,
     *   "environmentVariables": {
     *     "baseUrl": "https://api.example.com",
     *     "apiKey": "secret123"
     *   }
     * }
     *
     * Response: { "runId": 123 }
     *
     * @param id Test suite ID
     * @param request Execution request with environment variables
     * @return Run ID
     */
    @PostMapping("/test-suites/{id}/execute")
    public ResponseEntity<Map<String, Long>> executeTestSuite(
        @PathVariable Long id,
        @Valid @RequestBody ExecuteTestSuiteRequest request
    ) {
        // Ensure the path variable matches the request body
        if (!id.equals(request.testSuiteId())) {
            return ResponseEntity.badRequest().build();
        }

        Long runId = testExecutionService.executeTestSuite(request);
        return ResponseEntity.ok(Map.of("runId", runId));
    }

    /**
     * Execute a single test case.
     *
     * POST /api/test-cases/{id}/execute
     *
     * Request body:
     * {
     *   "testCaseId": 1,
     *   "environmentVariables": {
     *     "baseUrl": "https://api.example.com"
     *   }
     * }
     *
     * Response: { "runId": 456 }
     *
     * @param id Test case ID
     * @param request Execution request with environment variables
     * @return Run ID
     */
    @PostMapping("/test-cases/{id}/execute")
    public ResponseEntity<Map<String, Long>> executeTestCase(
        @PathVariable Long id,
        @Valid @RequestBody ExecuteTestCaseRequest request
    ) {
        // Ensure the path variable matches the request body
        if (!id.equals(request.testCaseId())) {
            return ResponseEntity.badRequest().build();
        }

        Long runId = testExecutionService.executeTestCase(request);
        return ResponseEntity.ok(Map.of("runId", runId));
    }

    /**
     * Get test suite run results.
     *
     * GET /api/test-suite-runs/{id}
     *
     * Response includes:
     * - Run status (NOT_STARTED, IN_PROGRESS, COMPLETED)
     * - Run result (SUCCESS, FAILURE)
     * - All test case runs with their results
     * - Timestamps (created, started, completed)
     *
     * @param id Run ID
     * @return Test suite run
     */
    @GetMapping("/test-suite-runs/{id}")
    public ResponseEntity<TestSuiteRun> getTestSuiteRun(@PathVariable Long id) {
        TestSuiteRun run = testExecutionService.getTestSuiteRun(id);
        return ResponseEntity.ok(run);
    }

    /**
     * Get test case run results.
     *
     * GET /api/test-case-runs/{id}
     *
     * Response includes:
     * - Run status and result
     * - All assertion results
     * - HTTP request and response details
     * - Timestamps
     *
     * @param id Run ID
     * @return Test case run
     */
    @GetMapping("/test-case-runs/{id}")
    public ResponseEntity<TestCaseRun> getTestCaseRun(@PathVariable Long id) {
        TestCaseRun run = testExecutionService.getTestCaseRun(id);
        return ResponseEntity.ok(run);
    }

    /**
     * Get run history for a test suite.
     *
     * GET /api/test-suites/{id}/runs?limit=10
     *
     * Returns recent runs ordered by start time descending.
     *
     * @param id Test suite ID
     * @param limit Maximum number of runs to return (default: 10)
     * @return List of test suite runs
     */
    @GetMapping("/test-suites/{id}/runs")
    public ResponseEntity<List<TestSuiteRun>> getRunHistory(
        @PathVariable Long id,
        @RequestParam(defaultValue = "10") int limit
    ) {
        List<TestSuiteRun> runs = testExecutionService.getRunHistory(id, limit);
        return ResponseEntity.ok(runs);
    }
}
