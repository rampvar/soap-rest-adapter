package com.soaprestadapter.exception;

/**
 * Exception thrown when access to a resource is denied.
 */
public class AccessDeniedException extends RuntimeException {

    /**
     * Default constructor with a standard access denied message.
     */
    public AccessDeniedException() {
        super("Access denied");
    }

    /**
     * Constructs a new AccessDeniedException with a custom message.
     *
     * @param message the detail message
     */
    public AccessDeniedException(final String message) {
        super(message);
    }

    /**
     * Constructs a new AccessDeniedException with a custom message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public AccessDeniedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
