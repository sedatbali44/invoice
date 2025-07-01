package com.invoice.invoice.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class InvoiceDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceRequest {
        @NotBlank(message = "base64xml is required")
        @JsonProperty("base64xml")
        private String base64Xml;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceResponse {
        private String message;
    }
}