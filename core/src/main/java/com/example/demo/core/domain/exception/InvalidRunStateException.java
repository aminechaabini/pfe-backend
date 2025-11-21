package com.example.demo.core.domain.exception;

import com.example.demo.core.domain.run.RunStatus;

/**
 * Thrown when attempting an invalid state transition on a Run.
 */
public class InvalidRunStateException extends IllegalStateException {
    
    public InvalidRunStateException(RunStatus current, RunStatus expected) {
        super(String.format("Cannot perform operation. Current state: %s, Expected: %s", 
                            current, expected));
    }
    
    public InvalidRunStateException(String message) {
        super(message);
    }
}
