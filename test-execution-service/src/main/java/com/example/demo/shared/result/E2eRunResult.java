package com.example.demo.shared.result;

import java.util.List;
import java.util.Map;

/**
 * Result of executing an E2E test workflow.
 */
public record E2eRunResult(
    String runId,
    String status,
    long duration,
    List<StepResult> stepResults,
    Map<String, String> finalVariables,
    String errorMessage
) implements RunResult {
}
