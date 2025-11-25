package com.example.demo.runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;
import com.jayway.jsonpath.JsonPath;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Validates JSONPath assertions against JSON response bodies.
 * Supports: jsonPathExists, jsonPathEquals
 */
public class JsonPathAssertionValidator implements Validator {

    @Override
    public List<String> supportedTypes() {
        return List.of("jsonPathExists", "jsonPathEquals");
    }

    @Override
    public AssertionResult validate(AssertionSpec assertion, HttpResponse<String> response) {
        try {
            String body = response.body();
            Object value = JsonPath.read(body, assertion.expr());

            if ("jsonPathExists".equals(assertion.type())) {
                return new AssertionResult(
                    assertion.type(),
                    true,
                    "Path exists: " + assertion.expr()
                );
            }

            // jsonPathEquals
            String actualValue = String.valueOf(value);
            boolean passed = assertion.expected().equals(actualValue);
            String message = passed
                ? "Value matches: " + value
                : "Expected " + assertion.expected() + " but got " + value;

            return new AssertionResult(
                assertion.type(),
                passed,
                message,
                assertion.expected(),
                actualValue
            );

        } catch (Exception e) {
            return new AssertionResult(
                assertion.type(),
                false,
                "JSONPath error: " + e.getMessage()
            );
        }
    }
}
