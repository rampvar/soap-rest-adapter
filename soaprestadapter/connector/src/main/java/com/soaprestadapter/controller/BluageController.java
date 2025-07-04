package com.soaprestadapter.controller;

import org.mulesoft.tshirt_service.OrderTshirtResponse;
import org.mulesoft.tshirt_service.Size;
import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bluage")
public class BluageController {

    @PostMapping("/order_tshirt")
    public OrderTshirtResponse handleRequest1(@RequestBody String data) {
        try {
            OrderTshirtResponse response = new OrderTshirtResponse();
            response.setOrderId("101");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while handling request for order tshirt" + e);
        }
    }

    @PostMapping("/track_order")
    public TrackOrderResponse handleRequest2(@RequestBody String data) {
        try {
            TrackOrderResponse response = new TrackOrderResponse();
            response.setSize(Size.M);
            response.setStatus("Shipped");
            response.setOrderId("101");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Error while handling request for track order" + e);
        }
    }
}
