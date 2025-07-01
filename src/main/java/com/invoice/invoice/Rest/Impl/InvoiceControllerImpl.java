package com.invoice.invoice.Rest.Impl;

import com.invoice.invoice.Dto.InvoiceDto;
import com.invoice.invoice.Rest.InvoiceController;
import com.invoice.invoice.Service.InvoiceService;
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
    @PostMapping(value = "/invoices/xml", consumes = "application/xml", produces = "application/json")
    public ResponseEntity<InvoiceDto.InvoiceResponse> processXmlInvoice(@RequestBody String xmlContent) {
       return ResponseEntity.ok(invoiceService.processXmlInvoice(xmlContent));
    }
}
