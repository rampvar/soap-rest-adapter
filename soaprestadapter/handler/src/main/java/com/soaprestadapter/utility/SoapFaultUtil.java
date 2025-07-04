package com.soaprestadapter.utility;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * Utility class for creating SOAP fault exceptions.
 */
public class SoapFaultUtil {

    /**
     * Creates a SOAPFaultException with the specified message.
     *
     * @param message the fault message to set in the SOAP fault
     * @return a SOAPFaultException containing the fault message and code
     */
    public static SOAPFaultException createSoapFault(final String message) {
        try {
            SOAPFault fault = SOAPFactory.newInstance().createFault();
            fault.setFaultString(message);
            fault.setFaultCode(new QName("http://schemas.xmlsoap.org/soap/envelope/", "Server"));
            return new SOAPFaultException(fault);
        } catch (SOAPException e) {
            throw new RuntimeException("Unable to create SOAP fault", e);
        }
    }
}
