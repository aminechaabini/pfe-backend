package com.example.demo.core.api.controller;

import com.example.demo.common.context.*;
import com.example.demo.common.dto.ai.*;
import com.example.demo.core.application.service.AIGenerationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for AI-powered test generation.
 *
 * <p>Endpoints:
 * - POST /api/ai/generate/rest-suite - Generate REST test suite from OpenAPI spec
 * - POST /api/ai/generate/soap-suite - Generate SOAP test suite from WSDL
 * - POST /api/ai/generate/e2e-workflow - Generate E2E workflow from user story
 * - POST /api/ai/analyze/rest-failure - Analyze REST test failure
 * - POST /api/ai/analyze/soap-failure - Analyze SOAP test failure
 * - POST /api/ai/analyze/e2e-failure - Analyze E2E test failure
 * - POST /api/ai/analyze/rest-spec-update - Analyze REST spec changes
 * - POST /api/ai/analyze/soap-spec-update - Analyze SOAP spec changes
 * - POST /api/ai/plan - Generate test planning preview
 */
@RestController
@RequestMapping("/api/ai")
public class AIGenerationController {

    private final AIGenerationService aiGenerationService;

    public AIGenerationController(AIGenerationService aiGenerationService) {
        this.aiGenerationService = aiGenerationService;
    }

    /**
     * Generate REST test suite from OpenAPI specification.
     *
     * POST /api/ai/generate/rest-suite
     *
     * Request body:
     * {
     *   "specContent": "openapi: 3.0.0...",
     *   "approvedPlan": {...}
     * }
     *
     * Response: Generated test suite
     *
     * @param context REST suite generation context
     * @return Generated test suite
     */
    @PostMapping("/generate/rest-suite")
    public ResponseEntity<GeneratedTestSuiteResult> generateRestTestSuite(
        @RequestBody RestSuiteGenerationContext context
    ) {
        GeneratedTestSuiteResult result = aiGenerationService.generateTestSuiteFromRestSpec(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate SOAP test suite from WSDL specification.
     *
     * POST /api/ai/generate/soap-suite
     *
     * Request body:
     * {
     *   "wsdlContent": "<?xml version...",
     *   "approvedPlan": {...}
     * }
     *
     * Response: Generated test suite
     *
     * @param context SOAP suite generation context
     * @return Generated test suite
     */
    @PostMapping("/generate/soap-suite")
    public ResponseEntity<GeneratedTestSuiteResult> generateSoapTestSuite(
        @RequestBody SoapSuiteGenerationContext context
    ) {
        GeneratedTestSuiteResult result = aiGenerationService.generateTestSuiteFromSoapSpec(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate E2E workflow from user story.
     *
     * POST /api/ai/generate/e2e-workflow
     *
     * Request body:
     * {
     *   "userStory": "As a user, I want to...",
     *   "availableEndpoints": [...]
     * }
     *
     * Response: Generated E2E test
     *
     * @param context E2E generation context
     * @return Generated E2E test
     */
    @PostMapping("/generate/e2e-workflow")
    public ResponseEntity<GeneratedE2eTestResult> generateE2eWorkflow(
        @RequestBody E2eGenerationContext context
    ) {
        GeneratedE2eTestResult result = aiGenerationService.generateE2eWorkflow(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze REST test failure and suggest fixes.
     *
     * POST /api/ai/analyze/rest-failure
     *
     * Request body:
     * {
     *   "testCaseRun": {...},
     *   "specContent": "openapi: 3.0.0..."
     * }
     *
     * Response: Failure analysis with suggested fixes
     *
     * @param context REST failure analysis context
     * @return Failure analysis result
     */
    @PostMapping("/analyze/rest-failure")
    public ResponseEntity<FailureAnalysisResult> analyzeRestFailure(
        @RequestBody RestFailureAnalysisContext context
    ) {
        FailureAnalysisResult result = aiGenerationService.analyzeRestTestFailure(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze SOAP test failure and suggest fixes.
     *
     * POST /api/ai/analyze/soap-failure
     *
     * @param context SOAP failure analysis context
     * @return Failure analysis result
     */
    @PostMapping("/analyze/soap-failure")
    public ResponseEntity<FailureAnalysisResult> analyzeSoapFailure(
        @RequestBody SoapFailureAnalysisContext context
    ) {
        FailureAnalysisResult result = aiGenerationService.analyzeSoapTestFailure(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze E2E test failure and suggest fixes.
     *
     * POST /api/ai/analyze/e2e-failure
     *
     * @param context E2E failure analysis context
     * @return Failure analysis result
     */
    @PostMapping("/analyze/e2e-failure")
    public ResponseEntity<FailureAnalysisResult> analyzeE2eFailure(
        @RequestBody E2eFailureAnalysisContext context
    ) {
        FailureAnalysisResult result = aiGenerationService.analyzeE2eTestFailure(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze REST spec changes and suggest test updates.
     *
     * POST /api/ai/analyze/rest-spec-update
     *
     * Request body:
     * {
     *   "oldSpec": "openapi: 3.0.0...",
     *   "newSpec": "openapi: 3.0.0...",
     *   "existingTests": [...]
     * }
     *
     * Response: Analysis of changes and suggested updates
     *
     * @param context REST spec update analysis context
     * @return Spec update analysis result
     */
    @PostMapping("/analyze/rest-spec-update")
    public ResponseEntity<SpecUpdateAnalysisResult> analyzeRestSpecUpdate(
        @RequestBody RestSpecUpdateAnalysisContext context
    ) {
        SpecUpdateAnalysisResult result = aiGenerationService.analyzeRestSpecUpdate(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Analyze SOAP spec changes and suggest test updates.
     *
     * POST /api/ai/analyze/soap-spec-update
     *
     * @param context SOAP spec update analysis context
     * @return Spec update analysis result
     */
    @PostMapping("/analyze/soap-spec-update")
    public ResponseEntity<SpecUpdateAnalysisResult> analyzeSoapSpecUpdate(
        @RequestBody SoapSpecUpdateAnalysisContext context
    ) {
        SpecUpdateAnalysisResult result = aiGenerationService.analyzeSoapSpecUpdate(context);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate test planning preview.
     *
     * POST /api/ai/plan
     *
     * Request body:
     * {
     *   "specContent": "openapi: 3.0.0...",
     *   "planningOptions": {...}
     * }
     *
     * Response: Test plan showing what tests would be generated
     *
     * @param context Test planning context
     * @return Test plan result
     */
    @PostMapping("/plan")
    public ResponseEntity<TestPlanResult> planTestGeneration(
        @RequestBody TestPlanningContext context
    ) {
        TestPlanResult result = aiGenerationService.planTestGeneration(context);
        return ResponseEntity.ok(result);
    }
}
