package com.example.demo.orchestrator.dto.suite;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTestSuiteRequest(
    Long projectId,
    @NotBlank @Size(max = 255) String name,
    @Size(max = 1000) String description
) {
    public Long getProjectId() { return projectId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
}