package com.soaprestadapter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class AmtServiceImplTest {

    @Test
    void shouldHandleJsonPayloadWithNestedObjects() throws IOException, URISyntaxException {

        AmtServiceImpl amtService = new AmtServiceImpl(null, null);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonPayload = new HashMap<>();
        jsonPayload.put("email", "abc@gmail.com    ");
        jsonPayload.put("orderId", "123  ");
        Map<String, String> requestPayload = new HashMap<>();
        requestPayload.put("operationName", "TrackOrder");
        requestPayload.put("programName", "sample_cobol2");

        String payload = amtService.generatePayload(jsonPayload, requestPayload);

        ClassLoader classLoader = AmtServiceImplTest.class.getClassLoader();
        String expectedJson = Files.readString(Paths.get(classLoader.getResource("amtPayload.json").toURI()));
        assertEquals(payload.length(), expectedJson.length());


    }
}