package com.example.demo.orchestrator.domain.run;

/**
 * Exception thrown when attempting to transition a Run to an invalid state.
 */
public class InvalidRunStateException extends IllegalStateException {
    
    public InvalidRunStateException(RunStatus currentState, RunStatus expectedState) {
        super(String.format("Cannot perform operation. Current state: %s, Expected: %s", 
                            currentState, expectedState));
    }
    
    public InvalidRunStateException(String message) {
        super(message);
    }
}
