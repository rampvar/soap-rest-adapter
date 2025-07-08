package com.soaprestadapter.interceptor;

import com.soaprestadapter.Repository.UserRepository;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Message;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;

/**
 * This interceptor checks if the user making the SOAP request is entitled to perform the requested operation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEntitlementInterceptor implements Processor {
    /**
     *  Initializes the interceptor with the user repository.
     */
    private final UserRepository userRepository;

    private String findUserIdRecursive(final Element node) {
        if ("userId".equalsIgnoreCase(node.getLocalName())) {
            return node.getTextContent().trim();
        }
        for (int i = 0; i < node.getChildNodes().getLength(); i++) {
            if (node.getChildNodes().item(i) instanceof Element) {
                String found = findUserIdRecursive((Element) node.getChildNodes().item(i));
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

    /**
     * Checks if the user making the SOAP request is entitled to perform the requested operation.
     * @param exchange  the message exchange
     * @throws Exception    if the user is not entitled
     */
    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Inside Processing User Entitlement Interceptor");

        Message cxfMessage = exchange.getIn().getHeader("CamelCxfMessage", Message.class);

        if (!(cxfMessage instanceof SoapMessage soapMessage)) {
            throw new RuntimeException("Invalid SOAP message");
        }

        if (soapMessage.getHeaders() == null || soapMessage.getHeaders().isEmpty()) {
            throw new RuntimeException("Missing SOAP header");
        }

        List<Header> headers = soapMessage.getHeaders();

        String userId = null;

        for (Header header : headers) {
            if (header.getObject() instanceof Element) {
                userId = findUserIdRecursive((Element) header.getObject());
                if (userId != null) {
                    break;
                }
            }
        }

        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("userId not found in SOAP request");
        }

        log.info("User Id from SOAP XML : User ID : {}", userId);

        boolean entitled = userRepository.isUserEntitled(Long.valueOf(userId));
        if (!entitled) {
            throw new RuntimeException("User not entitled");
        }

    }
}
