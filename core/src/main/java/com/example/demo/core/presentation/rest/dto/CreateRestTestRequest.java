package com.example.demo.core.presentation.rest.dto;

import com.example.demo.core.domain.test.assertion.Assertion;
import com.example.demo.core.domain.test.request.RestRequest;

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
