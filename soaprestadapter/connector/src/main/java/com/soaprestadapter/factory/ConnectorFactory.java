package com.soaprestadapter.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soaprestadapter.config.ConnectorProperties;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ConnectorFactory class
 */
@RequiredArgsConstructor
@Service
public class ConnectorFactory {

    /**
     * connectorMap
     */
    private final Map<String, Connector> connectorMap;

    /**
     * properties
     */
    private final ConnectorProperties properties;


    /**
     * run execute
     *
     * @param jsonPayload
     * @param requestPayload
     * @param jwtToken
     * @return string
     */
    public String execute(final Map<String, Object> jsonPayload,
                          final Map<String, String> requestPayload,
                          final String jwtToken) throws JsonProcessingException {
        String key = properties.getConnector();
        Connector connector = connectorMap.get(key);
        String body;
        if (connector != null) {
            String payload = connector.generatePayload(jsonPayload, requestPayload);
            body = connector.sendRequest(payload, requestPayload, jwtToken);
        } else {
            throw new IllegalArgumentException("No connector found for key: " + key);
        }
        return body;
    }

}
