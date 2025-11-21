package com.example.demo.runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Validates HTTP status code assertions.
 * Supports: statusEquals
 */
public class StatusAssertionValidator implements Validator {

    @Override
    public List<String> supportedTypes() {
        return List.of("statusEquals");
    }

    @Override
    public AssertionResult validate(AssertionSpec assertion, HttpResponse<String> response) {
        try {
            int expectedStatus = Integer.parseInt(assertion.expected());
            int actualStatus = response.statusCode();
            boolean passed = expectedStatus == actualStatus;

            String message = passed
                ? "Status matches: " + actualStatus
                : "Expected status " + expectedStatus + " but got " + actualStatus;

            return new AssertionResult(assertion, passed, message);

        } catch (NumberFormatException e) {
            return new AssertionResult(assertion, false, "Invalid expected status: " + assertion.expected());
        }
    }
}
