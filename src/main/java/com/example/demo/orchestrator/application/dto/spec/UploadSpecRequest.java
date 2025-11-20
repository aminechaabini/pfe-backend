package com.example.demo.orchestrator.application.dto.spec;

import com.example.demo.orchestrator.domain.spec.SpecType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for uploading an API specification.
 */
public record UploadSpecRequest(
        @NotNull(message = "Project ID is required")
        Long projectId,

        @NotBlank(message = "Spec name is required")
        @Size(max = 100, message = "Spec name must be at most 100 characters")
        String name,

        @NotBlank(message = "File name is required")
        @Size(max = 255, message = "File name must be at most 255 characters")
        String fileName,

        @NotNull(message = "Spec type is required")
        SpecType specType,

        @NotBlank(message = "Spec content is required")
        String content
) {
}
