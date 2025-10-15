package com.mediway.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for receipt response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReceiptResponse {

    private UUID receiptId;
    private String receiptNumber;
    private UUID paymentId;
    private UUID userId;
    private UUID appointmentId;
    private BigDecimal amount;
    private String currency;
    private String paymentMethod;
    private String transactionId;
    private String payerEmail;
    private String payerName;
    private String description;
    private LocalDateTime paymentDate;
    private LocalDateTime createdAt;
    private String pdfPath;
}
