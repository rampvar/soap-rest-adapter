package com.soaprestadapter.processor;

import com.soaprestadapter.exception.AccessDeniedException;
import com.soaprestadapter.exception.DataBaseException;
import com.soaprestadapter.exception.NotFoundException;
import com.soaprestadapter.exception.ParsingException;
import javax.xml.namespace.QName;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.cxf.binding.soap.SoapFault;
import org.apache.cxf.helpers.DOMUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Processes exceptions caught during Camel route execution and
 * throws a SOAPFaultException to be returned as a proper SOAP fault to the client.
 */
@Component
public class GenericExceptionProcessor implements Processor {

    /**
     * The namespace used for generating SOAP fault codes and detail elements.
     * This value is injected from the application configuration property
     * {@code soap.fault.namespace}.
     */
    @Value("${soap.fault.namespace}")
    private String soapFaultNamespace;

    /** HTTP status code for Bad Request. */
    private static final int HTTP_BAD_REQUEST = 400;

    /** HTTP status code for Internal Server Error. */
    private static final int HTTP_INTERNAL_SERVER_ERROR = 500;


    @Override
    public void process(final Exchange exchange) {

        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);

        String faultCode = "SERVER_ERROR";
        String faultString = "Unexpected error occurred";

        if (exception instanceof AccessDeniedException ex) {
            faultCode = "HTTP_" + ex.getStatusCode();
            faultString = "REST call failed: " + ex.getResponseBody();

        } else if (exception instanceof NotFoundException ex) {
            faultCode = "HTTP_" + ex.getStatusCode();
            faultString = "REST call failed: " + ex.getResponseBody();

        } else if (exception instanceof ParsingException ex) {
            faultCode = "HTTP_" + ex.getStatusCode();
            faultString = "REST call failed: " + ex.getResponseBody();

        } else if (exception instanceof IllegalArgumentException ex) {
            faultCode = "HTTP_" + HTTP_BAD_REQUEST;
            faultString = "Invalid request: " + ex.getMessage();

        } else if (exception instanceof DataBaseException ex) {
            faultCode = "HTTP_" + ex.getStatusCode();
            faultString = "Invalid request: " + ex.getMessage();

        } else if (exception != null) {
            faultCode = "SERVER_ERROR_" + HTTP_INTERNAL_SERVER_ERROR;
            faultString = "Unexpected error: " + exception.getMessage();
        }

        QName customCode = new QName(soapFaultNamespace, faultCode, "soap");
        SoapFault fault = new SoapFault(faultString, customCode);
        // Create the detail block
        Document doc = DOMUtils.createDocument();
        Element detail = doc.createElementNS(soapFaultNamespace, "errorDetail");
        detail.setTextContent("This is the detailed error description");
        // Attach detail to the fault
        fault.setDetail(detail);
        exchange.getIn().setBody(fault);
        // Set content type to SOAP XML
        exchange.getIn().setHeader(Exchange.CONTENT_TYPE, "text/xml;charset=UTF-8");

    }
}
