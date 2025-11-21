package com.example.demo.core.api;

import com.example.demo.core.app.service.ReportingService;
import com.example.demo.core.app.service.ReportingService.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API for Reports and Metrics.
 *
 * Endpoints:
 * - GET /api/projects/{id}/metrics           - Get project metrics
 * - GET /api/tests/{id}/metrics              - Get test metrics
 * - GET /api/suite-runs/{id}/report          - Get suite execution report
 * - GET /api/reports/top-failing-tests       - Get top failing tests
 */
@RestController
@RequestMapping("/api")
public class ReportingController {

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    // ========================================================================
    // METRICS ENDPOINTS
    // ========================================================================

    /**
     * Get project metrics.
     *
     * GET /api/projects/{id}/metrics
     */
    @GetMapping("/projects/{id}/metrics")
    public ResponseEntity<ProjectMetrics> getProjectMetrics(@PathVariable Long id) {
        ProjectMetrics metrics = reportingService.getProjectMetrics(id);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Get test metrics.
     *
     * GET /api/tests/{id}/metrics
     */
    @GetMapping("/tests/{id}/metrics")
    public ResponseEntity<TestMetrics> getTestMetrics(@PathVariable Long id) {
        TestMetrics metrics = reportingService.getTestMetrics(id);
        return ResponseEntity.ok(metrics);
    }

    // ========================================================================
    // REPORT ENDPOINTS
    // ========================================================================

    /**
     * Get suite execution report.
     *
     * GET /api/suite-runs/{id}/report
     */
    @GetMapping("/suite-runs/{id}/report")
    public ResponseEntity<SuiteExecutionReport> getSuiteRunReport(@PathVariable Long id) {
        SuiteExecutionReport report = reportingService.getSuiteRunReport(id);
        return ResponseEntity.ok(report);
    }

    /**
     * Get top failing tests.
     *
     * GET /api/reports/top-failing-tests?limit=10
     */
    @GetMapping("/reports/top-failing-tests")
    public ResponseEntity<List<TestFailureSummary>> getTopFailingTests(
            @RequestParam(defaultValue = "10") int limit) {

        List<TestFailureSummary> failingTests = reportingService.getTopFailingTests(limit);
        return ResponseEntity.ok(failingTests);
    }
}
