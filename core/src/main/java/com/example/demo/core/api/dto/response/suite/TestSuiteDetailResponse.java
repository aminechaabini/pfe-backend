package com.example.demo.core.api.dto.response.suite;

import com.example.demo.core.api.dto.response.spec.EndpointResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * API response DTO for TestSuite with test cases (detail).
 */
public record TestSuiteDetailResponse(
        Long id,
        String name,
        String description,
        Map<String, String> variables,
        Integer testCaseCount,
        Long endpointId,
        EndpointResponse endpoint,
        List<TestCaseResponse> testCases,
        Instant createdAt,
        Instant updatedAt
) {
}
