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

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping("/invoices")
    public ResponseEntity<InvoiceDto.InvoiceResponse> processInvoice(
            @Valid @RequestBody InvoiceDto.InvoiceRequest request) {

        log.info("Received invoice processing request");

        try {
            invoiceService.processInvoice(request.getBase64Xml());

            InvoiceDto.InvoiceResponse response = new InvoiceDto.InvoiceResponse("Invoice saved successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Failed to process invoice: {}", e.getMessage());
            throw e; // Let the global exception handler deal with it
        }
    }
}
