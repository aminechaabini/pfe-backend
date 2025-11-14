package com.example.demo.orchestrator.app.service.exception;

/**
 * Exception thrown when attempting to create a duplicate entity (409).
 */
public class DuplicateEntityException extends OrchestratorException {

    public DuplicateEntityException(String message) {
        super(message);
    }
}
