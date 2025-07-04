package com.soaprestadapter.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class PaddingWithJson {

    public Map<String, Object> processPayload(String json, Map<String, Object> map1) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> root = mapper.readValue(json, new TypeReference<>() {});
            Map.Entry<String, Object> rootEntry = root.entrySet().iterator().next();
            Map<String, Object> payload2 = (Map<String, Object>)rootEntry.getValue();

            for (Map.Entry<String, Object> entry : payload2.entrySet()) {
                String key = entry.getKey();
                Map<String, Object> spec = (Map<String, Object>) entry.getValue();
                String datatype = String.valueOf(spec.get("datatype"));
                int length = (int) spec.get("length");

                if (map1.containsKey(key)) {
                    Object originalValue = map1.get(key);
                    String paddedValue = String.valueOf(originalValue);

                    if (paddedValue.length() < length) {
                        if ("string".equalsIgnoreCase(datatype)) {
                            paddedValue = String.format("%-" + length + "s", paddedValue); // right-pad with space
                        } else if ("int".equalsIgnoreCase(datatype)) {
                            paddedValue = String.format("%-" + length + "s", paddedValue).replace(' ', '0'); // right-pad with 0
                        }
                        map1.put(key, paddedValue); // update value
                    }
                }
            }
        }catch(JsonProcessingException e) {
            throw new RuntimeException("Error while processing json data"+e);
        }
        return map1;
    }
}
