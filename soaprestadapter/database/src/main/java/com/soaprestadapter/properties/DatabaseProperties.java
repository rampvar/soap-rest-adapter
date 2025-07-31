package com.soaprestadapter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Database properties class
 */
@Data
@Component
@ConfigurationProperties(prefix = "custom.datasource")
public class DatabaseProperties {
    /**
     * DB URL
     */
    private String url;
    /**
     * DB Driver class name
     */
    private String driverClassName;
    /**
     * DB username
     */
    private String username;
    /**
     * DB password
     */
    private String password;
}
