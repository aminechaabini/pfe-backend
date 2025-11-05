package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.test.Runnable;

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

    public Run(Runnable runnable){
        this.runnable = runnable;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void start() {
        if (this.status == RunStatus.NOT_STARTED) {
            this.status = RunStatus.IN_PROGRESS;
            this.startedAt = Instant.now();
            this.updatedAt = this.startedAt;
        }
        else {
            throw new RuntimeException("Run is not in NOT_STARTED state");
        }
    }

    public void complete() {
        if (this.status == RunStatus.IN_PROGRESS) {
            this.status = RunStatus.COMPLETED;
            this.completedAt = Instant.now();
            this.updatedAt = this.completedAt;
        }
        else {
            throw new RuntimeException("Run is not in IN_PROGRESS state");
        }
    }

    public void fail() {
        if (this.status == RunStatus.IN_PROGRESS) {
        this.status = RunStatus.FAILED;
        this.updatedAt = Instant.now();
    }
        else {
            throw new RuntimeException("Run is not in IN_PROGRESS state");
        }
    }

    public Long getId() {
        return id;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public RunStatus getStatus() {
        return status;
    }

    public RunResult getResult() {
        return result;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setResult(RunResult result) {
        this.result = result;
    }
}


