package com.soaprestadapter.service;

import com.soaprestadapter.properties.WsdlJobProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * service implementation method to load .class files into db
 */
@Service
@RequiredArgsConstructor
public class LoadClassesAtBootstrapServiceImpl implements LoadClassesAtBootstrapService {

    /**
     * Generate .class files from wsdl
     */
    private final WsdlGenerationService generationService;

    /**
     * load wsdl urls from yml file
     */
    private final WsdlJobProperties wsdlJobProperties;

    /**
     * Load.class files from wsdl urls
     *
     * @throws Exception if an error occurs while processing wsdl urls
     */
    @Override
    public void loadWsdlClassAtBootstrap() throws Exception {
        generationService.processWsdlUrls(wsdlJobProperties.getWsdlJobs());
    }
}
