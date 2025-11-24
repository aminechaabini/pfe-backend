package com.example;

import com.example.demo.runner.*;
import com.example.demo.runner.builder.*;
import com.example.demo.runner.executor.*;
import com.example.demo.runner.extractor.*;
import com.example.demo.runner.validator.*;
import com.example.demo.shared.request.*;
import com.example.demo.shared.result.*;
import com.example.demo.shared.valueobject.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test Execution Runner - demonstrates test execution service capabilities.
 *
 * This application demonstrates:
 * 1. REST API test execution with assertions
 * 2. SOAP API test execution with XML validation
 * 3. E2E workflow execution with variable extraction and passing
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.demo.runner", "com.example"})
public class TestExecutionRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestExecutionRunnerApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            System.out.println("\n=== Test Execution Service Demo ===\n");

            try {
                // Setup test execution infrastructure
                Map<String, HttpRequestBuilder> builders = Map.of(
                    "REST", new RestRequestBuilder(),
                    "SOAP", new SoapRequestBuilder()
                );

                HttpRequestExecutor executor = new DefaultHttpRequestExecutor();

                AssertionValidator assertionValidator = new CompositeAssertionValidator(
                    new StatusAssertionValidator(),
                    new JsonPathAssertionValidator(),
                    new XPathAssertionValidator()
                );

                VariableExtractor variableExtractor = new CompositeVariableExtractor(
                    new JsonPathExtractor(),
                    new XPathExtractor(),
                    new RegexExtractor()
                );

                ApiTestRunner apiRunner = new ApiTestRunner(builders, executor, assertionValidator);
                E2eTestRunner e2eRunner = new E2eTestRunner(builders, executor, assertionValidator, variableExtractor);
                RunnerService runnerService = new RunnerService(apiRunner, e2eRunner);

                // Run demos
                testRestApiExecution(runnerService);
                testSoapApiExecution(runnerService);
                testE2eWorkflowExecution(runnerService);

                System.out.println("=== Demo completed successfully! ===\n");

                // Shutdown
                runnerService.shutdown();

            } catch (Exception e) {
                System.err.println("Error during test execution: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Test REST API execution with JSONPath assertions.
     */
    private void testRestApiExecution(RunnerService runnerService) throws Exception {
        System.out.println("=== Testing REST API Execution ===\n");

        // Create a REST test for JSONPlaceholder API
        HttpRequestData request = new HttpRequestData(
            "GET",
            "https://jsonplaceholder.typicode.com/users/1",
            Map.of("Accept", "application/json"),
            (byte[]) null
        );

        List<AssertionSpec> assertions = List.of(
            new AssertionSpec("statusEquals", "", "200"),
            new AssertionSpec("jsonPathEquals", "$.id", "1"),
            new AssertionSpec("jsonPathEquals", "$.username", "Bret")
        );

        RestRunRequest runRequest = new RestRunRequest(
            "rest-test-1",
            request,
            assertions,
            Map.of()
        );

        CountDownLatch latch = new CountDownLatch(1);

        runnerService.submit(runRequest, result -> {
            System.out.println("✓ REST Test Completed:");
            System.out.println("  Run ID: " + result.runId());
            System.out.println("  Status: " + result.status());
            System.out.println("  Duration: " + result.duration() + "ms");

            if (result instanceof ApiRunResult apiResult) {
                System.out.println("  Response Status: " + apiResult.response().statusCode());
                System.out.println("  Assertions:");
                for (AssertionResult assertion : apiResult.assertionResults()) {
                    System.out.println("    - " + (assertion.ok() ? "✓" : "✗") + " " + assertion.message());
                }
            }

            latch.countDown();
        });

        latch.await(10, TimeUnit.SECONDS);
        System.out.println();
    }

    /**
     * Test SOAP API execution with XPath assertions.
     */
    private void testSoapApiExecution(RunnerService runnerService) throws Exception {
        System.out.println("=== Testing SOAP API Execution ===\n");

        String soapEnvelope = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <NumberToWords xmlns="http://www.dataaccess.com/webservicesserver/">
                  <ubiNum>42</ubiNum>
                </NumberToWords>
              </soap:Body>
            </soap:Envelope>
            """;

        HttpRequestData request = new HttpRequestData(
            "POST",
            "https://www.dataaccess.com/webservicesserver/numberconversion.wso",
            Map.of(
                "Content-Type", "text/xml; charset=utf-8",
                "SOAPAction", "http://www.dataaccess.com/webservicesserver/NumberToWords"
            ),
            soapEnvelope
        );

        List<AssertionSpec> assertions = List.of(
            new AssertionSpec("statusEquals", "", "200")
        );

        SoapRunRequest runRequest = new SoapRunRequest(
            "soap-test-1",
            request,
            assertions,
            Map.of()
        );

        CountDownLatch latch = new CountDownLatch(1);

        runnerService.submit(runRequest, result -> {
            System.out.println("✓ SOAP Test Completed:");
            System.out.println("  Run ID: " + result.runId());
            System.out.println("  Status: " + result.status());
            System.out.println("  Duration: " + result.duration() + "ms");

            if (result instanceof ApiRunResult apiResult) {
                System.out.println("  Response Status: " + apiResult.response().statusCode());
                System.out.println("  Response Body Preview: " +
                    (apiResult.response().body().length() > 100
                        ? apiResult.response().body().substring(0, 100) + "..."
                        : apiResult.response().body()));
                System.out.println("  Assertions:");
                for (AssertionResult assertion : apiResult.assertionResults()) {
                    System.out.println("    - " + (assertion.ok() ? "✓" : "✗") + " " + assertion.message());
                }
            }

            latch.countDown();
        });

        latch.await(10, TimeUnit.SECONDS);
        System.out.println();
    }

    /**
     * Test E2E workflow execution with variable extraction and passing.
     */
    private void testE2eWorkflowExecution(RunnerService runnerService) throws Exception {
        System.out.println("=== Testing E2E Workflow Execution ===\n");

        // Step 1: Get a post
        E2eStepRequest step1 = new E2eStepRequest(
            "step-1",
            "Get Post",
            1,
            "REST",
            new HttpRequestData(
                "GET",
                "https://jsonplaceholder.typicode.com/posts/1",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200"),
                new AssertionSpec("jsonPathEquals", "$.id", "1")
            ),
            List.of(
                new ExtractorSpec("userId", "JSON_PATH", "$.userId")
            )
        );

        // Step 2: Get the user using extracted userId
        E2eStepRequest step2 = new E2eStepRequest(
            "step-2",
            "Get User",
            2,
            "REST",
            new HttpRequestData(
                "GET",
                "https://jsonplaceholder.typicode.com/users/${userId}",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200")
            ),
            List.of(
                new ExtractorSpec("username", "JSON_PATH", "$.username")
            )
        );

        // Step 3: Get user's todos using extracted userId
        E2eStepRequest step3 = new E2eStepRequest(
            "step-3",
            "Get User Todos",
            3,
            "REST",
            new HttpRequestData(
                "GET",
                "https://jsonplaceholder.typicode.com/todos?userId=${userId}",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200")
            ),
            List.of()
        );

        E2eRunRequest runRequest = new E2eRunRequest(
            "e2e-test-1",
            List.of(step1, step2, step3),
            Map.of()
        );

        CountDownLatch latch = new CountDownLatch(1);

        runnerService.submit(runRequest, result -> {
            System.out.println("✓ E2E Test Completed:");
            System.out.println("  Run ID: " + result.runId());
            System.out.println("  Status: " + result.status());
            System.out.println("  Duration: " + result.duration() + "ms");

            if (result instanceof E2eRunResult e2eResult) {
                System.out.println("\n  Steps:");
                for (StepResult stepResult : e2eResult.stepResults()) {
                    System.out.println("    Step " + stepResult.stepOrder() + ": " + stepResult.stepName());
                    System.out.println("      Status: " + stepResult.status());
                    System.out.println("      Duration: " + stepResult.duration() + "ms");
                    System.out.println("      Response Status: " + stepResult.response().statusCode());

                    if (!stepResult.extractedVariables().isEmpty()) {
                        System.out.println("      Extracted Variables:");
                        stepResult.extractedVariables().forEach((key, value) ->
                            System.out.println("        " + key + " = " + value)
                        );
                    }

                    System.out.println("      Assertions:");
                    for (AssertionResult assertion : stepResult.assertionResults()) {
                        System.out.println("        - " + (assertion.ok() ? "✓" : "✗") + " " + assertion.message());
                    }
                    System.out.println();
                }

                System.out.println("  Final Variables:");
                e2eResult.finalVariables().forEach((key, value) ->
                    System.out.println("    " + key + " = " + value)
                );
            }

            latch.countDown();
        });

        latch.await(15, TimeUnit.SECONDS);
        System.out.println();
    }
}
