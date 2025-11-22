package com.example.demo.core.presentation.rest.controller;

import com.example.demo.core.presentation.rest.dto.response.suite.TestSuiteDetailResponse;
import com.example.demo.core.presentation.rest.dto.response.suite.TestSuiteResponse;
import com.example.demo.core.presentation.rest.mapper.TestSuiteResponseMapper;
import com.example.demo.core.application.dto.project.SetVariableRequest;
import com.example.demo.core.application.dto.suite.CreateTestSuiteRequest;
import com.example.demo.core.application.dto.suite.UpdateTestSuiteRequest;
import com.example.demo.core.application.service.TestSuiteService;
import com.example.demo.core.domain.test.test_suite.TestSuite;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Test Suite management.
 *
 * Base paths:
 * - /api/projects/{projectId}/suites - Project-scoped operations
 * - /api/suites/{id} - Suite-scoped operations
 */
@RestController
public class TestSuiteController {

    private final TestSuiteService testSuiteService;
    private final TestSuiteResponseMapper mapper;

    public TestSuiteController(TestSuiteService testSuiteService, TestSuiteResponseMapper mapper) {
        this.testSuiteService = testSuiteService;
        this.mapper = mapper;
    }

    /**
     * Create test suite.
     *
     * POST /api/projects/{projectId}/suites
     */
    @PostMapping("/api/projects/{projectId}/suites")
    public ResponseEntity<TestSuiteResponse> createTestSuite(
            @PathVariable Long projectId,
            @RequestBody @Valid CreateTestSuiteRequest request) {
        TestSuite testSuite = testSuiteService.createTestSuite(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(testSuite));
    }

    /**
     * Get test suite by ID (summary).
     *
     * GET /api/suites/{id}
     */
    @GetMapping("/api/suites/{id}")
    public ResponseEntity<TestSuiteResponse> getTestSuite(@PathVariable Long id) {
        TestSuite testSuite = testSuiteService.getTestSuite(id);
        return ResponseEntity.ok(mapper.toResponse(testSuite));
    }

    /**
     * Get test suite by ID with test cases (detail).
     *
     * GET /api/suites/{id}/detail
     */
    @GetMapping("/api/suites/{id}/detail")
    public ResponseEntity<TestSuiteDetailResponse> getTestSuiteDetail(@PathVariable Long id) {
        TestSuite testSuite = testSuiteService.getTestSuiteWithTestCases(id);
        return ResponseEntity.ok(mapper.toDetailResponse(testSuite));
    }

    /**
     * Get all test suites for a project.
     *
     * GET /api/projects/{projectId}/suites
     */
    @GetMapping("/api/projects/{projectId}/suites")
    public ResponseEntity<List<TestSuiteResponse>> getProjectTestSuites(@PathVariable Long projectId) {
        List<TestSuite> testSuites = testSuiteService.getProjectTestSuites(projectId);
        return ResponseEntity.ok(mapper.toResponseList(testSuites));
    }

    /**
     * Update test suite.
     *
     * PUT /api/suites/{id}
     */
    @PutMapping("/api/suites/{id}")
    public ResponseEntity<TestSuiteResponse> updateTestSuite(
            @PathVariable Long id,
            @RequestBody @Valid UpdateTestSuiteRequest request) {
        TestSuite testSuite = testSuiteService.updateTestSuite(id, request);
        return ResponseEntity.ok(mapper.toResponse(testSuite));
    }

    /**
     * Delete test suite.
     *
     * DELETE /api/suites/{id}
     */
    @DeleteMapping("/api/suites/{id}")
    public ResponseEntity<Void> deleteTestSuite(@PathVariable Long id) {
        testSuiteService.deleteTestSuite(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Remove test case from suite.
     *
     * DELETE /api/suites/{suiteId}/test-cases/{testCaseId}
     */
    @DeleteMapping("/api/suites/{suiteId}/test-cases/{testCaseId}")
    public ResponseEntity<TestSuiteResponse> removeTestCase(
            @PathVariable Long suiteId,
            @PathVariable Long testCaseId) {
        TestSuite testSuite = testSuiteService.removeTestCase(suiteId, testCaseId);
        return ResponseEntity.ok(mapper.toResponse(testSuite));
    }

    /**
     * Set suite variable.
     *
     * POST /api/suites/{id}/variables
     */
    @PostMapping("/api/suites/{id}/variables")
    public ResponseEntity<TestSuiteResponse> setVariable(
            @PathVariable Long id,
            @RequestBody @Valid SetVariableRequest request) {
        TestSuite testSuite = testSuiteService.setVariable(id, request);
        return ResponseEntity.ok(mapper.toResponse(testSuite));
    }

    /**
     * Remove suite variable.
     *
     * DELETE /api/suites/{id}/variables/{name}
     */
    @DeleteMapping("/api/suites/{id}/variables/{name}")
    public ResponseEntity<TestSuiteResponse> removeVariable(
            @PathVariable Long id,
            @PathVariable String name) {
        TestSuite testSuite = testSuiteService.removeVariable(id, name);
        return ResponseEntity.ok(mapper.toResponse(testSuite));
    }
}
