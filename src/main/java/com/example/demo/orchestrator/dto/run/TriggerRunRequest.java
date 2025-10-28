package com.example.demo.orchestrator.dto.run;

import jakarta.validation.constraints.NotBlank;

public record TriggerRunRequest(@NotBlank String projectId,
                                String suiteId,
                                String testId) {
}
