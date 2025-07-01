package com.invoice.invoice.Exception;

import com.invoice.invoice.Dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvoiceProcessingException.class)
    public ResponseEntity<ErrorResponse> handleInvoiceProcessingException(
            InvoiceProcessingException ex, WebRequest request) {

        log.error("Invoice processing error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INVOICE_PROCESSING_ERROR")
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(XmlValidationException.class)
    public ResponseEntity<ErrorResponse> handleXmlValidationException(
            XmlValidationException ex, WebRequest request) {

        log.error("XML validation error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("XML_VALIDATION_ERROR")
                .message("The provided XML does not conform to the required schema: " + ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(XmlParsingException.class)
    public ResponseEntity<ErrorResponse> handleXmlParsingException(
            XmlParsingException ex, WebRequest request) {

        log.error("XML parsing error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("XML_PARSING_ERROR")
                .message("Failed to parse the provided XML content: " + ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Base64DecodingException.class)
    public ResponseEntity<ErrorResponse> handleBase64DecodingException(
            Base64DecodingException ex, WebRequest request) {

        log.error("Base64 decoding error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("BASE64_DECODING_ERROR")
                .message("Invalid Base64 encoding provided: " + ex.getMessage())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Validation error: {}", ex.getMessage());

        List<ErrorResponse.ValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .collect(Collectors.toList());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("VALIDATION_ERROR")
                .message("Request validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .validationErrors(validationErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, WebRequest request) {

        log.error("Message not readable error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("MALFORMED_REQUEST")
                .message("Request body is malformed or missing")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {

        log.error("Media type not supported error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("UNSUPPORTED_MEDIA_TYPE")
                .message("Content type '" + ex.getContentType() + "' is not supported. Expected: " +
                        ex.getSupportedMediaTypes())
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex, WebRequest request) {

        log.error("Method not supported error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("METHOD_NOT_ALLOWED")
                .message("HTTP method '" + ex.getMethod() + "' is not supported for this endpoint. " +
                        "Supported methods: " + ex.getSupportedMethods())
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, WebRequest request) {

        log.error("Method argument type mismatch: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INVALID_PARAMETER_TYPE")
                .message("Parameter '" + ex.getName() + "' should be of type " +
                        ex.getRequiredType().getSimpleName())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, WebRequest request) {

        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(getPath(request))
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse.ValidationError mapFieldError(FieldError fieldError) {
        return ErrorResponse.ValidationError.builder()
                .field(fieldError.getField())
                .message(fieldError.getDefaultMessage())
                .rejectedValue(fieldError.getRejectedValue())
                .build();
    }

    private String getPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
