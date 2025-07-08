package com.soaprestadapter.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.soaprestadapter.config.ConnectorProperties;

import java.util.Map;

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
     * @param inputDataOne
     * @param inputDataTwo
     * @return string
     */
    public String execute(final Map<String, Object> inputDataOne, final Map<String, String> inputDataTwo) {
        String key = properties.getConnector();
        Connector connector = connectorMap.get(key);
        String body;
        if (connector != null) {
            String payload = connector.generatePayload(inputDataOne, inputDataTwo);
            ResponseEntity<String> stringResponseEntity = connector.sendRequest(payload, inputDataTwo);
            body = stringResponseEntity.getBody();
        } else {
            throw new IllegalArgumentException("No connector found for key: " + key);
        }
        return body;
    }

}
