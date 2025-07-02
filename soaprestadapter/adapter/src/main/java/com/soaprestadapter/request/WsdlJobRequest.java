package com.soaprestadapter.request;

import java.util.List;

public class WsdlJobRequest {

    /**
     * Holds the wsdlUrl.
     */
    private String wsdlUrl;

    /**
     * Holds the xsdUrls.
     */
    private List<String> xsdUrls;

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(final String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public List<String> getXsdUrls() {
        return xsdUrls;
    }

    public void setXsdUrls(final List<String> xsdUrls) {
        this.xsdUrls = xsdUrls;
    }
}
