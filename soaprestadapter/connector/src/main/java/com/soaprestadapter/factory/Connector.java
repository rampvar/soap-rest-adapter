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
     * @param inputData
     * @param inputDataTwo
     * @return string
     */
    String generatePayload(Map<String, Object> inputData, Map<String, String> inputDataTwo);

    /**
     * run generatePayload
     *
     * @param payload
     * @param inputData
     * @return string
     */
    ResponseEntity<String> sendRequest(String payload, Map<String, String> inputData);
}
