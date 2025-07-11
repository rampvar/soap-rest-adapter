package com.soaprestadapter.interceptor;

import com.soaprestadapter.Repository.UserRepository;
import com.soaprestadapter.factory.EntitlementFactory;
import com.soaprestadapter.service.AwsIamActualEntitlementService;
import com.soaprestadapter.service.AwsIamLocalEntitlementService;
import com.soaprestadapter.service.EntitlementService;
import com.soaprestadapter.service.UserRoleGroupEntitlementService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Element;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInterceptorTest {
    @Mock
    private EntitlementFactory entitlementFactory;

    @Mock
    private UserRoleGroupEntitlementService userRoleGroupEntitlementService;

    @Mock
    private AwsIamActualEntitlementService iamActualEntitlementService;

    @Mock
    private AwsIamLocalEntitlementService localEntitlementService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntitlementService entitlementService;

    @Mock
    private Exchange exchange;

    @Mock
    private Message camelMessage;

    @Mock
    private SoapMessage soapMessage;

    @InjectMocks
    private UserEntitlementInterceptor interceptor;


    @BeforeEach
    void setUp() {
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);
    }

    @Test
    void testProcessSuccess() throws Exception {
        Element userId = createMockElement("userId", "user123");

        Element root = mock(Element.class);
        when(root.getLocalName()).thenReturn("root");
        when(root.getChildNodes()).thenReturn(new NodeListBuilder()
                .add(userId).build());

        Header header = new Header(null, root);
        when(soapMessage.getHeaders()).thenReturn(List.of(header));

        when(entitlementFactory.getEntitlementService()).thenReturn(userRoleGroupEntitlementService);

        when(userRoleGroupEntitlementService.isUserEntitled(any(), any())).thenReturn(true);

        interceptor.process(exchange);

        verify(userRoleGroupEntitlementService, atLeastOnce()).isUserEntitled(anyString(), anyString());
    }

    @Test
    void testProcessFailureNotEntitled() {
        Element userIdElement = createMockElement("userId", "user123");


        Element rootElement = mock(Element.class);
        when(rootElement.getLocalName()).thenReturn("headerRoot");
        when(rootElement.getChildNodes()).thenReturn(new NodeListBuilder()
                .add(userIdElement)
                .build());

        Header header = new Header(null, rootElement);
        when(soapMessage.getHeaders()).thenReturn(List.of(header));

        when(entitlementFactory.getEntitlementService()).thenReturn(userRoleGroupEntitlementService);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("User not entitled", ex.getMessage());
    }

    @Test
    void testMissingUserId() {
        Element userNameElement = mock(Element.class);
        when(userNameElement.getLocalName()).thenReturn("userName");
        when(userNameElement.getChildNodes()).thenReturn(new NodeListBuilder().build());


        Element actionElement = mock(Element.class);
        when(actionElement.getLocalName()).thenReturn("action");
        when(actionElement.getChildNodes()).thenReturn(new NodeListBuilder().build());

        Element rootElement = mock(Element.class);
        when(rootElement.getLocalName()).thenReturn("headerRoot");
        when(rootElement.getChildNodes()).thenReturn(new NodeListBuilder()
                .add(userNameElement)
                .add(actionElement)
                .build());

        Header header = new Header(null, rootElement);
        when(soapMessage.getHeaders()).thenReturn(List.of(header));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("userId not found in SOAP request", ex.getMessage());
    }

    // Helper method to mock an XML element
    private Element createMockElement(String name, String value) {
        Element element = mock(Element.class);
        when(element.getLocalName()).thenReturn(name);
        when(element.getTextContent()).thenReturn(value);
        when(element.getChildNodes()).thenReturn(new NodeListBuilder().build());
        return element;
    }

    @Test
    void testUserRoleGroupEntitlementServicePath() throws Exception {
        when(entitlementFactory.getEntitlementService()).thenReturn(userRoleGroupEntitlementService);
        when(userRoleGroupEntitlementService.isUserEntitled("user123", null)).thenReturn(true);
        when(userRoleGroupEntitlementService.isUserEntitled("user123", "read")).thenReturn(true);

        Element userId = createMockElement("userId", "user123");

        Element root = mock(Element.class);
        when(root.getLocalName()).thenReturn("header");
        when(root.getChildNodes()).thenReturn(new NodeListBuilder()
                .add(userId).build());

        Header header = new Header(null, root);
        when(soapMessage.getHeaders()).thenReturn(List.of(header));

        interceptor.process(exchange);

        verify(userRoleGroupEntitlementService).isUserEntitled("user123", null);
    }

    @Test
    void testHeaderObjectNotElement() {
        Header invalidHeader = new Header(null, "StringNotXml");
        when(soapMessage.getHeaders()).thenReturn(List.of(invalidHeader));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("userId not found in SOAP request", ex.getMessage());
    }

    @Test
    void testEmptySoapHeaders() {
        when(soapMessage.getHeaders()).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("Missing SOAP header", ex.getMessage());
    }
    @Test
    void testInvalidSoapMessageType() {
        org.apache.cxf.message.Message nonSoap = mock(org.apache.cxf.message.Message.class);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(nonSoap);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("Invalid SOAP message", ex.getMessage());
    }
}
