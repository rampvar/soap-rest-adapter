package com.soaprestadapter.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.service.CobolAttributeService;
import com.soaprestadapter.utility.PaddingWithJson;
import com.soaprestadapter.utility.XmlToMapParser;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

/**
 * CommonProcessor class representing common processing in route.
 */
@RequiredArgsConstructor
@Component
public class CommonProcessor implements Processor {

    /**
     * CobolAttributeService
     */
    private final CobolAttributeService service;


    @Override
    public void process(final Exchange exchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String operation = exchange.getIn().getHeader("operation", String.class);
        XmlToMapParser parser = new XmlToMapParser();
        Map<String, Object> stringObjectMap = parser.parseXml(exchange.getIn().getBody(String.class));

        String payloadOne = getPayloadOne(operation);
        String jsonData = getPayloadTwo(operation);

        Map<String, String> payload1Map = mapper.readValue(payloadOne, Map.class);
        PaddingWithJson paddingWithJsonSpec = new PaddingWithJson();
        Map<String, Object> updatedMap = paddingWithJsonSpec.processPayload(jsonData, stringObjectMap);
        exchange.setProperty("mapWithCobolJsonAttribute", updatedMap);
        exchange.setProperty("mapWithPayload", payload1Map);
    }

    private String getPayloadTwo(final String operationName) {
        return service.getPayloadTwo(operationName);
    }

    private String getPayloadOne(final String operationName) {
        return service.getPayloadOne(operationName);
    }
}
