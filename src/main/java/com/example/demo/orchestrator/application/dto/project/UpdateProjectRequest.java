package com.example.demo.orchestrator.application.dto.project;

import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing project.
 * Null fields are ignored (not updated).
 */
public record UpdateProjectRequest(
        @Size(max = 40, message = "Project name must be at most 40 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description
) {
}
