package com.example.demo.core.application.dto.generation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * DTO for generating an E2E workflow test.
 */
public record GenerateE2eTestRequest(
        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotBlank(message = "User story is required")
        String userStory,

        @NotEmpty(message = "At least one endpoint ID is required")
        List<Long> endpointIds
) {
}
