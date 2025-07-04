package com.soaprestadapter.exception;

/**
 * Exception thrown when a parsing error occurs during processing.
 */
public class ParsingException extends RuntimeException {

    /**
     * Constructs a new ParsingException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public ParsingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new ParsingException with a custom message.
     *
     * @param message the detail message
     */
    public ParsingException(final String message) {
        super(message);
    }

    /**
     * Default constructor with a standard parsing error message.
     */
    public ParsingException() {
        super("Parsing failed");
    }
}
