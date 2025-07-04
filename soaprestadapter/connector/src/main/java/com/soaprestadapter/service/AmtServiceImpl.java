package com.soaprestadapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.factory.Connector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
@Component("AMT")
public class AmtServiceImpl implements Connector {

    private final RestClientService service;

    @Override
    public String generatePayload(Map<String, Object> inputDataOne, Map<String, String> inputDataTwo) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> wrapper = Map.of(
                    "programName", inputDataTwo.get("programName"),
                    "operationName", inputDataTwo.get("operationName"),
                    "parameters", inputDataOne
            );
            return mapper.writeValueAsString(wrapper);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load AMT payload", e);
        }
    }

    @Override
    public ResponseEntity<String> sendRequest(String payload, Map<String, String> inputData1) {
        ResponseEntity<String> process = service.process("AMT", inputData1.get("operationName"), payload);
        return process;
    }
}
