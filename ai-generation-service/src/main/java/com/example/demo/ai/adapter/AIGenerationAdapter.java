package com.example.demo.ai.adapter;

import com.example.demo.ai.ai_services.*;
import com.example.demo.common.context.*;
import com.example.demo.common.context.dto.plan.RestTestGenerationPlan;
import com.example.demo.common.context.dto.plan.SoapTestGenerationPlan;
import com.example.demo.common.ports.AIGenerationPort;
import com.example.demo.common.dto.ai.*;
import com.example.demo.common.context.dto.analysis.FailureAnalysis;
import com.example.demo.common.context.dto.analysis.RestSpecUpdateAnalysis;
import com.example.demo.common.context.dto.analysis.SoapSpecUpdateAnalysis;
import com.example.demo.common.context.dto.e2e.CreateE2eTestRequest;
import com.example.demo.common.context.dto.spec2suite.suite.CreateRestTestSuiteRequest;
import com.example.demo.common.context.dto.spec2suite.suite.CreateSoapTestSuiteRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * Adapter that implements the AIGenerationPort interface using LangChain4j AI services.
 * This is the bridge between the core module and the AI generation service.
 */
@Component
public class AIGenerationAdapter implements AIGenerationPort {

    private final TestGenerationPlanner planner;
    private final SpecToSuiteGenerator suiteGenerator;
    private final E2eWorkflowGenerator workflowGenerator;
    private final TestFailureAnalyzer failureAnalyzer;
    private final SpecUpdateAnalyzer updateAnalyzer;
    private final ObjectMapper objectMapper;

    public AIGenerationAdapter(
            TestGenerationPlanner planner,
            SpecToSuiteGenerator suiteGenerator,
            E2eWorkflowGenerator workflowGenerator,
            TestFailureAnalyzer failureAnalyzer,
            SpecUpdateAnalyzer updateAnalyzer) {
        this.planner = planner;
        this.suiteGenerator = suiteGenerator;
        this.workflowGenerator = workflowGenerator;
        this.failureAnalyzer = failureAnalyzer;
        this.updateAnalyzer = updateAnalyzer;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public GeneratedTestSuiteResult generateTestSuiteFromSpec(RestSuiteGenerationContext context) {
        try {
            String approvedPlanJson = objectMapper.writeValueAsString(context.approvedPlan());
            CreateRestTestSuiteRequest suite = suiteGenerator.generateRestTestSuite(
                    context.specContent(),
                    approvedPlanJson
            );
            return GeneratedTestSuiteResult.forRest(suite);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate REST test suite", e);
        }
    }

    @Override
    public GeneratedTestSuiteResult generateTestSuiteFromSpec(SoapSuiteGenerationContext context) {
        try {
            String approvedPlanJson = objectMapper.writeValueAsString(context.approvedPlan());
            CreateSoapTestSuiteRequest suite = suiteGenerator.generateSoapTestSuite(
                    context.specContent(),
                    approvedPlanJson
            );
            return GeneratedTestSuiteResult.forSoap(suite);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate SOAP test suite", e);
        }
    }

    @Override
    public GeneratedE2eTestResult generateE2eWorkflow(E2eGenerationContext context) {
        CreateE2eTestRequest e2eTest = workflowGenerator.generateWorkflow(
                context.workflowName(),
                context.workflowDescription(),
                context.endpointSequence(),
                context.schemas(),
                context.scenarioType()
        );
        return new GeneratedE2eTestResult(e2eTest);
    }

    @Override
    public FailureAnalysisResult analyzeTestFailure(RestFailureAnalysisContext context) {
        FailureAnalysis analysis = failureAnalyzer.analyzeRestFailure(
                context.testName(),
                context.method(),
                context.url(),
                context.requestBody(),
                String.valueOf(context.expectedStatus()),
                String.valueOf(context.actualStatus()),
                context.actualBody(),
                context.failedAssertion(),
                String.valueOf(context.wasPassingBefore())
        );
        return new FailureAnalysisResult(analysis);
    }

    @Override
    public FailureAnalysisResult analyzeTestFailure(SoapFailureAnalysisContext context) {
        FailureAnalysis analysis = failureAnalyzer.analyzeSoapFailure(
                context.testName(),
                context.url(),
                context.soapEnvelope(),
                context.soapAction(),
                String.valueOf(context.expectedStatus()),
                String.valueOf(context.actualStatus()),
                context.actualBody(),
                context.failedAssertion(),
                String.valueOf(context.wasPassingBefore())
        );
        return new FailureAnalysisResult(analysis);
    }

    @Override
    public FailureAnalysisResult analyzeTestFailure(E2eFailureAnalysisContext context) {
        FailureAnalysis analysis = failureAnalyzer.analyzeE2eFailure(
                context.testName(),
                String.valueOf(context.failedStepIndex()),
                context.failedStepName(),
                context.stepType(),
                context.method(),
                context.url(),
                context.request(),
                String.valueOf(context.expectedStatus()),
                String.valueOf(context.actualStatus()),
                context.actualBody(),
                context.failedAssertion(),
                context.extractedVariables(),
                String.valueOf(context.wasPassingBefore())
        );
        return new FailureAnalysisResult(analysis);
    }

    @Override
    public SpecUpdateAnalysisResult analyzeSpecUpdate(RestSpecUpdateAnalysisContext context) {
        try {
            String existingTestsJson = objectMapper.writeValueAsString(context.existingTests());
            RestSpecUpdateAnalysis analysis = updateAnalyzer.analyzeRestSpecUpdate(
                    context.oldSpecContent(),
                    context.newSpecContent(),
                    existingTestsJson
            );
            return SpecUpdateAnalysisResult.forRest(analysis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze REST spec update", e);
        }
    }

    @Override
    public SpecUpdateAnalysisResult analyzeSpecUpdate(SoapSpecUpdateAnalysisContext context) {
        try {
            String existingTestsJson = objectMapper.writeValueAsString(context.existingTests());
            SoapSpecUpdateAnalysis analysis = updateAnalyzer.analyzeSoapSpecUpdate(
                    context.oldSpecContent(),
                    context.newSpecContent(),
                    existingTestsJson
            );
            return SpecUpdateAnalysisResult.forSoap(analysis);
        } catch (Exception e) {
            throw new RuntimeException("Failed to analyze SOAP spec update", e);
        }
    }

    @Override
    public TestPlanResult planTestGeneration(TestPlanningContext context) {
        // Determine which type of spec and call the appropriate planner
        if ("REST".equals(context.specType())) {
            RestTestGenerationPlan plan = planner.createRestTestPlan(
                    context.specType(),
                    context.specContent(),
                    String.valueOf(context.includeHappyPath()),
                    String.valueOf(context.includeValidation()),
                    String.valueOf(context.includeAuth()),
                    String.valueOf(context.includeErrors()),
                    String.valueOf(context.includeEdgeCases())
            );
            return TestPlanResult.forRest(plan);
        } else {
            SoapTestGenerationPlan plan = planner.createSoapTestPlan(
                    context.specType(),
                    context.specContent(),
                    String.valueOf(context.includeHappyPath()),
                    String.valueOf(context.includeValidation()),
                    String.valueOf(context.includeAuth()),
                    String.valueOf(context.includeErrors()),
                    String.valueOf(context.includeEdgeCases())
            );
            return TestPlanResult.forSoap(plan);
        }
    }
}
