package com.soaprestadapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.factory.Connector;
import com.soaprestadapter.factory.ResponseHandler;
import com.soaprestadapter.factory.ResponseHandlerFactory;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * AmtServiceImpl class
 */
@RequiredArgsConstructor
@Component("AMT")
@Slf4j
public class AmtServiceImpl implements Connector {

    /**
     * RestClientService
     */
    private final RestClientService service;

    /**
     * ResponseHandlerFactory.
     */
    private final ResponseHandlerFactory responseHandlerFactory;

    /**
     * generatePayload execute
     *
     * @param jsonPayload
     * @param requestPayload
     * @return string
     */
    @Override
    public String generatePayload(final Map<String, Object> jsonPayload,
                                  final Map<String, String> requestPayload) {
        log.info("Inside AMTServiceImpl");
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> wrapper = Map.of(
                    "programName", requestPayload.get("programName"),
                    "operationName", requestPayload.get("operationName"),
                    "parameters", jsonPayload
            );
            log.info("Generated AMT payload: {}", mapper.writeValueAsString(wrapper));
            return mapper.writeValueAsString(wrapper);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load AMT payload", e);
        }
    }

    /**
     * sendRequest execute
     *
     * @param payload
     * @param requestPayload
     * @param jwtToken
     * @return ResponseEntity<String>
     */
    @Override
    public String sendRequest(final String payload,
                              final Map<String, String> requestPayload,
                              final String jwtToken) throws JsonProcessingException {
        ResponseEntity<String> process = service.process("AMT", requestPayload.get("operationName"), payload, jwtToken);
        ResponseHandler responseHandler = responseHandlerFactory.getResponseHandler("AMT-RESPONSE");
        if (responseHandler != null) {
            return responseHandler.convertRestResponse(process.getBody(), requestPayload.get("operationName"));

        }
        return null;
    }
}
