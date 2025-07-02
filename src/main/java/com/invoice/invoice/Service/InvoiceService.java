package com.invoice.invoice.Service;

import com.invoice.invoice.Dto.InvoiceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

public interface InvoiceService {

    InvoiceDto.InvoiceResponse processInvoice(String base64Xml);

    InvoiceDto.InvoiceResponse processXmlInvoice(String xmlContent);
}
