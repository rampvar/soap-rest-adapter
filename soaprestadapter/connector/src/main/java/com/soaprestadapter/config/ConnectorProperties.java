package com.soaprestadapter.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ConnectorProperties class
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ConnectorProperties {

    /**
     * connector
     */
    private String connector;

}

