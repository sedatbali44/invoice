package com.invoice.invoice.Exception;

public class Base64DecodingException extends RuntimeException {
    public Base64DecodingException(String message) {
        super(message);
    }

    public Base64DecodingException(String message, Throwable cause) {
        super(message, cause);
    }
}
