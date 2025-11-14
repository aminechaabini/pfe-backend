package com.example.demo.orchestrator.api.dto;

import com.example.demo.orchestrator.domain.test.assertion.Assertion;
import com.example.demo.orchestrator.domain.test.request.RestRequest;

import java.util.List;

/**
 * Request DTO for creating a REST API test.
 */
public record CreateRestTestRequest(
        String name,
        String description,
        RestRequest request,
        List<Assertion> assertions
) {}
