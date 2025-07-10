package com.soaprestadapter.utility;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * Utility class to generate SOAPFaultException with specific fault codes
 * for SOAP 1.1-compliant error handling.
 */
public class SoapFaultUtil {

    /**
     * Creates a SOAPFaultException using a string fault code ("Client" or "Server").
     *
     * @param message   the fault message to include in the SOAP fault
     * @param faultType the fault code as a string; typically "Client" or "Server"
     * @return a SOAPFaultException containing the provided fault string and fault code
     * @throws RuntimeException if the SOAP fault cannot be created
     */
    public static SOAPFaultException createSoapFault(final String message, final String faultType) {
        try {
            SOAPFactory factory = SOAPFactory.newInstance();
            SOAPFault fault = factory.createFault();

            // Set message and code using string (SOAP 1.1)
            fault.setFaultString(message);
            fault.setFaultCode("soap:" + faultType); // soap:Client or soap:Server

            return new SOAPFaultException(fault);

        } catch (SOAPException e) {
            throw new RuntimeException("Unable to create SOAP fault", e);
        }
    }

    /**
     * Creates a SOAPFaultException with the default fault type "Server".
     *
     * @param message the fault message to include in the SOAP fault
     * @return a SOAPFaultException with fault code "soap:Server"
     * @throws RuntimeException if the SOAP fault cannot be created
     */
    public static SOAPFaultException createSoapFault(final String message) {
        return createSoapFault(message, "Server");
    }
}
