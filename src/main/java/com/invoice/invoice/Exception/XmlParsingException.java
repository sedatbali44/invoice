package com.invoice.invoice.Exception;

public class XmlParsingException extends RuntimeException {
    public XmlParsingException(String message) {
        super(message);
    }

    public XmlParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}