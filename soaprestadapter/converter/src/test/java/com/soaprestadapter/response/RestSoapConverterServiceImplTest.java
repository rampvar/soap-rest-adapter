package com.soaprestadapter.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.exception.ParsingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
//import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RestSoapConverterServiceImplTest {

    @InjectMocks
    private RestSoapConverterServiceImpl restSoapConverterService;

    private static final Logger LOGGER = LoggerFactory.getLogger(RestSoapConverterServiceImplTest.class);

    @Mock
    private ObjectMapper mapper;

    @Test
    public void convertRestResponseToSoapXmlSuccess(){
        String testJson="{"
                + "\"orderId\": \"101\","
                + "\"status\": \"Alice\","
                + "\"size\": \"M\""
                + "}";
        String response="";
//        response = restSoapConverterService.convertRestResponseToSoapXml(testJson, TrackOrderResponse.class);
        assertTrue(response.contains("orderId"), String.valueOf(true));
    }

    @Test
    public void convertRestResponseToSoapXmlFail() throws Exception {
        String testJson="{"
                + "\"orderId1\": \"101\","
                + "\"status1\": \"Alice\","
                + "\"size1\": \"M\""
                + "}";
//        assertThrows(ParsingException.class, () -> restSoapConverterService.convertRestResponseToSoapXml(testJson, TrackOrderResponse.class));
    }
}