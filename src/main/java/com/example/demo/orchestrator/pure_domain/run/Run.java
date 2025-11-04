package com.example.demo.orchestrator.pure_domain.run;

import com.example.demo.orchestrator.pure_domain.test.Runnable;

import java.time.Instant;

public class Run {

    private Long id;

    private Runnable runnable;

    private RunStatus status = RunStatus.NOT_STARTED;

    private RunResult result; // null until completed/failed

    private Instant createdAt;

    private Instant updatedAt;

    private Instant startedAt;

    private Instant completedAt;


