package com.example.demo.orchestrator.app.service.exception;

/**
 * Exception thrown when test execution fails (500).
 */
public class TestExecutionException extends OrchestratorException {

    public TestExecutionException(String message) {
        super(message);
    }

    public TestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
