package com.soaprestadapter.route;

import com.soaprestadapter.config.DynamicInvoker;
import com.soaprestadapter.processor.CommonProcessor;
import com.soaprestadapter.response.RestSoapConverterService;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * InventoryServiceRoute class representing inventory service.
 */
@RequiredArgsConstructor
@Component
public class InventoryServiceRoute extends RouteBuilder {

    /**
     * CommonProcessor
     */
    private final CommonProcessor commonProcessor;

    /**
     * Invoker utility for accessing dynamically loaded classes.
     */
    private final DynamicInvoker dynamicInvoker;
    /**
     * RestSoapConverterService for converting REST responses to SOAP XML.
     */
    private final RestSoapConverterService restSoapConverterService;

    @Override
    public void configure() throws Exception {
        from("direct:TrackOrder")
                .process(commonProcessor)
                .to("bean:requestDispatcher?" +
                        "method=run(${exchangeProperty.mapWithCobolJsonAttribute}," +
                        " ${exchangeProperty.mapWithPayload}," +
                        " ${exchangeProperty.jwtToken})")
                .process(exchange -> {
                    String jsonData = exchange.getIn().getBody(String.class);

                    // Dynamically load the TrackOrderResponse class
                    Class<?> responseClass = dynamicInvoker.getLoadedClass(
                            "org.mulesoft.tshirt_service.TrackOrderResponse");

                    if (responseClass == null) {
                        throw new ClassNotFoundException(
                                "Class not found: org.mulesoft.tshirt_service.TrackOrderResponse");
                    }

                    // Call the conversion method with the actual Class object
                    String soapXml = restSoapConverterService.convertRestResponseToSoapXml(jsonData, responseClass);
                    exchange.getIn().setBody(soapXml);
                    log.info("Received Response SoapXml:{}", soapXml);


                }).log("Response SoapXml:${body}");

        from("direct:OrderTshirt")
                .process(commonProcessor)
                .to("bean:requestDispatcher?" +
                        "method=run(${exchangeProperty.mapWithCobolJsonAttribute}," +
                        " ${exchangeProperty.mapWithPayload}," +
                        " ${exchangeProperty.jwtToken})")
                .process(exchange -> {
                    String jsonData = exchange.getIn().getBody(String.class);
                    // Dynamically load the TrackOrderResponse class
                    Class<?> responseClass = dynamicInvoker.getLoadedClass
                            ("org.mulesoft.tshirt_service.OrderTshirtResponse");

                    if (responseClass == null) {
                        throw new ClassNotFoundException(
                                "Class not found: org.mulesoft.tshirt_service.OrderTshirtResponse");
                    }

                    // Call the conversion method with the actual Class object
                    String soapXml = restSoapConverterService.convertRestResponseToSoapXml(jsonData, responseClass);
                    exchange.getIn().setBody(soapXml);

                }).log("Response SoapXml:${body}");
    }
}