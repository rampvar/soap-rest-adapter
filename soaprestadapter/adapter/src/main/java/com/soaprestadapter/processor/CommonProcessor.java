package com.soaprestadapter.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.service.CobolAttributeService;
import com.soaprestadapter.utility.PaddingWithJson;
import com.soaprestadapter.utility.XmlToMapParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommonProcessor implements Processor {

    private final CobolAttributeService service;

    @Override
    public void process(Exchange exchange) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String operation = exchange.getIn().getHeader("operation", String.class);
        XmlToMapParser parser = new XmlToMapParser();
        Map<String, Object> stringObjectMap = parser.parseXml(exchange.getIn().getBody(String.class));

        String payloadOne = getPayloadOne(operation);
        String jsonData = getPayloadTwo(operation);

        Map<String, String> payload1Map = mapper.readValue(payloadOne, Map.class);
        PaddingWithJson paddingWithJsonSpec = new PaddingWithJson();
        Map<String, Object> updatedMap = paddingWithJsonSpec.processPayload(jsonData, stringObjectMap);
        exchange.setProperty("map1", updatedMap);
        exchange.setProperty("map2", payload1Map);
    }

    private String getPayloadTwo(String operationName) {
        return service.getPayloadTwo(operationName);
    }

    private String getPayloadOne(String operationName) {
        return service.getPayloadOne(operationName);
    }
}
