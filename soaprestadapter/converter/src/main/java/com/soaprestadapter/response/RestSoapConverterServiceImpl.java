package com.soaprestadapter.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soaprestadapter.exception.ParsingException;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.StringWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link RestSoapConverterService} that converts REST JSON responses
 * into SOAP-compatible XML using Jackson for JSON and JAXB for XML processing.
 */
@Service
public class RestSoapConverterServiceImpl implements RestSoapConverterService {

    /**
     * Logger instance for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestSoapConverterServiceImpl.class);

    /**
     * Reusable ObjectMapper instance.
     */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Converts a REST JSON response string into a SOAP-compatible XML string.
     *
     * @param restJsonStringResponse the JSON string response
     * @param cls                    the class type to deserialize the JSON into
     * @param <T>                    the type to deserialize and marshal
     * @return the resulting SOAP XML string
     */
    @Override
    public <T> String convertRestResponseToSoapXml(final String restJsonStringResponse, final Class<T> cls) {
        try {
            final T response = mapper.readValue(restJsonStringResponse, cls);
            return convertJavaObjectToXML(cls, response);
        } catch (final JsonProcessingException e) {
            LOGGER.error("Error converting JSON string to Java object", e);
            throw new ParsingException("Failed to marshal Java object to XML: " + e.getMessage(), e);
        }
    }

    /**
     * Converts a Java object to an XML string using JAXB.
     *
     * @param cls        the class type of the object
     * @param javaObject the Java object to convert
     * @param <T>        the type of the object
     * @return the resulting XML string
     */
    private <T> String convertJavaObjectToXML(final Class<T> cls, final Object javaObject) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(cls);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            final StringWriter sw = new StringWriter();
            marshaller.marshal(javaObject, sw);

            final String xmlResponse = sw.toString();
            LOGGER.info("XML Response inside service class: \n{}", xmlResponse);

            return xmlResponse;
        } catch (final JAXBException e) {
            LOGGER.error("Error converting Java object to XML", e);
            throw new ParsingException("Failed to convert Java to XML", e);
        }
    }
}
