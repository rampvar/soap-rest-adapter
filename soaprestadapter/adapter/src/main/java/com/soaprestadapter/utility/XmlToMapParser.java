package com.soaprestadapter.utility;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class XmlToMapParser {

    public Map<String, Object> parseXml(String xml) {
        try {
            Map<String, Object> result = parseXmlToMap(new ByteArrayInputStream(xml.getBytes()));
            System.out.println(result);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error while parsing xml" + e);
        }
    }

    public static Map<String, Object> parseXmlToMap(InputStream xmlStream) throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true); // Optional: if you want to handle namespaces
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlStream);
        Element root = doc.getDocumentElement();
        return parseElement(root);
    }

    private static Map<String, Object> parseElement(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();
        NodeList childNodes = element.getChildNodes();

        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);

            if (node instanceof Element) {
                Element childElement = (Element) node;
                String tag = childElement.getLocalName() != null ? childElement.getLocalName() : childElement.getNodeName();

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

    private static Object getValue(Element element) {
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
