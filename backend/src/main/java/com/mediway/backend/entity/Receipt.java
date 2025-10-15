package com.mediway.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Receipt entity to store payment receipt details
 * Generated after successful payment completion
 */
@Entity
@Table(name = "receipts", indexes = {
        @Index(name = "idx_receipt_payment_id", columnList = "payment_id"),
        @Index(name = "idx_receipt_user_id", columnList = "user_id"),
        @Index(name = "idx_receipt_number", columnList = "receipt_number")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "receipt_id", updatable = false, nullable = false)
    private UUID receiptId;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 50)
    private String receiptNumber;

    @Column(name = "payment_id", nullable = false)
    private UUID paymentId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(name = "transaction_id", length = 100)
    private String transactionId;

    @Column(name = "payer_email", length = 100)
    private String payerEmail;

    @Column(name = "payer_name", length = 100)
    private String payerName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "payment_date", nullable = false)
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "pdf_path", length = 500)
    private String pdfPath;

    /**
     * Generate unique receipt number
     * Format: RCP-YYYYMMDD-XXXXX
     */
    public static String generateReceiptNumber() {
        String datePart = LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "RCP-" + datePart + "-" + randomPart;
    }
}
