package com.example.demo.core.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * DTO for creating a new project.
 */
public record CreateProjectRequest(
        @NotBlank(message = "Project name is required")
        @Size(max = 40, message = "Project name must be at most 40 characters")
        String name,

        @Size(max = 2000, message = "Description must be at most 2000 characters")
        String description,

        Map<String, String> initialVariables
) {
}
