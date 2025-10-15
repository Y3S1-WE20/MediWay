package com.mediway.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for payment response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private UUID paymentId;
    private UUID userId;
    private UUID appointmentId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String paypalPaymentId;
    private String description;
    private String approvalUrl;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String message;
}
