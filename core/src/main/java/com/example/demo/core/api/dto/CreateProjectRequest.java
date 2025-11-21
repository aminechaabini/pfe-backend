package com.example.demo.core.api.dto;

/**
 * Request DTO for creating a Project.
 */
public record CreateProjectRequest(
        String name,
        String description
) {}
