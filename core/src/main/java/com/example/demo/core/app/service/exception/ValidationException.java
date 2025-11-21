package com.example.demo.core.app.service.exception;

/**
 * Exception thrown when validation fails (400).
 */
public class ValidationException extends OrchestratorException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
