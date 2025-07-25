package com.soaprestadapter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * properties file to read from yml file
 */
@Data
@Component
@ConfigurationProperties(prefix = "entitlement")
public class CloudProviderProperties {
    /**
     * Entitlement Strategy
     */
    private Strategy strategy = new Strategy();

    /**
     *  Strategy properties
     */
    @Data
    public static class Strategy {
        /**
         * Mode of entitlement
         */
        private String mode;
        /**
         * Type of cloud provider
         */
        private String cloudProviderType;
    }
}
