package com.soaprestadapter.route;

import com.google.gson.Gson;
import com.soaprestadapter.processor.CommonProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.mulesoft.tshirt_service.OrderTshirtResponse;
import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.springframework.stereotype.Component;


@Component
public class InventoryServiceRoute extends RouteBuilder {

    private final CommonProcessor commonProcessor;

    public InventoryServiceRoute(CommonProcessor commonProcessor) {
        this.commonProcessor = commonProcessor;
    }

    @Override
    public void configure() throws Exception {
        from("direct:TrackOrder")
                .process(commonProcessor)
                .to("bean:requestDispatcher?method=run(${exchangeProperty.map1}, ${exchangeProperty.map2})")
                .process(exchange -> {
                    exchange.getIn().setBody(new Gson().fromJson(exchange.getIn().getBody(String.class), TrackOrderResponse.class));
                }).marshal()
                .jaxb("org.mulesoft.tshirt_service")
                .log("End");


        from("direct:OrderTshirt")
                .process(commonProcessor)
                .to("bean:requestDispatcher?method=run(${exchangeProperty.map1}, ${exchangeProperty.map2})")
                .process(exchange -> {
                    exchange.getIn().setBody(new Gson().fromJson(exchange.getIn().getBody(String.class), OrderTshirtResponse.class));
                }).marshal()
                .jaxb("org.mulesoft.tshirt_service")
                .log("End");
    }
}