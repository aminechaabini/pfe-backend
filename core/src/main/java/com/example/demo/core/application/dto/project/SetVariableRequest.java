package com.example.demo.core.application.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for setting a project variable.
 */
public record SetVariableRequest(
        @NotBlank(message = "Variable name is required")
        @Size(max = 200, message = "Variable name must be at most 200 characters")
        String name,

        @NotBlank(message = "Variable value is required")
        @Size(max = 2000, message = "Variable value must be at most 2000 characters")
        String value
) {
}
