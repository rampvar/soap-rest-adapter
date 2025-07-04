package com.soaprestadapter.exception;

/**
 * Exception thrown when a requested resource is not found.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public NotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public NotFoundException(final String message) {
        super(message);
    }

    /**
     * Default constructor with a standard not found message.
     */
    public NotFoundException() {
        super("Resource not found");
    }
}
