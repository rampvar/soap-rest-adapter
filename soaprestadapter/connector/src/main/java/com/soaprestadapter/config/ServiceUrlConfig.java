package com.soaprestadapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.Map;

/**
 * ServiceUrlConfig class
 */
@Component
@ConfigurationProperties(prefix = "services")
@Getter
@Setter
public class ServiceUrlConfig {

    /**
     * endpoints
     */
    private Map<String, Map<String, String>> endpoints;


    /**
     * getUrl method
     *
     * @param connectorName
     * @param operationName
     * @return string
     */
    public String getUrl(final String connectorName, final String operationName) {
        Map<String, String> stringStringMap = endpoints.get(connectorName);
        String url = stringStringMap.get(operationName);
        return url;
    }
}

