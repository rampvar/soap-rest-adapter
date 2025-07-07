package com.soaprestadapter.route;

import com.soaprestadapter.utility.ErrorCodeMapper;
import com.soaprestadapter.utility.SoapFaultUtil;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Route that handles SOAP exceptions by mapping exceptions to error codes
 * and throwing SOAP faults.
 */
@Component
public class SoapExceptionHandlerRoute extends RouteBuilder {

    /**
     * Configures the route to handle SOAP exceptions.
     *
     * @throws Exception if route configuration fails
     */
    @Override
    public void configure() throws Exception {
        from("direct:soapExceptionHandler")
                .process(exchange -> {
                    Exception ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    ErrorCodeMapper error = ErrorCodeMapper.fromException(ex);
                    String faultMessage = String.format("Error Code: %s, Message: %s",
                            error.getCode(), error.getMessage());
                    String faultType = (error.getCode().startsWith("4")) ? "Client" : "Server";
                    exchange.setException(
                            SoapFaultUtil.createSoapFault(faultMessage, faultType)
                    );
                });
    }
}
