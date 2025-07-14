package com.soaprestadapter.properties;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *  Configuration properties for the IAM service.
 */
@Data
@Component
@ConfigurationProperties(prefix = "iam")
public class IamConfigProperties {

    /**
     * IAM service role mappings.
     */
    private Map<String, String> roleMappings;
    /**
     *  IAM service permission mappings.
     */
    private Map<String, List<String>> permissions;
}

