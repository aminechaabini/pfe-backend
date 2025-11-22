package com.example.demo.core.presentation.rest.dto;

/**
 * Request DTO for creating a Project.
 */
public record CreateProjectRequest(
        String name,
        String description
) {}
