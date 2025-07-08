package com.soaprestadapter.route;

import com.soaprestadapter.interceptor.UserEntitlementInterceptor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoapAdapterRoute extends RouteBuilder {

    /**
     * Interceptor for user entitlement.
     */
    private final UserEntitlementInterceptor interceptor;

    public static final String INVENTORY_ID = "InventoryServiceWsdlRoute";
    public static final String HELLO_ID = "HelloServiceWsdlRoute";

    @Override
    public void configure() throws Exception {
        from("cxf:{{camel.cxf.inventory}}?serviceClass=org.mulesoft.tshirt_service.TshirtServicePortType&dataFormat=payload")
                .routeId(INVENTORY_ID)
                .process(interceptor)
                .log("inside inventory...")
                .setHeader("operation").simple("${header.operationName}")
                .toD("direct:${header.operationName}");

        from("cxf:{{camel.cxf.hello}}?serviceClass=com.example.wsdl.HelloPortType&dataFormat=payload")
                .routeId(HELLO_ID)
                .process(interceptor)
                .setHeader("operation").simple("${header.operationName}")
                .toD("direct:${header.operationName}");
    }
}
