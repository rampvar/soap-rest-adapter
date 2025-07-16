package com.soaprestadapter.exception;

import lombok.Getter;

/**
 *
 */
@Getter
public class JwtValidationException extends RuntimeException {

    /**
     * errorCode
     */
    private final String errorCode;

    /**
     * Constructor
     *
     * @param message
     * @param code*/

    public JwtValidationException(final String message, final String code) {
        super(message);
        this.errorCode = code;
    }

}
