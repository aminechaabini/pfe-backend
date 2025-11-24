package com.example.demo.core.application.parser;

/**
 * Exception thrown when specification parsing fails.
 */
public class SpecParseException extends RuntimeException {

    public SpecParseException(String message) {
        super(message);
    }

    public SpecParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
