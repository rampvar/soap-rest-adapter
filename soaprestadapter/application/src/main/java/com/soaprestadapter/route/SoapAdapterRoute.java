package com.soaprestadapter.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SoapAdapterRoute extends RouteBuilder {

    public static final String INVENTORY_ID = "InventoryServiceWsdlRoute";
    public static final String HELLO_ID = "HelloServiceWsdlRoute";

    @Override
    public void configure() throws Exception {
        from("cxf:{{camel.cxf.inventory}}?serviceClass=org.mulesoft.tshirt_service.TshirtServicePortType&dataFormat=payload")
                .routeId(INVENTORY_ID)
                .log("inside inventory...")
                .setHeader("operation").simple("${header.operationName}")
                .toD("direct:${header.operationName}");

        from("cxf:{{camel.cxf.hello}}?serviceClass=com.example.wsdl.HelloPortType&dataFormat=payload")
                .routeId(HELLO_ID)
                .setHeader("operation").simple("${header.operationName}")
                .toD("direct:${header.operationName}");
    }
}
