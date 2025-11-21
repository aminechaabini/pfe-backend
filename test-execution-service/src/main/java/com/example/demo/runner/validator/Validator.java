package com.example.demo.runner.validator;

import com.example.demo.shared.valueobject.AssertionResult;
import com.example.demo.shared.valueobject.AssertionSpec;

import java.net.http.HttpResponse;
import java.util.List;

/**
 * Base interface for specific assertion validators.
 * Each validator supports one or more assertion types.
 */
public interface Validator {

    /**
     * Get list of assertion types this validator supports.
     *
     * @return list of supported types (e.g., ["statusEquals"], ["jsonPathExists", "jsonPathEquals"])
     */
    List<String> supportedTypes();

    /**
     * Validate a single assertion against the response.
     *
     * @param assertion the assertion to validate
     * @param response the HTTP response
     * @return assertion result with pass/fail status and message
     */
    AssertionResult validate(AssertionSpec assertion, HttpResponse<String> response);
}
