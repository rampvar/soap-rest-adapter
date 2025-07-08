package com.soaprestadapter.utility;

import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaddingWithJsonTest {

    @Test
    void shouldHandleMapWithDifferentDataTypesInJsonPayload() {
        String json = "{\"payload\": {\"name\": {\"datatype\": \"string\", \"length\": 10}, " +
                "\"age\": {\"datatype\": \"int\", \"length\": 3}, " +
                "\"salary\": {\"datatype\": \"string\", \"length\": 5}}}";


        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("name", "John");
        inputMap.put("age", 30);
        inputMap.put("salary", 5000);

        PaddingWithJson paddingWithJson = new PaddingWithJson();
        Map<String, Object> resultMap = paddingWithJson.processPayload(json, inputMap);

        assertEquals("John      ", resultMap.get("name"));
        assertEquals("300", resultMap.get("age"));
        assertEquals("5000 ", resultMap.get("salary"));
    }

    @Test
    void shouldHandleMapWithStringDatatypeAndLengthLessThanSpecified() {
        String json = "{\"payload\": {\"name\": {\"datatype\": \"string\", \"length\": 10}}}";

        Map<String, Object> inputMap = new HashMap<>();
        inputMap.put("name", "John");

        PaddingWithJson paddingWithJson = new PaddingWithJson();
        Map<String, Object> resultMap = paddingWithJson.processPayload(json, inputMap);

        assertEquals("John      ", resultMap.get("name"));
    }
}
