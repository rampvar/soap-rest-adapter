package com.soaprestadapter.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

/**
 * PaddingWithJson class process the map and add padding as per json specification.
 */
public class PaddingWithJson {

    /**
     * processPayload method
     * @param json and
     * @param map
     * @return string
     */
    public Map<String, Object> processPayload(final String json, final Map<String, Object> map) {
        Map<String, Object> inputMap = map;
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> root = mapper.readValue(json, new TypeReference<>() { } );
            Map.Entry<String, Object> rootEntry = root.entrySet().iterator().next();
            Map<String, Object> payloadWithJson = (Map<String, Object>)  rootEntry.getValue();

            for (Map.Entry<String, Object> entry : payloadWithJson.entrySet()) {
                String key = entry.getKey();
                Map<String, Object> spec = (Map<String, Object>) entry.getValue();
                String datatype = String.valueOf(spec.get("datatype"));
                int length = (int) spec.get("length");

                if (inputMap.containsKey(key)) {
                    Object originalValue = inputMap.get(key);
                    String paddedValue = String.valueOf(originalValue);

                    if (paddedValue.length() < length) {
                        if ("string".equalsIgnoreCase(datatype)) {
                            paddedValue = String.format("%-" + length + "s", paddedValue); // right-pad with space
                        } else if ("int".equalsIgnoreCase(datatype)) {
                            paddedValue = String.format("%-" + length + "s", paddedValue)
                                    .replace(' ', '0'); // right-pad with 0
                        }
                        inputMap.put(key, paddedValue); // update value
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while processing json data" + e );
        }
        return inputMap;
    }
}
