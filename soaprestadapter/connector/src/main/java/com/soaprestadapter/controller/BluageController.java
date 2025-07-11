package com.soaprestadapter.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BluageController class
 */
@RestController
@RequestMapping("/bluage")
@RequiredArgsConstructor
public class BluageController {

    /**
     * ResourceLoader instance
     */
    private final ResourceLoader resourceLoader;


    /**
     * handleRequest1 method
     *
     * @param data - data in json format
     * @return object
     */
    @PostMapping("/order_tshirt")
    public String handleRequest1(@RequestBody final String data) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/orderTshirtResponseBluage.json");
        InputStream inputStream = resource.getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
    /**
     * handleRequest2 method
     * @param data - data in json format
     * @return object
     */
    @PostMapping("/track_order")
    public String handleRequest2(@RequestBody final String data) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:json/trackOrderResponseBluage.json");
        InputStream inputStream = resource.getInputStream();
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}