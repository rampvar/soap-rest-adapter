package com.soaprestadapter.utility;

import com.soaprestadapter.exception.AccessDeniedException;
import com.soaprestadapter.exception.NotFoundException;
import com.soaprestadapter.exception.ParsingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;


/**
 * Enum for mapping exceptions to standardized error codes and messages.
 */
public enum ErrorCodeMapper {

    /** Internal server error. */
    INTERNAL_ERROR("ERR-500", "Internal server error"),

    /** Invalid request. */
    INVALID_REQUEST("ERR-400", "Invalid request"),

    /** Authentication failed. */
    UNAUTHORIZED("ERR-401", "Authentication failed"),

    /** Access denied. */
    FORBIDDEN("ERR-403", "Access denied"),

    /** Requested resource not found. */
    NOT_FOUND("ERR-404", "Requested resource not found"),

    /** Invalid or malformed input. */
    PARSING_ERROR("ERR-422", "Invalid or malformed input"),

    /** Request timed out. */
    TIMEOUT("ERR-408", "Request timed out"),

    /** External service is unavailable. */
    SERVICE_UNAVAILABLE("ERR-503", "External service is unavailable");

    /** This is an error code. */
    private final String code;
    /** This is an error message. */
    private final String message;

    /**
     * Constructor to initialize the error code and message.
     *
     * @param codeParam the error code
     * @param messageParam the error message
     */
    ErrorCodeMapper(final String codeParam, final String messageParam) {
        this.code = codeParam;
        this.message = messageParam;
    }


    /**
     * Returns the error code.
     *
     * @return error code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the error message.
     *
     * @return error message string
     */
    public String getMessage() {
        return message;
    }

    /**
     * Maps an exception to an appropriate error code.
     *
     * @param e the exception to map
     * @return the corresponding ErrorCodeMapper
     */
    public static ErrorCodeMapper fromException(final Exception e) {
        if (e instanceof IllegalArgumentException) {
            return INVALID_REQUEST;
        } else if (e instanceof ParsingException) {
            return PARSING_ERROR;
        } else if (e instanceof SocketTimeoutException) {
            return TIMEOUT;
        } else if (e instanceof ConnectException) {
            return SERVICE_UNAVAILABLE;
        } else if (e instanceof SecurityException) {
            return UNAUTHORIZED;
        } else if (e instanceof AccessDeniedException) {
            return FORBIDDEN;
        } else if (e instanceof NotFoundException) {
            return NOT_FOUND;
        }
        return INTERNAL_ERROR;
    }
}
