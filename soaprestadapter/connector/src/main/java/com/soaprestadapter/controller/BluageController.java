
package com.soaprestadapter.controller;

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
    public String handleRequest1(@RequestBody final String data) {

        return "{"
                + "\"orderId\": \"101\""
                + "}";
    }


    /**
     * handleRequest2 method
     *
     * @param data
     * @return object
     */

    @PostMapping("/track_order")
    public String handleRequest2(@RequestBody final String data) {
        return "{"
                + "\"orderId\": \"101\","
                + "\"status\": \"Alice\","
                + "\"size\": \"M\""
                + "}";
    }
}