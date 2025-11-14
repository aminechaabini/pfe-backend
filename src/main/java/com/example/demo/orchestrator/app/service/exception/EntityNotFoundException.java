package com.example.demo.orchestrator.app.service.exception;

/**
 * Exception thrown when a requested entity is not found (404).
 */
public class EntityNotFoundException extends OrchestratorException {

    public EntityNotFoundException(String entityType, Long id) {
        super(String.format("%s with ID %d not found", entityType, id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
