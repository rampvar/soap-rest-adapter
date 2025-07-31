package com.soaprestadapter.interceptor;

import com.soaprestadapter.Repository.UserRepository;
import com.soaprestadapter.factory.EntitlementFactory;
import com.soaprestadapter.service.AwsIamCloudEntitlementService;
import com.soaprestadapter.service.AwsIamLocalEntitlementService;
import com.soaprestadapter.factory.EntitlementService;
import com.soaprestadapter.service.UserRoleGroupEntitlementService;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserInterceptorTest {
    @Mock
    private EntitlementFactory entitlementFactory;

    @Mock
    private UserRoleGroupEntitlementService userRoleGroupEntitlementService;

    @Mock
    private AwsIamCloudEntitlementService iamActualEntitlementService;

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


    @Test
    void testProcessSuccessWhenEntitled() throws Exception {
        // Arrange
        // 1. Setup entitlementService mock
        when(entitlementFactory.getEntitlementService()).thenReturn(entitlementService);
        when(entitlementService.isUserEntitled("john", "read")).thenReturn(true);

        // 2. Create a real DOM Element with userId, userName, action
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element root = doc.createElement("AuthHeader");

        Element userIdEl = doc.createElement("userId");
        userIdEl.appendChild(doc.createTextNode("123"));
        root.appendChild(userIdEl);

        Element userNameEl = doc.createElement("userName");
        userNameEl.appendChild(doc.createTextNode("john"));
        root.appendChild(userNameEl);

        Element actionEl = doc.createElement("action");
        actionEl.appendChild(doc.createTextNode("read"));
        root.appendChild(actionEl);

        Header soapHeader = new Header(new QName("auth"), root);
        SoapMessage realSoapMessage = new SoapMessage(new MessageImpl());
        realSoapMessage.getHeaders().add(soapHeader);

        // 4. Mock protocol headers for JWT
        Map<String, List<String>> jwtHeaders = Map.of("jwt_token", List.of("mocked-jwt"));
        realSoapMessage.put(org.apache.cxf.message.Message.PROTOCOL_HEADERS,jwtHeaders);

        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader(eq("CamelCxfMessage"), eq(org.apache.cxf.message.Message.class)))
                .thenReturn(realSoapMessage);

        // Act
        assertDoesNotThrow(() -> interceptor.process(exchange));

        // Assert
        verify(entitlementService).isUserEntitled("john", "read");
        verify(camelMessage).setHeader("Authorization", "mocked-jwt");
    }

    @Test
    void testProcessFailureNotEntitled() {
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

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
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

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
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

        when(entitlementFactory.getEntitlementService()).thenReturn(userRoleGroupEntitlementService);
        when(userRoleGroupEntitlementService.isUserEntitled("user123", null)).thenReturn(true);

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
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

        Header invalidHeader = new Header(null, "StringNotXml");
        when(soapMessage.getHeaders()).thenReturn(List.of(invalidHeader));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("userId not found in SOAP request", ex.getMessage());
    }

    @Test
    void testEmptySoapHeaders() {
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

        when(soapMessage.getHeaders()).thenReturn(List.of());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("Missing SOAP header", ex.getMessage());
    }
    @Test
    void testInvalidSoapMessageType() {
        when(exchange.getIn()).thenReturn(camelMessage);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(soapMessage);

        org.apache.cxf.message.Message nonSoap = mock(org.apache.cxf.message.Message.class);
        when(camelMessage.getHeader("CamelCxfMessage", org.apache.cxf.message.Message.class)).thenReturn(nonSoap);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> interceptor.process(exchange));
        assertEquals("Invalid SOAP message", ex.getMessage());
    }
}
