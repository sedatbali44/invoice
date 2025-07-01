package com.invoice.invoice.Rest.Impl;

import com.invoice.invoice.Dto.InvoiceDto;
import com.invoice.invoice.Rest.InvoiceController;

import com.invoice.invoice.Service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;

    @Override
    @PostMapping(value = "/invoices/xml", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<InvoiceDto.InvoiceResponse> processXmlInvoice(@RequestBody String xmlContent) {
        log.info("Received XML invoice processing request");
        try {
            // Encode the XML to Base64 before processing
            String base64Xml = Base64.getEncoder().encodeToString(xmlContent.getBytes());
            invoiceService.processInvoice(base64Xml);

            InvoiceDto.InvoiceResponse response = new InvoiceDto.InvoiceResponse("Invoice saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Failed to process XML invoice: {}", e.getMessage());
            throw e;
        }
    }
}
