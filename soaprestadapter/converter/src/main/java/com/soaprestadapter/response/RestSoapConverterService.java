package com.soaprestadapter.response;

/**
 * Service interface for converting REST JSON responses into SOAP-compatible XML.
 */
public interface RestSoapConverterService {

    /**
     * Converts a REST JSON response string into a SOAP-compatible XML string.
     *
     * @param restJsonResponse the JSON response string from the REST API
     * @param cls              the target class type for mapping the response
     * @param <T>              the type to which the JSON should be deserialized before XML conversion
     * @return the resulting SOAP XML string
     */
    <T> String convertRestResponseToSoapXml(String restJsonResponse, Class<T> cls);

}
