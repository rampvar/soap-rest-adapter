package com.soaprestadapter.config;

import com.soaprestadapter.utility.SoapFaultUtil;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Configures a global exception handling route for SOAP-related processing.
 * All exceptions are routed to a centralized handler using Camel's onException DSL.
 */
@Component
public class SoapRouteConfig extends RouteBuilder {

    /**
     * Configures the Camel context to route all unhandled exceptions
     * to the "direct:soapExceptionHandler" endpoint.
     */
    @Override
    public void configure() {
        onException(Exception.class)
                .handled(true)
                .process(exchange -> {
                    // Get the original exception
                    Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

                    // Build a fault message
                    String faultMessage = "An error occurred: " + ex.getMessage();

                    // Decide fault type
                    String faultType = ex instanceof IllegalArgumentException ? "Client" : "Server";

                    // Create SOAP fault and attach to exchange
                    exchange.setException(
                            SoapFaultUtil.createSoapFault(faultMessage, faultType)
                    );
                });
    }
}

