package com.example.demo.core.app.service.exception;

/**
 * Base exception for all orchestrator service layer exceptions.
 */
public class OrchestratorException extends RuntimeException {

    public OrchestratorException(String message) {
        super(message);
    }

    public OrchestratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
