package com.example.demo.core.dto.run;

import jakarta.validation.constraints.NotBlank;

public record TriggerRunRequest(@NotBlank String projectId,
                                String suiteId,
                                String testId) {
}
