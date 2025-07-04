package com.soaprestadapter.client;

import com.soaprestadapter.factory.ConnectorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component("requestDispatcher")
public class RequestDispatcher {

    private final ConnectorFactory connectorFactory;

    public String run(Map<String, Object> inputData1, Map<String, String> inputData2) {
        try {
            String execute = connectorFactory.execute(inputData1, inputData2);
            return execute;
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while executing request dispatcher" + e);
        }
    }
}