package com.invoice.invoice.Service.Impl;


import com.invoice.invoice.Service.XmlService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Service
public class XmlServiceImpl implements XmlService {

    private static final String NAMESPACE_URI = "http://crd.gov.pl/wzor/2023/06/29/12648/";

    @Override
    public String decodeBase64(String base64Xml) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Xml);
            return new String(decodedBytes);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Base64 encoding", e);
        }
    }


    @Override
    public Document parseXml(String xmlContent) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException("Failed to parse XML", e);
        }
    }

    @Override
    public void validateXml(Document document, InputStream xsdStream) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new StreamSource(xsdStream));
            Validator validator = schema.newValidator();
            Source source = new DOMSource(document);
            validator.validate(source);
        } catch (SAXException | IOException e) {
            throw new RuntimeException("XML validation failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractNip(Document document) {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext(new javax.xml.namespace.NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if ("fa".equals(prefix)) {
                        return NAMESPACE_URI;
                    }
                    return null;
                }
                public String getPrefix(String uri) {
                    return null;
                }
                public java.util.Iterator<String> getPrefixes(String uri) {
                    return null;
                }
            });

            String expression = "//fa:Podmiot1/fa:DaneIdentyfikacyjne/fa:NIP";
            Node nipNode = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (nipNode != null) {
                return nipNode.getTextContent();
            }

            // Fallback for documents without namespace prefix
            NodeList nodes = document.getElementsByTagNameNS(NAMESPACE_URI, "NIP");
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }

            throw new RuntimeException("NIP not found in XML");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract NIP: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractP1(Document document) {
        try {
            NodeList nodes = document.getElementsByTagNameNS(NAMESPACE_URI, "P_1");
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
            throw new RuntimeException("P_1 not found in XML");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract P_1: " + e.getMessage(), e);
        }
    }

    @Override
    public String extractP2(Document document) {
        try {
            NodeList nodes = document.getElementsByTagNameNS(NAMESPACE_URI, "P_2");
            if (nodes.getLength() > 0) {
                return nodes.item(0).getTextContent();
            }
            throw new RuntimeException("P_2 not found in XML");
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract P_2: " + e.getMessage(), e);
        }
    }
}
