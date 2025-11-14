package com.example.demo.orchestrator.api.dto;

/**
 * Request DTO for creating a Project.
 */
public record CreateProjectRequest(
        String name,
        String description
) {}
