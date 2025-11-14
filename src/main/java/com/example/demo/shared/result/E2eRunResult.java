package com.example.demo.shared.result;

import java.util.List;
import java.util.Map;

/**
 * Result of an E2E test workflow execution.
 * Contains results from all steps, accumulated variables, and execution metadata.
 */
public record E2eRunResult(
    String runId,                       // Unique identifier for this E2E test run
    String status,                      // "PASS", "FAIL", "ERROR"
    long durationMs,                    // Total workflow execution time in milliseconds
    List<StepResult> stepResults,       // Results from each step in execution order
    Map<String, String> extractedVariables, // Final accumulated variables from all steps
    String errorMessage                 // Error message (nullable unless ERROR)
) implements RunResult {}
