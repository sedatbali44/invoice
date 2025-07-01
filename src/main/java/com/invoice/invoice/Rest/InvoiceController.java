package com.invoice.invoice.Rest;

import com.invoice.invoice.Dto.InvoiceDto;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface InvoiceController {

    ResponseEntity<InvoiceDto.InvoiceResponse> processInvoice(@Valid @RequestBody InvoiceDto.InvoiceRequest request);
}
