package com.example.demo.orchestrator.dto.run;

import java.time.Instant;

public record RunItemResponse(
    Long id,
    Long runId,
    Long testId,
    String status,   // RunStatus as string
    String result,   // RunResult as string (nullable until done)
    Instant startedAt,
    Instant finishedAt,
    Long durationMs,
    String workerId
) {}
