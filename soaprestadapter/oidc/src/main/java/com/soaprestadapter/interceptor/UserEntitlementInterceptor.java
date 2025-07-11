package com.soaprestadapter.interceptor;

import com.soaprestadapter.factory.EntitlementFactory;
import com.soaprestadapter.service.EntitlementService;
import com.soaprestadapter.service.UserRoleGroupEntitlementService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.message.Message;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This interceptor checks if the user making the SOAP request is entitled to perform the requested operation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserEntitlementInterceptor implements Processor {
    /**
     * Initializes the interceptor with the user repository.
     */
    private final EntitlementFactory entitlementFactory;

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
     *
     * @param exchange the message exchange
     * @throws Exception if the user is not entitled
     */
    @Override
    public void process(final Exchange exchange) throws Exception {
        log.info("Inside Processing User Entitlement Interceptor");

        Message cxfMessage = exchange.getIn().getHeader("CamelCxfMessage", Message.class);

        SoapMessage soapMessage = validateSoapMessage(cxfMessage);

        List<Header> headers = soapMessage.getHeaders();

        String userId = null;
        String userName = null;
        String action = null;
        boolean entitled;

        for (Header header : headers) {
            if (header.getObject() instanceof Element) {
                userId = findUserIdRecursive((Element) header.getObject());
                Map<String, String> values = findUserNameAndAction((Element) header.getObject());
                if (values.containsKey("userName")) {
                    userName = values.get("userName");
                }
                if (values.containsKey("action")) {
                    action = values.get("action");
                }
                if (allUserAttributesPresent(userId, userName, action)) {
                    break;
                }
            }
        }

        // Step 3: Extract Authorization header (HTTP)



        if (userId == null || userId.isEmpty()) {
            throw new RuntimeException("userId not found in SOAP request");
        }

        log.info("User Id from SOAP XML : User ID : {}", userId);

        EntitlementService entitlementService = entitlementFactory.getEntitlementService();

        if (entitlementService instanceof UserRoleGroupEntitlementService) {
            log.info("Inside User group validation");
            entitled = entitlementService.isUserEntitled(userId, null);
        } else {
            entitled = entitlementService.isUserEntitled(userName, action);
        }

        if (!entitled) {
            throw new RuntimeException("User not entitled");
        }

    }

    private boolean allUserAttributesPresent(final String userId,
                                             final String userName, final String action) {
        return userId != null && userName != null && action != null;
    }

    private SoapMessage validateSoapMessage(final Message message) {

        if (!(message instanceof SoapMessage soapMessage)) {
            throw new RuntimeException("Invalid SOAP message");
        }

        if (soapMessage.getHeaders() == null || soapMessage.getHeaders().isEmpty()) {
            throw new RuntimeException("Missing SOAP header");
        }

        return soapMessage;
    }

    private Map<String, String> findUserNameAndAction(final Element root) {
        Map<String, String> result = new HashMap<>();
        traverseAndFind(root, result);
        return result;
    }

    private void traverseAndFind(final Node node, final Map<String, String> result) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getLocalName() != null ? node.getLocalName().toLowerCase() :
                    node.getNodeName().toLowerCase();
            String value = node.getTextContent().trim();

            if ("username".equalsIgnoreCase(nodeName) && !result.containsKey("userName")) {
                result.put("userName", value);
            } else if ("action".equalsIgnoreCase(nodeName) && !result.containsKey("action")) {
                result.put("action", value);
            }
        }

        // Recurse into child nodes
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            traverseAndFind(children.item(i), result);
        }
    }
}
