package com.soaprestadapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.soaprestadapter.factory.Connector;
import com.soaprestadapter.factory.ResponseHandler;
import com.soaprestadapter.factory.ResponseHandlerFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * BluageServiceImpl class
 */
@RequiredArgsConstructor
@Component("BLUAGE")
@Slf4j
public class BluageServiceImpl implements Connector {

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
    public String generatePayload(final Map<String, Object> jsonPayload, final Map<String, String> requestPayload) {
        String jsonOutput;
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Root object
            ObjectNode root = mapper.createObjectNode();
            root.put("attentionKey", "");
            root.put("activeRecord", "");
            root.put("activeField", "");
            root.put("cursorPosition", 0);

            // Fields array
            ArrayNode fieldsArray = mapper.createArrayNode();
            for (Map.Entry<String, Object> entry : jsonPayload.entrySet()) {
                ObjectNode field = mapper.createObjectNode();
                field.put("component", requestPayload.get("programName"));
                field.put("id", entry.getKey());
                field.put("value", String.valueOf(entry.getValue()));
                fieldsArray.add(field);
            }
            root.set("fields", fieldsArray);
            root.put("transactionId", "");

            // Convert to JSON string
            jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            log.info("Generated payload for BLUAGE: {}", jsonOutput);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred while generating input for bluage" + e);
        }
        return jsonOutput;
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
        ResponseEntity<String> process = service.process(
                "BLUAGE",
                requestPayload.get("operationName"),
                payload,
                jwtToken);
        ResponseHandler responseHandler = responseHandlerFactory.getResponseHandler("BLUEAGE-RESPONSE");
        if (responseHandler != null) {
            return responseHandler.convertRestResponse(process.getBody(), requestPayload.get("operationName"));
        }
        return null;
    }
}