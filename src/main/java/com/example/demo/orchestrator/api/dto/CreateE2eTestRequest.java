package com.example.demo.orchestrator.api.dto;

import com.example.demo.orchestrator.domain.test.e2e.E2eStep;

import java.util.List;

/**
 * Request DTO for creating an E2E test.
 */
public record CreateE2eTestRequest(
        String name,
        String description,
        List<E2eStep> steps
) {}
