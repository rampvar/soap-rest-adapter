package com.soaprestadapter.properties;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties to get Azure role mapping
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "azure")
public class AzureEntitlementProperties {
    /**
     * Role action mapping
     */
    private Map<String, List<String>> requiredActions;

    /**
     * get action from config
     * @param logicalAction action field from request
     * @return list of String with allowed actions
     */
    public List<String> getActionsFor(final String logicalAction) {
        return requiredActions.getOrDefault(logicalAction.toLowerCase(), List.of());
    }
}
