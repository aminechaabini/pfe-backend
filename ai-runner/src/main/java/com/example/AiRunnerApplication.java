package com.example;

import com.example.demo.common.context.*;
import com.example.demo.common.context.dto.analysis.FailureAnalysis;
import com.example.demo.common.context.dto.analysis.RestSpecUpdateAnalysis;
import com.example.demo.common.context.dto.analysis.SoapSpecUpdateAnalysis;
import com.example.demo.common.context.dto.e2e.CreateE2eTestRequest;
import com.example.demo.common.context.dto.e2e.E2eStepData;
import com.example.demo.common.context.dto.plan.E2eTestGenerationPlan;
import com.example.demo.common.context.dto.plan.PlannedStep;
import com.example.demo.common.context.dto.plan.PlannedTest;
import com.example.demo.common.context.dto.plan.RestTestGenerationPlan;
import com.example.demo.common.context.dto.plan.SoapTestGenerationPlan;
import com.example.demo.common.context.dto.spec2suite.suite.CreateRestTestSuiteRequest;
import com.example.demo.common.context.dto.spec2suite.suite.CreateSoapTestSuiteRequest;
import com.example.demo.common.context.dto.spec2suite.test.CreateRestApiTestRequest;
import com.example.demo.common.context.dto.spec2suite.test.CreateSoapApiTestRequest;
import com.example.demo.common.dto.ai.*;
import com.example.demo.common.ports.AIGenerationPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

/**
 * AI Runner application - demonstrates ALL AI test generation service capabilities.
 *
 * This application demonstrates:
 * 1. Test Planning (REST, SOAP, E2E)
 * 2. Test Suite Generation (REST, SOAP)
 * 3. E2E Workflow Generation
 * 4. Failure Analysis (REST, SOAP, E2E)
 * 5. Spec Update Analysis (REST, SOAP)
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo.ai", "com.example"})
public class AiRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiRunnerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AIGenerationPort aiGenerationPort) {
        return args -> {
            System.out.println("\n=== AI Test Generation Service Demo ===\n");

            try {
                // Run REST spec-to-suite generation demo (Planning + Generation)
                //testRestSpecToSuite(aiGenerationPort);

                // Run SOAP spec-to-suite generation demo (Planning + Generation)
                //testSoapSpecToSuite(aiGenerationPort);

                // Run E2E test planning demo
                testE2eTestPlanning(aiGenerationPort);

                // Run E2E workflow generation demo
                //testE2eWorkflowGeneration(aiGenerationPort);

                // Run failure analysis demos
//                testRestFailureAnalysis(aiGenerationPort);
//                testSoapFailureAnalysis(aiGenerationPort);
//                testE2eFailureAnalysis(aiGenerationPort);

                // Run spec update analysis demos
//                testRestSpecUpdateAnalysis(aiGenerationPort);
//                testSoapSpecUpdateAnalysis(aiGenerationPort);

                System.out.println("=== Demo completed successfully! ===\n");

            } catch (Exception e) {
                System.err.println("Error during AI generation: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Test REST API spec-to-suite generation (Planning + Generation).
     * Covers: TestGenerationPlanner.createRestTestPlan + SpecToSuiteGenerator.generateRestTestSuite
     */
    private void testRestSpecToSuite(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing REST Spec-to-Suite Generation ===\n");

        String openApiSpec = """
                openapi: 3.0.0
                info:
                  title: Pet Store API
                  version: 1.0.0
                paths:
                  /pets:
                    get:
                      summary: List all pets
                      parameters:
                        - name: limit
                          in: query
                          schema:
                            type: integer
                      responses:
                        '200':
                          description: A list of pets
                    post:
                      summary: Create a pet
                      requestBody:
                        required: true
                        content:
                          application/json:
                            schema:
                              type: object
                              required:
                                - name
                              properties:
                                name:
                                  type: string
                                tag:
                                  type: string
                      responses:
                        '201':
                          description: Pet created
                  /pets/{petId}:
                    get:
                      summary: Get a pet by ID
                      parameters:
                        - name: petId
                          in: path
                          required: true
                          schema:
                            type: string
                      responses:
                        '200':
                          description: Pet details
                        '404':
                          description: Pet not found
                """;

        // STEP 1: Plan Test Generation
        System.out.println("STEP 1: Planning test generation (preview)\n");

        TestPlanningContext planningContext = new TestPlanningContext(
                "REST",
                openApiSpec,
                true,  // includeHappyPath
                true,  // includeValidation
                false, // includeAuth
                true,  // includeErrors
                true   // includeEdgeCases
        );

        TestPlanResult planResult = aiGenerationPort.planTestGeneration(planningContext);

        if (planResult.isRest()) {
            RestTestGenerationPlan plan = planResult.restPlan();
            System.out.println("✓ Test Plan Created:");
            System.out.println("  Suite Name: " + plan.suiteName());
            System.out.println("  Description: " + plan.suiteDescription());
            System.out.println("  Total Tests: " + plan.totalTestCount());
            System.out.println("\n  Planned Tests:");

            for (PlannedTest test : plan.plannedTests()) {
                System.out.println("    - " + test.testName());
                System.out.println("      " + test.description());
            }

            // STEP 2: Generate Complete Test Suite
            System.out.println("\n\nSTEP 2: Generating complete test suite\n");

            RestSuiteGenerationContext suiteContext = new RestSuiteGenerationContext(
                    openApiSpec,
                    plan
            );

            GeneratedTestSuiteResult suiteResult = aiGenerationPort.generateTestSuiteFromSpec(suiteContext);

            if (suiteResult.isRest()) {
                CreateRestTestSuiteRequest suite = suiteResult.restTestSuite();
                System.out.println("✓ Test Suite Generated:");
                System.out.println("  Name: " + suite.name());
                System.out.println("  Description: " + suite.description());
                System.out.println("  Variables: " + suite.variables().size());
                System.out.println("  Test Count: " + suite.restApiTests().size());
                System.out.println("\n  Generated Tests:");

                for (CreateRestApiTestRequest test : suite.restApiTests()) {
                    System.out.println("\n    Test: " + test.name());
                    System.out.println("      Description: " + test.description());
                    System.out.println("      Method: " + test.request().method());
                    System.out.println("      URL: " + test.request().url());
                    System.out.println("      Assertions: " + test.assertions().size());
                }
            }
        }

        System.out.println("\n");
    }

    /**
     * Test SOAP spec-to-suite generation (Planning + Generation).
     * Covers: TestGenerationPlanner.createSoapTestPlan + SpecToSuiteGenerator.generateSoapTestSuite
     */
    private void testSoapSpecToSuite(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing SOAP Spec-to-Suite Generation ===\n");

        String wsdlSpec = """
                <?xml version="1.0" encoding="UTF-8"?>
                <definitions xmlns="http://schemas.xmlsoap.org/wsdl/"
                             xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"
                             xmlns:tns="http://example.com/calculator"
                             targetNamespace="http://example.com/calculator">
                  <message name="AddRequest">
                    <part name="a" type="xsd:int"/>
                    <part name="b" type="xsd:int"/>
                  </message>
                  <message name="AddResponse">
                    <part name="result" type="xsd:int"/>
                  </message>
                  <portType name="CalculatorPortType">
                    <operation name="Add">
                      <input message="tns:AddRequest"/>
                      <output message="tns:AddResponse"/>
                    </operation>
                  </portType>
                </definitions>
                """;

        // STEP 1: Plan Test Generation
        System.out.println("STEP 1: Planning SOAP test generation (preview)\n");

        TestPlanningContext planningContext = new TestPlanningContext(
                "SOAP",
                wsdlSpec,
                true,  // includeHappyPath
                true,  // includeValidation
                false, // includeAuth
                true,  // includeErrors
                true   // includeEdgeCases
        );

        TestPlanResult planResult = aiGenerationPort.planTestGeneration(planningContext);

        if (planResult.isSoap()) {
            SoapTestGenerationPlan plan = planResult.soapPlan();
            System.out.println("✓ SOAP Test Plan Created:");
            System.out.println("  Suite Name: " + plan.suiteName());
            System.out.println("  Description: " + plan.suiteDescription());
            System.out.println("  Total Tests: " + plan.totalTestCount());
            System.out.println("\n  Planned Tests:");

            for (PlannedTest test : plan.plannedTests()) {
                System.out.println("    - " + test.testName());
                System.out.println("      " + test.description());
            }

            // STEP 2: Generate Complete SOAP Test Suite
            System.out.println("\n\nSTEP 2: Generating complete SOAP test suite\n");

            SoapSuiteGenerationContext suiteContext = new SoapSuiteGenerationContext(
                    wsdlSpec,
                    plan
            );

            GeneratedTestSuiteResult suiteResult = aiGenerationPort.generateTestSuiteFromSpec(suiteContext);

            if (suiteResult.isSoap()) {
                CreateSoapTestSuiteRequest suite = suiteResult.soapTestSuite();
                System.out.println("✓ SOAP Test Suite Generated:");
                System.out.println("  Name: " + suite.name());
                System.out.println("  Description: " + suite.description());
                System.out.println("  Variables: " + suite.variables().size());
                System.out.println("  Test Count: " + suite.soapApiTests().size());
                System.out.println("\n  Generated Tests:");

                for (CreateSoapApiTestRequest test : suite.soapApiTests()) {
                    System.out.println("\n    Test: " + test.name());
                    System.out.println("      Description: " + test.description());
                    System.out.println("      URL: " + test.request().url());
                    System.out.println("      SOAP Action: " + test.request().soapAction());
                    System.out.println("      Assertions: " + test.assertions().size());
                }
            }
        }

        System.out.println("\n");
    }

    /**
     * Test E2E test planning.
     * Covers: TestGenerationPlanner.createE2eTestPlan
     */
    private void testE2eTestPlanning(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing E2E Test Planning ===\n");

        String endpointSequence = String.join("\n", List.of(
                "POST /auth/login - Authenticate user",
                "GET /products - Browse products",
                "POST /cart/items - Add product to cart",
                "POST /orders - Create order",
                "GET /orders/{orderId} - Verify order"
        ));

        TestPlanningContext planningContext = new TestPlanningContext(
                "E2E",
                endpointSequence,
                true,  // includeHappyPath
                true,  // includeValidation
                false, // includeAuth
                true,  // includeErrors
                false  // includeEdgeCases
        );

        TestPlanResult planResult = aiGenerationPort.planTestGeneration(planningContext);

        if (planResult.isE2e()) {
            E2eTestGenerationPlan plan = planResult.e2ePlan();
            System.out.println("✓ E2E Test Plan Created:");
            System.out.println("  Test Name: " + plan.testName());
            System.out.println("  Description: " + plan.testDescription());
            System.out.println("  Total Steps: " + plan.totalStepCount());
            System.out.println("\n  Planned Steps:");

            int stepNum = 1;
            for (PlannedStep step : plan.plannedSteps()) {
                System.out.println("    - Step " + stepNum++ + ": " + step.stepName());
                System.out.println("      " + step.description());
            }
        }

        System.out.println("\n");
    }

    /**
     * Test E2E workflow generation.
     * Covers: E2eWorkflowGenerator.generateWorkflow
     */
    private void testE2eWorkflowGeneration(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing E2E Workflow Generation ===\n");

        String endpointSequence = String.join("\n", List.of(
                "POST /auth/login - Authenticate user",
                "GET /products - Browse products",
                "POST /cart/items - Add product to cart",
                "POST /orders - Create order",
                "GET /orders/{orderId} - Verify order"
        ));

        E2eGenerationContext context = new E2eGenerationContext(
                "Complete Order Flow",
                "Test the complete flow from user login to order completion",
                endpointSequence,
                "{\"user\": {\"username\": \"string\", \"password\": \"string\"}, \"product\": {\"id\": \"string\", \"name\": \"string\"}, \"order\": {\"id\": \"string\", \"total\": \"number\"}}",
                "happy_path"
        );

        GeneratedE2eTestResult result = aiGenerationPort.generateE2eWorkflow(context);
        CreateE2eTestRequest e2eTest = result.e2eTest();

        System.out.println("✓ E2E Test Generated:");
        System.out.println("  Name: " + e2eTest.name());
        System.out.println("  Description: " + e2eTest.description());
        System.out.println("  Steps: " + e2eTest.steps().size());

        for (E2eStepData step : e2eTest.steps()) {
            System.out.println("\n    Step " + step.orderIndex() + ": " + step.name());
            System.out.println("      Description: " + step.description());
            System.out.println("      Type: " + step.stepType());
            if ("REST".equals(step.stepType()) && step.restRequest() != null) {
                System.out.println("      Method: " + step.restRequest().method());
                System.out.println("      URL: " + step.restRequest().url());
            }
            System.out.println("      Extractors: " + step.extractors().size());
            System.out.println("      Assertions: " + step.assertions().size());
        }

        System.out.println("\n");
    }

    /**
     * Test REST API failure analysis.
     * Covers: TestFailureAnalyzer.analyzeRestFailure
     */
    private void testRestFailureAnalysis(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing REST Failure Analysis ===\n");

        RestFailureAnalysisContext context = new RestFailureAnalysisContext(
                "Get User By ID",
                "GET",
                "/users/123",
                "",
                200,
                500,
                "{\"error\": \"Internal Server Error\", \"message\": \"Database connection failed\"}",
                "STATUS_EQUALS: expected 200 but got 500",
                true
        );

        FailureAnalysisResult result = aiGenerationPort.analyzeTestFailure(context);
        FailureAnalysis analysis = result.analysis();

        System.out.println("✓ Failure Analysis:");
        System.out.println("  Root Cause: " + analysis.rootCause());
        System.out.println("  Issue Type: " + analysis.issueType());
        System.out.println("  Recommendation: " + analysis.recommendation());

        System.out.println("\n");
    }

    /**
     * Test SOAP API failure analysis.
     * Covers: TestFailureAnalyzer.analyzeSoapFailure
     */
    private void testSoapFailureAnalysis(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing SOAP Failure Analysis ===\n");

        String soapEnvelope = """
                <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
                  <soap:Body>
                    <Add xmlns="http://example.com/calculator">
                      <a>5</a>
                      <b>invalid</b>
                    </Add>
                  </soap:Body>
                </soap:Envelope>
                """;

        SoapFailureAnalysisContext context = new SoapFailureAnalysisContext(
                "Calculator Add Operation",
                "http://example.com/calculator/service",
                soapEnvelope,
                "http://example.com/calculator/Add",
                200,
                500,
                "<soap:Fault><faultcode>soap:Server</faultcode><faultstring>Invalid input type for parameter b</faultstring></soap:Fault>",
                "STATUS_EQUALS: expected 200 but got 500",
                true
        );

        FailureAnalysisResult result = aiGenerationPort.analyzeTestFailure(context);
        FailureAnalysis analysis = result.analysis();

        System.out.println("✓ SOAP Failure Analysis:");
        System.out.println("  Root Cause: " + analysis.rootCause());
        System.out.println("  Issue Type: " + analysis.issueType());
        System.out.println("  Recommendation: " + analysis.recommendation());

        System.out.println("\n");
    }

    /**
     * Test E2E failure analysis.
     * Covers: TestFailureAnalyzer.analyzeE2eFailure
     */
    private void testE2eFailureAnalysis(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing E2E Failure Analysis ===\n");

        E2eFailureAnalysisContext context = new E2eFailureAnalysisContext(
                "Complete Order Flow",
                3,
                "Create order",
                "REST",
                "POST",
                "/orders",
                "{\"userId\": 123, \"items\": [{\"productId\": \"abc\", \"quantity\": 2}]}",
                201,
                400,
                "{\"error\": \"Invalid product ID\"}",
                "STATUS_EQUALS: expected 201 but got 400",
                "{\"authToken\": \"xyz123\", \"userId\": 123, \"productId\": \"abc\"}",
                true
        );

        FailureAnalysisResult result = aiGenerationPort.analyzeTestFailure(context);
        FailureAnalysis analysis = result.analysis();

        System.out.println("✓ E2E Failure Analysis:");
        System.out.println("  Root Cause: " + analysis.rootCause());
        System.out.println("  Issue Type: " + analysis.issueType());
        System.out.println("  Recommendation: " + analysis.recommendation());

        System.out.println("\n");
    }

    /**
     * Test REST spec update analysis.
     * Covers: SpecUpdateAnalyzer.analyzeRestSpecUpdate
     */
    private void testRestSpecUpdateAnalysis(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing REST Spec Update Analysis ===\n");

        String oldSpec = """
                openapi: 3.0.0
                paths:
                  /users:
                    get:
                      responses:
                        '200':
                          description: List users
                """;

        String newSpec = """
                openapi: 3.0.0
                paths:
                  /users:
                    get:
                      parameters:
                        - name: role
                          in: query
                          required: true
                          schema:
                            type: string
                      responses:
                        '200':
                          description: List users by role
                """;

        List<CreateRestApiTestRequest> existingTests = List.of();

        RestSpecUpdateAnalysisContext context = new RestSpecUpdateAnalysisContext(
                oldSpec,
                newSpec,
                existingTests
        );

        SpecUpdateAnalysisResult result = aiGenerationPort.analyzeSpecUpdate(context);
        RestSpecUpdateAnalysis analysis = result.restAnalysis();

        System.out.println("✓ Spec Update Analysis:");
        System.out.println("  Recommendations: " + analysis.recommendations());
        System.out.println("\n  Breaking Changes: " + analysis.breakingChanges().size());
        analysis.breakingChanges().forEach(change -> {
            System.out.println("    - " + change.description());
            System.out.println("      Endpoint: " + change.endpoint());
            System.out.println("      Change Type: " + change.changeType());
        });
        System.out.println("\n  New Endpoints: " + analysis.newEndpoints().size());
        analysis.newEndpoints().forEach(endpoint ->
                System.out.println("    + " + endpoint)
        );
        System.out.println("\n  Removed Endpoints: " + analysis.removedEndpoints().size());
        analysis.removedEndpoints().forEach(endpoint ->
                System.out.println("    - " + endpoint)
        );
        System.out.println("\n  Affected Tests: " + analysis.affectedTests().size());
        analysis.affectedTests().forEach(test -> {
            System.out.println("    - Test: " + test.testName());
            System.out.println("      Action: " + test.suggestedAction());
        });

        System.out.println("\n");
    }

    /**
     * Test SOAP spec update analysis.
     * Covers: SpecUpdateAnalyzer.analyzeSoapSpecUpdate
     */
    private void testSoapSpecUpdateAnalysis(AIGenerationPort aiGenerationPort) throws Exception {
        System.out.println("=== Testing SOAP Spec Update Analysis ===\n");

        String oldSpec = """
                <?xml version="1.0"?>
                <definitions xmlns="http://schemas.xmlsoap.org/wsdl/"
                             targetNamespace="http://example.com/calculator">
                  <portType name="CalculatorPortType">
                    <operation name="Add">
                      <input message="tns:AddRequest"/>
                      <output message="tns:AddResponse"/>
                    </operation>
                  </portType>
                </definitions>
                """;

        String newSpec = """
                <?xml version="1.0"?>
                <definitions xmlns="http://schemas.xmlsoap.org/wsdl/"
                             targetNamespace="http://example.com/calculator">
                  <portType name="CalculatorPortType">
                    <operation name="Add">
                      <input message="tns:AddRequest"/>
                      <output message="tns:AddResponse"/>
                    </operation>
                    <operation name="Subtract">
                      <input message="tns:SubtractRequest"/>
                      <output message="tns:SubtractResponse"/>
                    </operation>
                  </portType>
                </definitions>
                """;

        List<CreateSoapApiTestRequest> existingTests = List.of();

        SoapSpecUpdateAnalysisContext context = new SoapSpecUpdateAnalysisContext(
                oldSpec,
                newSpec,
                existingTests
        );

        SpecUpdateAnalysisResult result = aiGenerationPort.analyzeSpecUpdate(context);
        SoapSpecUpdateAnalysis analysis = result.soapAnalysis();

        System.out.println("✓ SOAP Spec Update Analysis:");
        System.out.println("  Recommendations: " + analysis.recommendations());
        System.out.println("\n  Breaking Changes: " + analysis.breakingChanges().size());
        analysis.breakingChanges().forEach(change -> {
            System.out.println("    - " + change.description());
            System.out.println("      Operation: " + change.endpoint());
            System.out.println("      Change Type: " + change.changeType());
        });
        System.out.println("\n  New Operations: " + analysis.newEndpoints().size());
        analysis.newEndpoints().forEach(operation ->
                System.out.println("    + " + operation)
        );
        System.out.println("\n  Removed Operations: " + analysis.removedEndpoints().size());
        analysis.removedEndpoints().forEach(operation ->
                System.out.println("    - " + operation)
        );
        System.out.println("\n  Affected Tests: " + analysis.affectedTests().size());
        analysis.affectedTests().forEach(test -> {
            System.out.println("    - Test: " + test.testName());
            System.out.println("      Action: " + test.suggestedAction());
        });

        System.out.println("\n");
    }
}
