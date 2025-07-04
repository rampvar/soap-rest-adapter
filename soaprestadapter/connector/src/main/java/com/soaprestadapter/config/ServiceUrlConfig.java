package com.soaprestadapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
public class ServiceUrlConfig {
    private Map<String, Map<String, String>> endpoints;


    public String getUrl(String connectorName, String operationName) {
        Map<String, String> stringStringMap = endpoints.get(connectorName);
        String s = stringStringMap.get(operationName);

        //System.out.println("url is"+s);

        return s;
    }
}

