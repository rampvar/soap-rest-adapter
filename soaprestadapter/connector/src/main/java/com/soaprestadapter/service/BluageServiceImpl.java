package com.soaprestadapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.soaprestadapter.factory.Connector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Component("BLUAGE")
public class BluageServiceImpl implements Connector {

    private final RestClientService service;

    @Override
    public String generatePayload(Map<String, Object> inputDataOne,Map<String, String> inputDataTwo) {
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
            for (Map.Entry<String, Object> entry : inputDataOne.entrySet()) {
                ObjectNode field = mapper.createObjectNode();
                field.put("component", "programName");
                field.put("id", entry.getKey());
                field.put("value", String.valueOf(entry.getValue()));
                fieldsArray.add(field);
            }
            root.set("fields", fieldsArray);
            root.put("transactionId", "");

            // Convert to JSON string
            jsonOutput = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root);
            System.out.println(jsonOutput);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error occurred while generating input for bluage" + e);
        }
        return jsonOutput;
    }

    @Override
    public ResponseEntity<String> sendRequest(String payload, Map<String, String> inputData1) {
        ResponseEntity<String> process = service.process("BLUAGE", inputData1.get("operationName"), payload);
        return process;
    }
}