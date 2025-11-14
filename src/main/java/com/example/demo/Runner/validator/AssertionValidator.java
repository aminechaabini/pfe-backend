package com.example.demo.Runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Validates assertions against HTTP responses.
 */
public interface AssertionValidator {

    /**
     * Validate all assertions against the response.
     *
     * @param assertions list of assertions to validate
     * @param response the HTTP response to validate against
     * @return list of assertion results (one per assertion)
     */
    List<AssertionResult> validate(List<AssertionSpec> assertions, HttpResponse<String> response);
}
