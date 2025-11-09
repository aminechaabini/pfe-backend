package com.example.demo.orchestrator.domain.run;

import com.example.demo.orchestrator.domain.exception.InvalidRunStateException;

import java.time.Instant;
import java.util.Objects;

public abstract class Run {

    private Long id;

    private RunStatus status = RunStatus.NOT_STARTED;

    private RunResult result; // null until completed

    private final Instant createdAt;

    private Instant updatedAt;

    private Instant startedAt;

    private Instant completedAt;

    protected Run() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /**
     * Start the run execution.
     * @throws InvalidRunStateException if run is not in NOT_STARTED state
     */
    public void start() {
        validateCanTransition(RunStatus.NOT_STARTED, "start");
        this.status = RunStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        this.updatedAt = this.startedAt;
    }

    /**
     * Complete the run successfully.
     * @throws InvalidRunStateException if run is not in IN_PROGRESS state
     */
    public void completeWithSuccess() {
        validateCanTransition(RunStatus.IN_PROGRESS, "complete");
        this.status = RunStatus.COMPLETED;
        this.result = RunResult.SUCCESS;
        this.completedAt = Instant.now();
        this.updatedAt = this.completedAt;
    }

    /**
     * Complete the run with failure.
     * @throws InvalidRunStateException if run is not in IN_PROGRESS state
     */
    public void completeWithFailure() {
        validateCanTransition(RunStatus.IN_PROGRESS, "complete");
        this.status = RunStatus.COMPLETED;
        this.result = RunResult.FAILURE;
        this.completedAt = Instant.now();
        this.updatedAt = this.completedAt;
    }

    /**
     * Check if the run has completed successfully.
     */
    public boolean isSuccessful() {
        return status == RunStatus.COMPLETED && result == RunResult.SUCCESS;
    }

    /**
     * Check if the run has completed with failure.
     */
    public boolean isFailed() {
        return status == RunStatus.COMPLETED && result == RunResult.FAILURE;
    }

    /**
     * Check if the run is currently in progress.
     */
    public boolean isInProgress() {
        return status == RunStatus.IN_PROGRESS;
    }

    /**
     * Validate that the run is in the expected state before transitioning.
     */
    private void validateCanTransition(RunStatus expected, String operation) {
        if (this.status != expected) {
            throw new InvalidRunStateException(
                String.format("Cannot %s run. Current state: %s, Expected: %s", 
                              operation, this.status, expected)
            );
        }
    }

    // Getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Cannot change ID once set");
        }
        this.id = Objects.requireNonNull(id, "ID cannot be null");
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
}
