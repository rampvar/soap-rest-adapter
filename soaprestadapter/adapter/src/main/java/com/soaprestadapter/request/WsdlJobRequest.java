package com.soaprestadapter.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Generates Getter and Setter methods.
 */
@Getter
@Setter
public class WsdlJobRequest {

    /**
     * Holds the wsdlUrl.
     */
    private String wsdlUrl;

    /**
     * Holds the xsdUrls.
     */
    private List<String> xsdUrls;

}
