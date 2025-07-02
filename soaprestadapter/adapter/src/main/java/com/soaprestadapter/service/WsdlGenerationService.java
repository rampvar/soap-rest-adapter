package com.soaprestadapter.service;

import com.soaprestadapter.request.WsdlJobRequest;
import java.util.List;
import java.util.Map;

/**
 * WsdlGenerationService Interface
 */
public interface WsdlGenerationService {
    /**
     * processWsdlUrls
     * @param jobRequests will have request payload
     * @return will return list of generated fileNames
     */
    Map<String, Map<String, List<String>>> processWsdlUrls
            (final List<WsdlJobRequest> jobRequests);
}
