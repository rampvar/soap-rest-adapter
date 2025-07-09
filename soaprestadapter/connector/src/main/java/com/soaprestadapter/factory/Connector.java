package com.soaprestadapter.factory;

import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * connector interface
 */
public interface Connector {

    /**
     * run generatePayload
     *
     * @param jsonPayload
     * @param requestPayload
     * @return string
     */
    String generatePayload(Map<String, Object> jsonPayload, Map<String, String> requestPayload);

    /**
     * run generatePayload
     *
     * @param restPayload
     * @param requestPayload
     * @return string
     */
    ResponseEntity<String> sendRequest(String restPayload, Map<String, String> requestPayload);
}
