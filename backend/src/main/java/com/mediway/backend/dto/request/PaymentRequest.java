package com.mediway.backend.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * DTO for creating a payment request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Invalid amount format")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be uppercase 3-letter code (e.g., USD)")
    @Builder.Default
    private String currency = "USD";

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    private UUID appointmentId;

    @NotBlank(message = "Return URL is required")
    @Pattern(regexp = "^https?://.*", message = "Return URL must be a valid HTTP/HTTPS URL")
    private String returnUrl;

    @NotBlank(message = "Cancel URL is required")
    @Pattern(regexp = "^https?://.*", message = "Cancel URL must be a valid HTTP/HTTPS URL")
    private String cancelUrl;

    @NotBlank(message = "Payment method is required")
    @Pattern(regexp = "^(PAYPAL|CREDIT_CARD|DEBIT_CARD)$", message = "Invalid payment method")
    @Builder.Default
    private String paymentMethod = "PAYPAL";
}
