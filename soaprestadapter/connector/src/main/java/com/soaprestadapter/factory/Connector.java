package com.soaprestadapter.factory;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface Connector {

    String generatePayload(Map<String, Object> inputData,Map<String, String> inputDataTwo);
    ResponseEntity<String> sendRequest(String payload,Map<String, String> inputData);
}
