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
     * @param jsonPayload
     * @param requestPayload
     * @return string
     */
    @Override
    public String generatePayload(final Map<String, Object> jsonPayload, final Map<String, String> requestPayload) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> wrapper = Map.of(
                    "programName", requestPayload.get("programName"),
                    "operationName", requestPayload.get("operationName"),
                    "parameters", jsonPayload
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
    public ResponseEntity<String> sendRequest(final String payload, final Map<String, String> requestPayload) {
        ResponseEntity<String> process = service.process("AMT", requestPayload.get("operationName"), payload);
        return process;
    }
}
