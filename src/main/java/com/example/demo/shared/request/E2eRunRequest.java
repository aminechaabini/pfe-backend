package com.example.demo.shared.request;

import java.util.List;
import java.util.Map;

/**
 * Request to execute an end-to-end test workflow.
 * Contains multiple steps that execute sequentially with variable passing between steps.
 */
public record E2eRunRequest(
    String runId,                       // Unique identifier for this E2E test run
    List<E2eStepRequest> steps,         // Sequential steps to execute in order
    Map<String, String> variables       // Initial variables (project + suite level)
) implements RunRequest {}
