package com.invoice.invoice.Service.Impl;

import com.invoice.invoice.Dto.InvoiceDto;
import com.invoice.invoice.Entity.Invoice;
import com.invoice.invoice.Repository.InvoiceRepository;
import com.invoice.invoice.Service.InvoiceService;
import com.invoice.invoice.Service.XmlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.w3c.dom.Document;
import java.io.InputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository repo;
    private final XmlService xmlService;

    @Transactional
    @Override
    public void processInvoice(String base64Xml) {
        try {
            String xmlContent = xmlService.decodeBase64(base64Xml);
            log.debug("Successfully decoded Base64 XML");

            Document document = xmlService.parseXml(xmlContent);
            log.debug("Successfully parsed XML document");

            try {
                ClassPathResource xsdResource = new ClassPathResource("xsd/faktura.xsd");
                if (xsdResource.exists()) {
                    try (InputStream xsdStream = xsdResource.getInputStream()) {
                        xmlService.validateXml(document, xsdStream);
                        log.debug("XML validation successful");
                    }
                } else {
                    log.warn("XSD file not found, skipping validation");
                }
            } catch (Exception e) {
                log.warn("XSD validation failed, proceeding without validation: {}", e.getMessage());
            }

            String nip = xmlService.extractNip(document);
            String p1 = xmlService.extractP1(document);
            String p2 = xmlService.extractP2(document);

            log.debug("Extracted data - NIP: {}, P1: {}, P2: {}", nip, p1, p2);

            Invoice invoice = new Invoice();
            invoice.setNip(nip);
            invoice.setP1(p1);
            invoice.setP2(p2);

            repo.save(invoice);
            log.info("Successfully saved invoice with NIP: {}", nip);

        } catch (Exception e) {
            log.error("Failed to process invoice: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process invoice: " + e.getMessage(), e);
        }
    }

    @Override
    public InvoiceDto.InvoiceResponse processXmlInvoice(@RequestBody String xmlContent) {
        log.info("Received XML invoice processing request");
        try {
            String base64Xml = Base64.getEncoder().encodeToString(xmlContent.getBytes());
            processInvoice(base64Xml);

            InvoiceDto.InvoiceResponse response = new InvoiceDto.InvoiceResponse("Invoice saved successfully");
            return response;
        } catch (Exception e) {
            log.error("Failed to process XML invoice: {}", e.getMessage());
            throw e;
        }
    }
}
