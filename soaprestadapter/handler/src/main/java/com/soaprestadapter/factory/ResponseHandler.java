package com.soaprestadapter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Interface for handling REST responses.
 */
public interface ResponseHandler {
    /**
     * Converts the given REST response body to the specified object type.
     *
     * @param responseBody The REST response body as a string.
     * @param operationName The name of the operation for which the response is being handled.
     * @return The converted json string.
     */
    String convertRestResponse(final String responseBody, final String operationName) throws JsonProcessingException;
}
