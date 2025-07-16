package com.soaprestadapter.properties;

import com.soaprestadapter.request.WsdlJobRequest;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Wsdl job properties load wsdl urls from yml file
 */
@Data
@Component
@ConfigurationProperties(prefix = "wsdl-jobs-config")
public class WsdlJobProperties {
    /**
     * list of wsdl urls with xsd
     */
    private List<WsdlJobRequest> wsdlJobs;
}
