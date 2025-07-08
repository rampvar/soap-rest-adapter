
package com.soaprestadapter.controller;

import org.mulesoft.tshirt_service.OrderTshirtResponse;
import org.mulesoft.tshirt_service.Size;
import org.mulesoft.tshirt_service.TrackOrderResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * BluageController class
 */

@RestController
@RequestMapping("/bluage")
public class BluageController {


/**
     * handleRequest1 method
     *
     * @param data
     * @return object
     */


    @PostMapping("/order_tshirt")
    public OrderTshirtResponse handleRequest1(@RequestBody final String data) {
        OrderTshirtResponse response = new OrderTshirtResponse();
        response.setOrderId("101");
        return response;
    }


/**
     * handleRequest2 method
     *
     * @param data
     * @return object
     */

    @PostMapping("/track_order")
    public TrackOrderResponse handleRequest2(@RequestBody final String data) {
        TrackOrderResponse response = new TrackOrderResponse();
        response.setSize(Size.M);
        response.setStatus("Shipped");
        response.setOrderId("101");
        return response;
    }
}