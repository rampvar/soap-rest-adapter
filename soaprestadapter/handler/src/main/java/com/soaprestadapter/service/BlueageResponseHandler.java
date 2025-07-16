package com.soaprestadapter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.FetchResponseCopybookDataStrategy;
import com.soaprestadapter.factory.ResponseHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * class for handling Blueage REST responses.
 */
@Slf4j
@Component("BLUEAGE-RESPONSE")
@RequiredArgsConstructor
public class BlueageResponseHandler implements ResponseHandler {

    /**
     * repository to get response copybook data
     */
    private  final FetchResponseCopybookDataStrategy repository;

    /**
     * convert rest response to Blueage format
     * @param responseBody - rest response body
     * @return - converted blueage format json string
     */
    @Override
    public String convertRestResponse
    (final String responseBody, final String operationName) throws JsonProcessingException {
        log.info("Rest raw response from target system{}", responseBody);
        JSONObject json = new JSONObject();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            JsonNode messages = root.path("messages");

            for (JsonNode message : messages) {
                if (operationName.equals(message.path("command").asText())) {
                    JsonNode maps = message.path("maps");
                    for (JsonNode map : maps) {
                        JsonNode fields = map.path("fields");
                        for (JsonNode field : fields) {
                            String id = field.path("id").asText();
                            String data = field.path("data").asText();
                            json.put(id, data);
                        }
                    }
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error converting rest response to Blueage format", e);
        }
        // Perform custom conversion logic when required.
        log.info("After conversion to Blueage format: {}", json);
        return json.toString();
    }

}
