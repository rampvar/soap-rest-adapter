package com.soaprestadapter.processor;

import com.soaprestadapter.service.CobolAttributeService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import static org.mockito.Mockito.*;

public class CommonProcessorTest {

    @Test
    void shouldProcessWithDifferentXmlStructures() throws Exception {

            // Given
            String operation = "OrderTshirt";
            Exchange exchange = mock(Exchange.class);
            Message in = mock(Message.class);
            when(exchange.getIn()).thenReturn(in);
            when(in.getHeader("operation", String.class)).thenReturn(operation);
            String xml = "<OrderTshirt>\n" +
                    "         <size>M</size>\n" +
                    "         <email>test@gmail.com</email>\n" +
                    "         <name>dora</name>\n" +
                    "         <address1>citypalace</address1>\n" +
                    "         <address2>link road</address2>\n" +
                    "         <city>pune</city>\n" +
                    "         <stateOrProvince>maharashtra</stateOrProvince>\n" +
                    "         <postalCode>413003</postalCode>\n" +
                    "         <country>india</country>\n" +
                    "      </OrderTshirt>\n";
            when(in.getBody(String.class)).thenReturn(xml);

            CobolAttributeService service = mock(CobolAttributeService.class);

            when(service.getPayloadOne(operation)).thenReturn("{\"operationName\": \"OrderTshirt\", \"programName\": \"sample_cobol\"}");
            when(service.getPayloadTwo(operation)).thenReturn("{\"orderTshirt\": {      \"size\": {        \"datatype\": \"string\",        \"length\": 3      },      \"email\": {        \"datatype\": \"string\",        \"length\": 100      },      \"name\": {        \"datatype\": \"string\",        \"length\": 100      },      \"address1\": {        \"datatype\": \"string\",        \"length\": 100      },      \"address2\": {        \"datatype\": \"string\",        \"length\": 100      },      \"city\": {        \"datatype\": \"string\",        \"length\": 50      },      \"stateOrProvince\": {        \"datatype\": \"string\",        \"length\": 50      },      \"postalCode\": {        \"datatype\": \"string\",        \"length\": 20      },      \"country\": {        \"datatype\": \"string\",        \"length\": 50      }    } }");

            CommonProcessor processor = new CommonProcessor(service);

            // When
            processor.process(exchange);

            // Then
            ArgumentCaptor<Map<String, Object>> mapCaptor = ArgumentCaptor.forClass(Map.class);
            verify(exchange).setProperty(eq("mapWithCobolJsonAttribute"), mapCaptor.capture());
            verify(exchange).setProperty(eq("mapWithPayload"), anyMap());
        }
    }
