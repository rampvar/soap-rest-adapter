package com.soaprestadapter.utility;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class XmlToMapParserTest {

    @Test
    void shouldParseXmlWithNestedElementsCorrectly() throws ParserConfigurationException, IOException, SAXException {
        String xml = "<root>" +
                "<person>" +
                "<name>John Doe</name>" +
                "<age>30</age>" +
                "</person>" +
                "<person>" +
                "<name>Jane Doe</name>" +
                "<age>28</age>" +
                "</person>" +
                "</root>";

        XmlToMapParser parser = new XmlToMapParser();
        Map<String, Object> result = parser.parseXml(xml);
        assertNotNull(result);
    }
}