package com.invoice.invoice.Service;

import org.w3c.dom.Document;

import java.io.InputStream;

public interface XmlService {

    String extractP2(Document document);

    String extractP1(Document document);

    String extractNip(Document document);

    void validateXml(Document document, InputStream xsdStream);

    Document parseXml(String xmlContent);

    String decodeBase64(String base64Xml);
}
