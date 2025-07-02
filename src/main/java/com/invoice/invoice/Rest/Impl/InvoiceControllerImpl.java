package com.invoice.invoice.Rest.Impl;

import com.invoice.invoice.Dto.InvoiceDto;
import com.invoice.invoice.Rest.InvoiceController;
import com.invoice.invoice.Service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class InvoiceControllerImpl implements InvoiceController {

    private final InvoiceService invoiceService;

    @Override
    @PostMapping(value = "/invoices", consumes = "application/json", produces = "application/json")
    public ResponseEntity<InvoiceDto.InvoiceResponse> processInvoice(@Valid @RequestBody InvoiceDto.InvoiceRequest request) {
        log.info("Received invoice processing request");
        InvoiceDto.InvoiceResponse response = invoiceService.processInvoice(request.getBase64Xml());
        return ResponseEntity.ok(response);
    }
}
