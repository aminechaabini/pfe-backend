package com.example.demo.core.domain.run;

/**
 * Represents the execution status of a Run.
 * To determine success/failure of a completed run, check the RunResult.
 */
public enum RunStatus {
    /** Run has been created but not started yet */
    NOT_STARTED,
    
    /** Run is currently executing */
    IN_PROGRESS,
    
    /** Run has completed (check result field for success/failure) */
    COMPLETED
}
