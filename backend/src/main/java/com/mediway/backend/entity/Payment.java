package com.mediway.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payment entity to store payment transaction details
 * Supports PayPal sandbox integration for MediWay payment processing
 */
@Entity
@Table(name = "payments", indexes = {
        @Index(name = "idx_payment_user_id", columnList = "user_id"),
        @Index(name = "idx_payment_paypal_id", columnList = "paypal_payment_id"),
        @Index(name = "idx_payment_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "payment_id", updatable = false, nullable = false)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    @Builder.Default
    private PaymentMethod paymentMethod = PaymentMethod.PAYPAL;

    @Column(name = "paypal_payment_id", length = 100, unique = true)
    private String paypalPaymentId;

    @Column(name = "payer_id", length = 100)
    private String payerId;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "return_url", length = 500)
    private String returnUrl;

    @Column(name = "cancel_url", length = 500)
    private String cancelUrl;

    @Column(name = "approval_url", length = 500)
    private String approvalUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Payment status enum
     */
    public enum PaymentStatus {
        CREATED,        // Payment intent created
        APPROVED,       // User approved payment
        COMPLETED,      // Payment successfully completed
        FAILED,         // Payment failed
        CANCELLED       // Payment cancelled by user
    }

    /**
     * Payment method enum
     */
    public enum PaymentMethod {
        PAYPAL,
        CREDIT_CARD,
        DEBIT_CARD
    }
}
