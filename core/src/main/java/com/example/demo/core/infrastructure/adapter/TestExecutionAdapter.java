package com.example.demo.core.infrastructure.adapter;

import com.example.demo.core.application.ports.TestExecutionPort;
import com.example.demo.core.domain.run.*;
import com.example.demo.core.domain.test.TestCase;
import com.example.demo.core.domain.test.api.RestApiTest;
import com.example.demo.core.domain.test.api.SoapApiTest;
import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.e2e.E2eStep;
import com.example.demo.core.domain.test.e2e.E2eTest;
import com.example.demo.core.domain.test.request.HttpRequest;
import com.example.demo.core.domain.test.request.RestRequest;
import com.example.demo.core.domain.test.request.SoapRequest;
import com.example.demo.core.domain.test.request.body.*;
import com.example.demo.core.domain.test.test_suite.TestSuite;
import com.example.demo.shared.request.*;
import com.example.demo.shared.result.*;
import com.example.demo.shared.valueobject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Infrastructure adapter that implements core's TestExecutionPort
 * by delegating to the test-execution-service via common's TestExecutionPort.
 *
 * <p>Responsibilities:
 * - Translate core domain objects to test-execution-service contracts
 * - Execute tests via test-execution-service
 * - Translate results back to core domain objects
 */
public class TestExecutionAdapter implements TestExecutionPort {

    private static final Logger log = LoggerFactory.getLogger(TestExecutionAdapter.class);

    private final com.example.demo.common.ports.TestExecutionPort testExecutionService;

    public TestExecutionAdapter(com.example.demo.common.ports.TestExecutionPort testExecutionService) {
        this.testExecutionService = testExecutionService;
    }

    @Override
    public TestCaseRun executeTestCase(TestCase testCase, Map<String, String> variables) {
        log.info("Executing test case: {}", testCase.getName());

        // Dispatch based on test case type
        if (testCase instanceof RestApiTest restTest) {
            return executeRestApiTest(restTest, variables);
        } else if (testCase instanceof SoapApiTest soapTest) {
            return executeSoapApiTest(soapTest, variables);
        } else if (testCase instanceof E2eTest e2eTest) {
            return executeE2eTest(e2eTest, variables);
        } else {
            throw new IllegalArgumentException("Unknown test case type: " + testCase.getClass().getName());
        }
    }

    @Override
    public TestSuiteRun executeTestSuite(TestSuite testSuite, Map<String, String> variables) {
        log.info("Executing test suite: {}", testSuite.getName());

        TestSuiteRun suiteRun = new TestSuiteRun();
        suiteRun.setTestSuite(testSuite);
        suiteRun.start();

        // Execute each test case sequentially
        for (TestCase testCase : testSuite.getTestCases()) {
            TestCaseRun caseRun = executeTestCase(testCase, variables);
            suiteRun.addTestCaseRun(caseRun);
        }

        // Complete the suite run
        if (suiteRun.allTestCasesPassed()) {
            suiteRun.completeWithSuccess();
        } else {
            suiteRun.completeWithFailure();
        }

        log.info("Test suite completed: {} - {}", testSuite.getName(), suiteRun.getResult());
        return suiteRun;
    }

    @Override
    public boolean isHealthy() {
        try {
            int queueSize = testExecutionService.getQueueSize();
            return queueSize >= 0; // Simple health check
        } catch (Exception e) {
            log.error("Health check failed", e);
            return false;
        }
    }

    // ========== REST API Test Execution ==========

    private ApiTestRun executeRestApiTest(RestApiTest restTest, Map<String, String> variables) {
        RestRequest request = restTest.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("REST test has no request defined");
        }

        // Create REST run request
        HttpRequestData httpRequestData = translateRestRequest(request);
        List<AssertionSpec> assertionSpecs = translateAssertions(restTest.getAssertions());

        RestRunRequest runRequest = new RestRunRequest(
            "rest-" + System.currentTimeMillis(),
            httpRequestData,
            assertionSpecs,
            variables
        );

        // Execute and wait for result
        CompletableFuture<ApiRunResult> futureResult = new CompletableFuture<>();
        CountDownLatch latch = new CountDownLatch(1);

        testExecutionService.submit(runRequest, result -> {
            if (result instanceof ApiRunResult apiResult) {
                futureResult.complete(apiResult);
            } else {
                futureResult.completeExceptionally(
                    new IllegalStateException("Expected ApiRunResult but got: " + result.getClass().getName())
                );
            }
            latch.countDown();
        });

        // Wait for result (with timeout)
        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Test execution timeout after 30 seconds");
            }
            ApiRunResult result = futureResult.get();
            return translateApiRunResult(restTest, result);
        } catch (Exception e) {
            log.error("Error executing REST test", e);
            throw new RuntimeException("Failed to execute REST test: " + e.getMessage(), e);
        }
    }

    // ========== SOAP API Test Execution ==========

    private ApiTestRun executeSoapApiTest(SoapApiTest soapTest, Map<String, String> variables) {
        SoapRequest request = soapTest.getRequest();
        if (request == null) {
            throw new IllegalArgumentException("SOAP test has no request defined");
        }

        // Create SOAP run request
        HttpRequestData httpRequestData = translateSoapRequest(request);
        List<AssertionSpec> assertionSpecs = translateAssertions(soapTest.getAssertions());

        SoapRunRequest runRequest = new SoapRunRequest(
            "soap-" + System.currentTimeMillis(),
            httpRequestData,
            assertionSpecs,
            variables
        );

        // Execute and wait for result
        CompletableFuture<ApiRunResult> futureResult = new CompletableFuture<>();
        CountDownLatch latch = new CountDownLatch(1);

        testExecutionService.submit(runRequest, result -> {
            if (result instanceof ApiRunResult apiResult) {
                futureResult.complete(apiResult);
            } else {
                futureResult.completeExceptionally(
                    new IllegalStateException("Expected ApiRunResult but got: " + result.getClass().getName())
                );
            }
            latch.countDown();
        });

        // Wait for result (with timeout)
        try {
            if (!latch.await(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Test execution timeout after 30 seconds");
            }
            ApiRunResult result = futureResult.get();
            return translateApiRunResult(soapTest, result);
        } catch (Exception e) {
            log.error("Error executing SOAP test", e);
            throw new RuntimeException("Failed to execute SOAP test: " + e.getMessage(), e);
        }
    }

    // ========== E2E Test Execution ==========

    private E2eTestRun executeE2eTest(E2eTest e2eTest, Map<String, String> variables) {
        List<E2eStep> steps = e2eTest.getSteps();
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("E2E test has no steps defined");
        }

        // Create E2E run request
        List<E2eStepRequest> stepRequests = new ArrayList<>();
        for (int i = 0; i < steps.size(); i++) {
            E2eStep step = steps.get(i);
            E2eStepRequest stepRequest = translateE2eStep(step, i + 1);
            stepRequests.add(stepRequest);
        }

        E2eRunRequest runRequest = new E2eRunRequest(
            "e2e-" + System.currentTimeMillis(),
            stepRequests,
            variables
        );

        // Execute and wait for result
        CompletableFuture<E2eRunResult> futureResult = new CompletableFuture<>();
        CountDownLatch latch = new CountDownLatch(1);

        testExecutionService.submit(runRequest, result -> {
            if (result instanceof E2eRunResult e2eResult) {
                futureResult.complete(e2eResult);
            } else {
                futureResult.completeExceptionally(
                    new IllegalStateException("Expected E2eRunResult but got: " + result.getClass().getName())
                );
            }
            latch.countDown();
        });

        // Wait for result (with timeout)
        try {
            if (!latch.await(60, TimeUnit.SECONDS)) {
                throw new RuntimeException("E2E test execution timeout after 60 seconds");
            }
            E2eRunResult result = futureResult.get();
            return translateE2eRunResult(e2eTest, result);
        } catch (Exception e) {
            log.error("Error executing E2E test", e);
            throw new RuntimeException("Failed to execute E2E test: " + e.getMessage(), e);
        }
    }

    // ========== Translation Methods ==========

    private HttpRequestData translateRestRequest(RestRequest request) {
        HttpRequest httpRequest = request.getHttpRequest();

        return new HttpRequestData(
            httpRequest.getMethod().name(),
            httpRequest.getUrl(),
            httpRequest.getHeaders() != null ? httpRequest.getHeaders() : Map.of(),
            translateBody(request.getBody())
        );
    }

    private HttpRequestData translateSoapRequest(SoapRequest request) {
        HttpRequest httpRequest = request.getHttpRequest();

        return new HttpRequestData(
            httpRequest.getMethod().name(),
            httpRequest.getUrl(),
            httpRequest.getHeaders() != null ? httpRequest.getHeaders() : Map.of(),
            request.getSoapEnvelope()
        );
    }

    private byte[] translateBody(Body body) {
        if (body == null || body instanceof NoBody) {
            return null;
        } else if (body instanceof JsonBody jsonBody) {
            return jsonBody.getRawJson().getBytes();
        } else if (body instanceof XmlBody xmlBody) {
            return xmlBody.getRawXml().getBytes();
        } else if (body instanceof TextBody textBody) {
            return textBody.getText().getBytes();
        } else if (body instanceof BinaryBody binaryBody) {
            return binaryBody.getData();
        } else if (body instanceof FormUrlEncodedBody formBody) {
            return formBody.getRawFormData().getBytes();
        } else {
            throw new IllegalArgumentException("Unknown body type: " + body.getClass().getName());
        }
    }

    private List<AssertionSpec> translateAssertions(List<Assertion> assertions) {
        return assertions.stream()
            .map(assertion -> new AssertionSpec(
                assertion.type().name().toLowerCase(),
                assertion.getPath() != null ? assertion.getPath() : "",
                assertion.getExpectedValue()
            ))
            .toList();
    }

    private E2eStepRequest translateE2eStep(E2eStep step, int order) {
        // E2eStep has HttpRequest directly, not through ApiTest
        HttpRequest<Body> httpRequest = step.getHttpRequest();
        if (httpRequest == null) {
            throw new IllegalArgumentException("E2E step has no HTTP request defined");
        }

        // Create HTTP request data
        HttpRequestData httpRequestData = new HttpRequestData(
            httpRequest.getMethod().name(),
            httpRequest.getUrl(),
            httpRequest.getHeaders() != null ? httpRequest.getHeaders() : Map.of(),
            translateBody(httpRequest.getBody())
        );

        // Determine API type based on content type or body type
        String apiType = determineApiType(httpRequest);

        // Translate assertions
        List<AssertionSpec> assertionSpecs = translateAssertions(step.getAssertions());

        // Translate extractors
        List<ExtractorSpec> extractorSpecs = step.getExtractorItems().stream()
            .map(extractor -> new ExtractorSpec(
                extractor.variableName(),
                extractor.type().name(),
                extractor.expression()
            ))
            .toList();

        return new E2eStepRequest(
            "step-" + order,
            step.getName(),
            order,
            apiType,
            httpRequestData,
            assertionSpecs,
            extractorSpecs
        );
    }

    /**
     * Determine API type (REST or SOAP) from HTTP request.
     */
    private String determineApiType(HttpRequest<Body> httpRequest) {
        // Check Content-Type header
        Map<String, String> headers = httpRequest.getHeaders();
        if (headers != null) {
            String contentType = headers.getOrDefault("Content-Type", "");
            if (contentType.contains("xml") || contentType.contains("soap")) {
                return "SOAP";
            }
        }

        // Check body type
        Body body = httpRequest.getBody();
        if (body instanceof XmlBody) {
            return "SOAP";
        }

        // Default to REST
        return "REST";
    }

    private ApiTestRun translateApiRunResult(TestCase testCase, ApiRunResult result) {
        ApiTestRun testRun = new ApiTestRun();
        testRun.setTestCase(testCase);
        testRun.start();

        // Add assertion results
        for (com.example.demo.shared.valueobject.AssertionResult assertionResult : result.assertionResults()) {
            // Find matching assertion in test case
            Assertion assertion = findMatchingAssertion(testCase, assertionResult);
            AssertionResult domainResult = new AssertionResult(
                assertion,
                assertionResult.ok(),
                assertionResult.message()
            );
            testRun.addAssertionResult(domainResult);
        }

        // Complete the test run
        if (result.status().equals("SUCCESS")) {
            testRun.completeWithSuccess();
        } else {
            testRun.completeWithFailure();
        }

        return testRun;
    }

    private E2eTestRun translateE2eRunResult(E2eTest testCase, E2eRunResult result) {
        E2eTestRun testRun = new E2eTestRun();
        testRun.setTestCase(testCase);
        testRun.start();

        // Add step runs
        for (StepResult stepResult : result.stepResults()) {
            E2eStepRun stepRun = new E2eStepRun();
            E2eStep step = testCase.getSteps().get(stepResult.stepOrder() - 1);
            stepRun.setE2eStep(step);
            stepRun.start();

            // Add assertion results for this step
            for (com.example.demo.shared.valueobject.AssertionResult assertionResult : stepResult.assertionResults()) {
                Assertion assertion = findMatchingAssertionInStep(step, assertionResult);
                AssertionResult domainResult = new AssertionResult(
                    assertion,
                    assertionResult.ok(),
                    assertionResult.message()
                );
                stepRun.addAssertionResult(domainResult);
            }

            // Add extracted variables
            if (stepResult.extractedVariables() != null) {
                stepRun.setExtractedVariables(new HashMap<>(stepResult.extractedVariables()));
            }

            // Complete step run
            if (stepResult.status().equals("SUCCESS")) {
                stepRun.completeWithSuccess();
            } else {
                stepRun.completeWithFailure();
            }

            testRun.addStepRun(stepRun);
        }

        // Complete the E2E test run
        if (result.status().equals("SUCCESS")) {
            testRun.completeWithSuccess();
        } else {
            testRun.completeWithFailure();
        }

        return testRun;
    }

    /**
     * Find matching assertion in test case by type and path.
     * This is a best-effort match since we don't have a direct link.
     */
    private Assertion findMatchingAssertion(TestCase testCase, com.example.demo.shared.valueobject.AssertionResult result) {
        List<Assertion> assertions = switch (testCase) {
            case RestApiTest rest -> rest.getAssertions();
            case SoapApiTest soap -> soap.getAssertions();
            case E2eTest e2e -> List.of(); // E2E has assertions in steps
            default -> List.of();
        };

        // Try to match by type name
        for (Assertion assertion : assertions) {
            if (assertion.type().name().equalsIgnoreCase(result.type())) {
                return assertion;
            }
        }

        // If no match found, return the first assertion (fallback)
        if (!assertions.isEmpty()) {
            return assertions.get(0);
        }

        // Last resort: create a dummy assertion
        throw new IllegalStateException("Could not find matching assertion for result: " + result.type());
    }

    /**
     * Find matching assertion in E2E step by type.
     */
    private Assertion findMatchingAssertionInStep(E2eStep step, com.example.demo.shared.valueobject.AssertionResult result) {
        List<Assertion> assertions = step.getAssertions();

        // Try to match by type name
        for (Assertion assertion : assertions) {
            if (assertion.type().name().equalsIgnoreCase(result.type())) {
                return assertion;
            }
        }

        // If no match found, return the first assertion (fallback)
        if (!assertions.isEmpty()) {
            return assertions.get(0);
        }

        // Last resort: create a dummy assertion
        throw new IllegalStateException("Could not find matching assertion for result: " + result.type());
    }
}
