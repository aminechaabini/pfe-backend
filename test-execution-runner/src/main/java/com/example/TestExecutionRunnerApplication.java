package com.example;

import com.example.demo.common.ports.TestExecutionPort;
import com.example.demo.shared.request.*;
import com.example.demo.shared.result.ApiRunResult;
import com.example.demo.shared.result.E2eRunResult;
import com.example.demo.shared.result.StepResult;
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
    public CommandLineRunner demo(TestExecutionPort testExecutionPort) {
        return args -> {
            System.out.println("\n=== Test Execution Service Demo ===\n");

            try {
                // Run demos using the injected port
                testRestApiExecution(testExecutionPort);
                testSoapApiExecution(testExecutionPort);
                testE2eWorkflowExecution(testExecutionPort);

                System.out.println("=== Demo completed successfully! ===\n");

                // Shutdown
                testExecutionPort.shutdown();

            } catch (Exception e) {
                System.err.println("Error during test execution: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }

    /**
     * Test REST API execution with JSONPath assertions.
     */
    private void testRestApiExecution(TestExecutionPort testExecutionPort) throws Exception {
        System.out.println("=== Testing REST API Execution with Variables ===\n");
        System.out.println("Using variable: baseUrl = https://jsonplaceholder.typicode.com\n");

        // Create a REST test using ${baseUrl} variable
        HttpRequestData request = new HttpRequestData(
            "GET",
            "${baseUrl}/users/1",
            Map.of("Accept", "application/json"),
            (byte[]) null
        );

        List<AssertionSpec> assertions = List.of(
            new AssertionSpec("statusEquals", "", "200"),
            new AssertionSpec("jsonPathEquals", "$.id", "1"),
            new AssertionSpec("jsonPathEquals", "$.username", "Bret")
        );

        // Pass baseUrl as a variable
        RestRunRequest runRequest = new RestRunRequest(
            "rest-test-1",
            request,
            assertions,
            Map.of("baseUrl", "https://jsonplaceholder.typicode.com")
        );

        CountDownLatch latch = new CountDownLatch(1);

        testExecutionPort.submit(runRequest, result -> {
            System.out.println("✓ REST Test Completed:");
            System.out.println("  Run ID: " + result.runId());
            System.out.println("  Status: " + result.status());
            System.out.println("  Duration: " + result.duration() + "ms");
            System.out.println("  Resolved URL: " + request.url().replace("${baseUrl}", "https://jsonplaceholder.typicode.com"));

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
    private void testSoapApiExecution(TestExecutionPort testExecutionPort) throws Exception {
        System.out.println("=== Testing SOAP API Execution with Variables ===\n");
        System.out.println("Using variable: soapUrl = https://www.dataaccess.com/webservicesserver\n");

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

        // Use ${soapUrl} variable in URL
        HttpRequestData request = new HttpRequestData(
            "POST",
            "${soapUrl}/numberconversion.wso",
            Map.of(
                "Content-Type", "text/xml; charset=utf-8",
                "SOAPAction", "http://www.dataaccess.com/webservicesserver/NumberToWords"
            ),
            soapEnvelope
        );

        List<AssertionSpec> assertions = List.of(
            new AssertionSpec("statusEquals", "", "200")
        );

        // Pass soapUrl as a variable
        SoapRunRequest runRequest = new SoapRunRequest(
            "soap-test-1",
            request,
            assertions,
            Map.of("soapUrl", "https://www.dataaccess.com/webservicesserver")
        );

        CountDownLatch latch = new CountDownLatch(1);

        testExecutionPort.submit(runRequest, result -> {
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
    private void testE2eWorkflowExecution(TestExecutionPort testExecutionPort) throws Exception {
        System.out.println("=== Testing E2E Workflow with Variable Extraction ===\n");
        System.out.println("Initial variables:");
        System.out.println("  baseUrl = https://jsonplaceholder.typicode.com");
        System.out.println("  postId = 1");
        System.out.println("\nWorkflow will:");
        System.out.println("  1. Get post and extract userId");
        System.out.println("  2. Use extracted userId to get user details and extract username, email, city");
        System.out.println("  3. Use extracted userId to get user's todos\n");

        // Step 1: Get a post and extract userId
        E2eStepRequest step1 = new E2eStepRequest(
            "step-1",
            "Get Post by ID",
            1,
            "REST",
            new HttpRequestData(
                "GET",
                "${baseUrl}/posts/${postId}",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200"),
                new AssertionSpec("jsonPathEquals", "$.id", "1"),
                new AssertionSpec("jsonPathEquals", "$.title", "sunt aut facere repellat provident occaecati excepturi optio reprehenderit")
            ),
            List.of(
                new ExtractorSpec("userId", "JSONPATH", "$.userId"),
                new ExtractorSpec("postTitle", "JSONPATH", "$.title")
            )
        );

        // Step 2: Get the user using extracted userId and extract more details
        E2eStepRequest step2 = new E2eStepRequest(
            "step-2",
            "Get User by Extracted ID",
            2,
            "REST",
            new HttpRequestData(
                "GET",
                "${baseUrl}/users/${userId}",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200"),
                new AssertionSpec("jsonPathEquals", "$.username", "Bret")
            ),
            List.of(
                new ExtractorSpec("username", "JSONPATH", "$.username"),
                new ExtractorSpec("email", "JSONPATH", "$.email"),
                new ExtractorSpec("city", "JSONPATH", "$.address.city")
            )
        );

        // Step 3: Get user's todos using extracted userId
        E2eStepRequest step3 = new E2eStepRequest(
            "step-3",
            "Get User's Todos",
            3,
            "REST",
            new HttpRequestData(
                "GET",
                "${baseUrl}/todos?userId=${userId}",
                Map.of("Accept", "application/json"),
                (byte[]) null
            ),
            List.of(
                new AssertionSpec("statusEquals", "", "200")
            ),
            List.of(
                new ExtractorSpec("firstTodoTitle", "JSONPATH", "$[0].title")
            )
        );

        // Step 4: Verify extracted variables by using them in a final request
        E2eStepRequest step4 = new E2eStepRequest(
            "step-4",
            "Verify Extracted Data",
            4,
            "REST",
            new HttpRequestData(
                "GET",
                "${baseUrl}/users/${userId}/posts",
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
            List.of(step1, step2, step3, step4),
            Map.of(
                "baseUrl", "https://jsonplaceholder.typicode.com",
                "postId", "1"
            )
        );

        CountDownLatch latch = new CountDownLatch(1);

        testExecutionPort.submit(runRequest, result -> {
            System.out.println("✓ E2E Test Completed:");
            System.out.println("  Run ID: " + result.runId());
            System.out.println("  Status: " + result.status());
            System.out.println("  Duration: " + result.duration() + "ms");

            if (result instanceof E2eRunResult e2eResult) {
                System.out.println("\n  🔗 Execution Chain:");
                for (StepResult stepResult : e2eResult.stepResults()) {
                    System.out.println("  ┌─ Step " + stepResult.stepOrder() + ": " + stepResult.stepName());
                    System.out.println("  │  Status: " + stepResult.status() + " (" + stepResult.duration() + "ms)");
                    System.out.println("  │  Response: HTTP " + stepResult.response().statusCode());

                    if (!stepResult.extractedVariables().isEmpty()) {
                        System.out.println("  │  📦 Extracted:");
                        stepResult.extractedVariables().forEach((key, value) ->
                            System.out.println("  │     • " + key + " = " + value)
                        );
                    }

                    System.out.println("  │  ✓ Assertions:");
                    for (AssertionResult assertion : stepResult.assertionResults()) {
                        System.out.println("  │     " + (assertion.ok() ? "✓" : "✗") + " " + assertion.message());
                    }
                    System.out.println("  └─");
                }

                System.out.println("\n  📊 All Variables Available at End:");
                e2eResult.finalVariables().forEach((key, value) ->
                    System.out.println("     • " + key + " = " + value)
                );
            }

            latch.countDown();
        });

        latch.await(15, TimeUnit.SECONDS);
        System.out.println();
    }
}
