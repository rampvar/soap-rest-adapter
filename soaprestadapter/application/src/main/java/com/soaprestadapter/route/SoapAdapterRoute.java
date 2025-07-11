package com.soaprestadapter.route;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.cxf.binding.soap.SoapMessage;
import org.springframework.stereotype.Component;

/**
 * SoapAdapterRoute class. Soap ws endpoint route.
 */
@Component
public class SoapAdapterRoute extends RouteBuilder {

    /**
     * INVENTORY_ID constant for inventory service.
     */
    public static final String INVENTORY_ID = "InventoryServiceWsdlRoute";

    /**
     * HELLO_ID constant for hello service.
     */
    public static final String HELLO_ID = "HelloServiceWsdlRoute";

    @Override
    public void configure() throws Exception {
        from("cxf:{{camel.cxf.inventory}}" +
                "?serviceClass=org.mulesoft.tshirt_service.TshirtServicePortType" +
                "&dataFormat=payload")
                .routeId(INVENTORY_ID)
                .log("inside inventory...")
                .setHeader("operation").simple("${header.operationName}")
                .process(exchange -> {
                    Message message = exchange.getMessage();
                    SoapMessage camelCXFMessage = (SoapMessage) message.getHeader("CamelCXFMessage");
                    String jwtToken = (String) camelCXFMessage.get("jwt_token");
                    if (jwtToken != null) {
                        exchange.getIn().setHeader("Authorization", jwtToken);
                    }
                })
                .toD("direct:${header.operationName}");

        from("cxf:{{camel.cxf.hello}}" +
                "?serviceClass=com.example.wsdl.HelloPortType" +
                "&dataFormat=payload")
                .routeId(HELLO_ID)
                .setHeader("operation").simple("${header.operationName}")
                .process(exchange -> {
                    Message message = exchange.getMessage();
                    SoapMessage camelCXFMessage = (SoapMessage) message.getHeader("CamelCXFMessage");
                    String jwtToken = (String) camelCXFMessage.get("jwt_token");
                    if (jwtToken != null) {
                        exchange.getIn().setHeader("Authorization", jwtToken);
                    }
                })
                .toD("direct:${header.operationName}");
    }
}
