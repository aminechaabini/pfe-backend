package com.example.demo.runner;

import com.example.demo.runner.builder.HttpRequestBuilder;
import com.example.demo.runner.executor.HttpRequestExecutor;
import com.example.demo.runner.validator.AssertionValidator;
import com.example.demo.shared.request.*;
import com.example.demo.shared.result.ApiRunResult;
import com.example.demo.shared.valueobject.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Executes single API tests (REST or SOAP).
 * Flow: Build → Execute → Validate → Return Result
 */
public class ApiTestRunner {

    private final Map<String, HttpRequestBuilder> builders;
    private final HttpRequestExecutor executor;
    private final AssertionValidator validator;

    public ApiTestRunner(
        Map<String, HttpRequestBuilder> builders,
        HttpRequestExecutor executor,
        AssertionValidator validator
    ) {
        this.builders = builders;
        this.executor = executor;
        this.validator = validator;
    }

    /**
     * Execute a REST API test.
     */
    public ApiRunResult run(RestRunRequest request) {
        return executeApiTest(request, "REST", request.httpRequest(),
            request.assertions(), request.variables());
    }

    /**
     * Execute a SOAP API test.
     */
    public ApiRunResult run(SoapRunRequest request) {
        return executeApiTest(request, "SOAP", request.httpRequest(),
            request.assertions(), request.variables());
    }

    private ApiRunResult executeApiTest(
        ApiRunRequest request,
        String protocol,
        HttpRequestData requestData,
        List<AssertionSpec> assertions,
        Map<String, String> variables
    ) {
        long startTime = System.currentTimeMillis();

        try {
            // 1. Build HTTP request with variable substitution
            HttpRequestBuilder builder = builders.get(protocol);
            HttpRequest httpRequest = builder.build(requestData, variables);

            // 2. Execute HTTP request
            HttpResponse<String> response = executor.execute(httpRequest);

            // 3. Validate assertions
            List<AssertionResult> assertionResults = validator.validate(assertions, response);

            // 4. Convert response to shared contract
            HttpResponseData responseData = toResponseData(response, startTime);

            // 5. Determine status
            boolean allPassed = assertionResults.stream().allMatch(AssertionResult::ok);
            String status = allPassed ? "PASS" : "FAIL";

            long duration = System.currentTimeMillis() - startTime;

            return new ApiRunResult(
                request.runId(),
                status,
                duration,
                responseData,
                assertionResults,
                null
            );

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            return new ApiRunResult(
                request.runId(),
                "ERROR",
                duration,
                null,
                List.of(),
                e.getMessage()
            );
        }
    }

    private HttpResponseData toResponseData(HttpResponse<String> response, long startTime) {
        Map<String, String> headers = response.headers().map().entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> String.join(",", e.getValue())
            ));

        long responseTime = System.currentTimeMillis() - startTime;

        return new HttpResponseData(
            response.statusCode(),
            headers,
            response.body(),
            responseTime
        );
    }
}
