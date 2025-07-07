package com.soaprestadapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.factory.Connector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * AmtServiceImpl class
 */
@RequiredArgsConstructor
@Component("AMT")
public class AmtServiceImpl implements Connector {

    /**
     * RestClientService
     */
    private final RestClientService service;

    /**
     * generatePayload execute
     *
     * @param inputDataOne
     * @param inputDataTwo
     * @return string
     */
    @Override
    public String generatePayload(final Map<String, Object> inputDataOne, final Map<String, String> inputDataTwo) {

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

    /**
     * sendRequest execute
     *
     * @param payload
     * @param inputData1
     * @return ResponseEntity<String>
     */
    @Override
    public ResponseEntity<String> sendRequest(final String payload, final Map<String, String> inputData1) {
        ResponseEntity<String> process = service.process("AMT", inputData1.get("operationName"), payload);
        return process;
    }
}
