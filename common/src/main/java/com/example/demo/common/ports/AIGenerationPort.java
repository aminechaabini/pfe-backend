package com.example.demo.common.ports;

import com.example.demo.common.context.*;
import com.example.demo.common.dto.ai.*;

/**
 * Port interface for AI-powered test generation.
 *
 * This interface defines the contract for generating tests using AI/LLM.
 * The core module depends on this abstraction, not on the concrete implementation.
 */
public interface AIGenerationPort {

    /**
     * Generate test suite from API specification.
     *
     * @param context Context containing spec, endpoint, and generation options
     * @return Generated test suite with test cases
     */
    GeneratedTestSuiteResult generateTestSuiteFromSpec(RestSuiteGenerationContext context);

    /**
     * Generate test suite from SOAP specification.
     *
     * @param context Context containing WSDL spec and generation options
     * @return Generated test suite with test cases
     */
    GeneratedTestSuiteResult generateTestSuiteFromSpec(SoapSuiteGenerationContext context);

    /**
     * Generate E2E workflow from user story and available endpoints.
     *
     * @param context Context containing user story and endpoints
     * @return Generated E2E test with steps
     */
    GeneratedE2eTestResult generateE2eWorkflow(E2eGenerationContext context);

    /**
     * Analyze test failure and suggest fixes.
     *
     * @param context Context containing test case run details and failure info
     * @return Analysis with suggested fixes
     */
    FailureAnalysisResult analyzeTestFailure(RestFailureAnalysisContext context);

    /**
     * Analyze test failure for SOAP tests.
     *
     * @param context Context containing SOAP test failure details
     * @return Analysis with suggested fixes
     */
    FailureAnalysisResult analyzeTestFailure(SoapFailureAnalysisContext context);

    /**
     * Analyze test failure for E2E tests.
     *
     * @param context Context containing E2E test failure details
     * @return Analysis with suggested fixes
     */
    FailureAnalysisResult analyzeTestFailure(E2eFailureAnalysisContext context);

    /**
     * Analyze spec changes and suggest test updates.
     *
     * @param context Context containing old and new spec
     * @return Analysis of changes and suggested test updates
     */
    SpecUpdateAnalysisResult analyzeSpecUpdate(RestSpecUpdateAnalysisContext context);

    /**
     * Analyze SOAP spec changes and suggest test updates.
     *
     * @param context Context containing old and new WSDL spec
     * @return Analysis of changes and suggested test updates
     */
    SpecUpdateAnalysisResult analyzeSpecUpdate(SoapSpecUpdateAnalysisContext context);

    /**
     * Generate test planning preview (what tests will be generated).
     *
     * @param context Context containing spec and planning options
     * @return Plan showing what tests would be generated
     */
    TestPlanResult planTestGeneration(TestPlanningContext context);
}
