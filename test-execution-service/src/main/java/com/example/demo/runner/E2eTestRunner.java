package com.example.demo.runner;

import com.example.demo.runner.builder.HttpRequestBuilder;
import com.example.demo.runner.context.VariableContext;
import com.example.demo.runner.executor.HttpRequestExecutor;
import com.example.demo.runner.extractor.VariableExtractor;
import com.example.demo.runner.validator.AssertionValidator;
import com.example.demo.shared.request.*;
import com.example.demo.shared.result.*;
import com.example.demo.shared.valueobject.*;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes E2E test workflows with sequential steps and variable passing.
 * Flow: Initialize Context → For Each Step (Build → Execute → Validate → Extract) → Return Result
 */
public class E2eTestRunner {

    private final Map<String, HttpRequestBuilder> builders;
    private final HttpRequestExecutor executor;
    private final AssertionValidator validator;
    private final VariableExtractor extractor;

    public E2eTestRunner(
        Map<String, HttpRequestBuilder> builders,
        HttpRequestExecutor executor,
        AssertionValidator validator,
        VariableExtractor extractor
    ) {
        this.builders = builders;
        this.executor = executor;
        this.validator = validator;
        this.extractor = extractor;
    }

    /**
     * Execute an E2E test workflow.
     */
    public E2eRunResult run(E2eRunRequest request) {
        long startTime = System.currentTimeMillis();

        // 1. Initialize variable context with initial variables
        VariableContext context = new VariableContext(request.variables());
        List<StepResult> stepResults = new ArrayList<>();

        // 2. Execute each step sequentially
        for (E2eStepRequest step : request.steps()) {
            StepResult stepResult = executeStep(step, context);
            stepResults.add(stepResult);

            // 3. Stop on first failure/error
            if (!"PASS".equals(stepResult.status())) {
                break;
            }

            // 4. Merge extracted variables into context for next steps
            context = context.merge(stepResult.extractedVariables());
        }

        // 5. Determine overall status
        boolean allPassed = stepResults.stream().allMatch(s -> "PASS".equals(s.status()));
        boolean hasError = stepResults.stream().anyMatch(s -> "ERROR".equals(s.status()));
        String status = hasError ? "ERROR" : (allPassed ? "PASS" : "FAIL");

        long duration = System.currentTimeMillis() - startTime;

        return new E2eRunResult(
            request.runId(),
            status,
            duration,
            stepResults,
            context.getVariables(),
            null
        );
    }

    /**
     * Execute a single E2E step.
     */
    private StepResult executeStep(E2eStepRequest step, VariableContext context) {
        long stepStartTime = System.currentTimeMillis();

        try {
            // 1. Get appropriate builder
            HttpRequestBuilder builder = builders.get(step.protocol());

            // 2. Build HTTP request with current variable context
            HttpRequest httpRequest = builder.build(step.httpRequest(), context.getVariables());

            // 3. Execute HTTP request
            HttpResponse<String> response = executor.execute(httpRequest);

            // 4. Validate assertions
            List<AssertionResult> assertionResults = validator.validate(step.assertions(), response);

            // 5. Extract variables from response
            Map<String, String> extractedVariables = extractor.extract(step.extractors(), response);

            // 6. Convert response to shared contract
            HttpResponseData responseData = toResponseData(response, stepStartTime);

            // 7. Determine step status
            boolean allPassed = assertionResults.stream().allMatch(AssertionResult::ok);
            String status = allPassed ? "PASS" : "FAIL";

            long stepDuration = System.currentTimeMillis() - stepStartTime;

            return new StepResult(
                step.stepId(),
                step.stepName(),
                step.stepOrder(),
                status,
                responseData,
                assertionResults,
                extractedVariables,
                stepDuration,
                null
            );

        } catch (Exception e) {
            long stepDuration = System.currentTimeMillis() - stepStartTime;
            return new StepResult(
                step.stepId(),
                step.stepName(),
                step.stepOrder(),
                "ERROR",
                null,
                List.of(),
                Map.of(),
                stepDuration,
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
