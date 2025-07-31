package com.soaprestadapter.exception;

/**
 * Custom exception thrown when parsing fails during REST calls.
 */
public class IoException extends RuntimeException {

    /**
     * HTTP status code returned from the REST service.
     */
    private final int statusCode;

    /**
     * Response body returned from the REST service.
     */
    private final String responseBody;

    /**
     * Constructs a IoException with the specified status code and response body.
     *
     * @param statusCodeParam the HTTP status code from the REST response
     * @param responseBodyParam the response body returned from the REST service
     */
    public IoException(final int statusCodeParam, final String responseBodyParam) {
        super(responseBodyParam);
        this.statusCode = statusCodeParam;
        this.responseBody = responseBodyParam;
    }

    /**
     * Constructs a IoException with the specified status code, response body, and detail message.
     *
     * @param statusCodeParam the HTTP status code from the REST response
     * @param responseBodyParam the response body returned from the REST service
     * @param message the detail message
     */
    public IoException(final int statusCodeParam, final String responseBodyParam, final String message) {
        super(message);
        this.statusCode = statusCodeParam;
        this.responseBody = responseBodyParam;
    }

    /**
     * Returns the HTTP status code.
     *
     * @return the HTTP status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Returns the response body.
     *
     * @return the response body
     */
    public String getResponseBody() {
        return responseBody;
    }
}


