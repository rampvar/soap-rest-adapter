package com.soaprestadapter.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.soaprestadapter.factory.ConnectorFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * RequestDispatcher class
 */
@Slf4j
@RequiredArgsConstructor
@Component("requestDispatcher")
public class RequestDispatcher {

    /**
     * ConnectorFactory
     */
    private final ConnectorFactory connectorFactory;


    /**
     * run method
     *
     * @param inputData1
     * @param inputData2
     * @return string
     */
    public String run(final Map<String, Object> inputData1,
                      final Map<String, String> inputData2) throws JsonProcessingException {
        return connectorFactory.execute(inputData1, inputData2);
    }
}