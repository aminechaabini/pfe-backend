package com.example.demo.core.presentation.rest.controller;

import com.example.demo.core.presentation.rest.dto.response.execution.TestCaseRunResponse;
import com.example.demo.core.presentation.rest.dto.response.execution.TestSuiteRunResponse;
import com.example.demo.core.presentation.rest.mapper.TestCaseRunResponseMapper;
import com.example.demo.core.presentation.rest.mapper.TestSuiteRunResponseMapper;
import com.example.demo.core.application.dto.execution.ExecuteTestCaseRequest;
import com.example.demo.core.application.dto.execution.ExecuteTestSuiteRequest;
import com.example.demo.core.application.service.TestExecutionService;
import com.example.demo.core.domain.run.TestCaseRun;
import com.example.demo.core.domain.run.TestSuiteRun;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Test Execution.
 *
 * Base path: /api/executions
 */
@RestController
@RequestMapping("/api/executions")
public class TestExecutionController {

    private final TestExecutionService testExecutionService;
    private final TestSuiteRunResponseMapper suiteRunMapper;
    private final TestCaseRunResponseMapper testCaseRunMapper;

    public TestExecutionController(
            TestExecutionService testExecutionService,
            TestSuiteRunResponseMapper suiteRunMapper,
            TestCaseRunResponseMapper testCaseRunMapper) {
        this.testExecutionService = testExecutionService;
        this.suiteRunMapper = suiteRunMapper;
        this.testCaseRunMapper = testCaseRunMapper;
    }

    /**
     * Execute test suite.
     *
     * POST /api/executions/suites
     */
    @PostMapping("/suites")
    public ResponseEntity<ExecutionStartedResponse> executeTestSuite(
            @RequestBody @Valid ExecuteTestSuiteRequest request) {
        Long runId = testExecutionService.executeTestSuite(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ExecutionStartedResponse(runId, "Test suite execution started"));
    }

    /**
     * Execute single test case.
     *
     * POST /api/executions/test-cases
     */
    @PostMapping("/test-cases")
    public ResponseEntity<ExecutionStartedResponse> executeTestCase(
            @RequestBody @Valid ExecuteTestCaseRequest request) {
        Long runId = testExecutionService.executeTestCase(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(new ExecutionStartedResponse(runId, "Test case execution started"));
    }

    /**
     * Get test suite run results.
     *
     * GET /api/executions/suites/{runId}
     */
    @GetMapping("/suites/{runId}")
    public ResponseEntity<TestSuiteRunResponse> getTestSuiteRun(@PathVariable Long runId) {
        TestSuiteRun run = testExecutionService.getTestSuiteRun(runId);
        return ResponseEntity.ok(suiteRunMapper.toResponse(run));
    }

    /**
     * Get test case run results (with full details for failure analysis).
     *
     * GET /api/executions/test-cases/{runId}
     */
    @GetMapping("/test-cases/{runId}")
    public ResponseEntity<TestCaseRunResponse> getTestCaseRun(@PathVariable Long runId) {
        TestCaseRun run = testExecutionService.getTestCaseRun(runId);
        return ResponseEntity.ok(testCaseRunMapper.toResponse(run));
    }

    /**
     * Get run history for a test suite.
     *
     * GET /api/suites/{suiteId}/runs
     */
    @GetMapping("/suites/{suiteId}/history")
    public ResponseEntity<List<TestSuiteRunResponse>> getRunHistory(
            @PathVariable Long suiteId,
            @RequestParam(defaultValue = "10") int limit) {
        List<TestSuiteRun> runs = testExecutionService.getRunHistory(suiteId, limit);
        return ResponseEntity.ok(suiteRunMapper.toResponseList(runs));
    }

    /**
     * Simple response for async execution start.
     */
    public record ExecutionStartedResponse(Long runId, String message) {
    }
}
