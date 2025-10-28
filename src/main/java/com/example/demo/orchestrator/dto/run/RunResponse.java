package com.example.demo.orchestrator.dto.run;

import java.time.Instant;
import java.util.List;

public record RunResponse(
    Long id,
    String type,      // RunType as string (SUITE or TEST)
    String status,    // RunStatus
    String result,    // RunResult (nullable)
    Long suiteId,     // nullable if type == TEST
    Long testId,      // nullable if type == SUITE
    Instant createdAt,
    Instant updatedAt,
    Instant startedAt,
    Instant completedAt,
    List<RunItemResponse> items
) {}
