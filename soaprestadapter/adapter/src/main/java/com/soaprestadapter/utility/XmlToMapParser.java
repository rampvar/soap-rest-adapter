package com.soaprestadapter.utility;

import com.soaprestadapter.exception.ParsingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * XmlToMapParser class which converts xml to map
 */
public class XmlToMapParser {
    /**
     * HTTP 400 status code for bad request.
     */
    private static final int HTTP_BAD_REQUEST = 400;
    /**
     * method parseXml
     *
     * @param xml
     * @return map
     */
    public Map<String, Object> parseXml(final String xml) {
        try {
            Map<String, Object> result = parseXmlToMap(new ByteArrayInputStream(xml.getBytes()));
            return result;
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParsingException(HTTP_BAD_REQUEST, "Failed to parse the XML. Cause: " + e.getMessage());
        }
    }

    /**
     * method parseXmlToMap
     *
     * @param xmlStream
     * @return map
     */
    public static Map<String, Object> parseXmlToMap(final InputStream xmlStream)
            throws ParserConfigurationException,
            IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStream);
        Element root = doc.getDocumentElement();
        return parseElement(root);
    }

    /**
     * method parseElement
     *
     * @param element
     * @return map
     */
    private static Map<String, Object> parseElement(final Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        NodeList childNodes = element.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node instanceof Element) {
                Element childElement = (Element) node;
                String tag = childElement.getLocalName() != null ?
                        childElement.getLocalName() : childElement.getNodeName();

                Object value = getValue(childElement);

                if (map.containsKey(tag)) {
                    Object existing = map.get(tag);
                    List<Object> list;

                    if (existing instanceof List) {
                        list = (List<Object>) existing;
                    } else {
                        list = new ArrayList<>();
                        list.add(existing);
                    }
                    list.add(value);
                    map.put(tag, list);
                } else {
                    map.put(tag, value);
                }
            }
        }

        return map;
    }

    /**
     * method getValue
     *
     * @param element
     * @return object
     */
    private static Object getValue(final Element element) {
        NodeList children = element.getChildNodes();
        boolean hasElementChildren = false;

        for (int i = 0; i < children.getLength(); i++) {
            if (children.item(i) instanceof Element) {
                hasElementChildren = true;
                break;
            }
        }

        if (hasElementChildren) {
            return parseElement(element);
        } else {
            return element.getTextContent().trim();
        }
    }
}
