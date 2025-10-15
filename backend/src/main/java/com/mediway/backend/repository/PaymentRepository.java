package com.mediway.backend.repository;

import com.mediway.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Payment entity
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    /**
     * Find payment by PayPal payment ID
     */
    Optional<Payment> findByPaypalPaymentId(String paypalPaymentId);

    /**
     * Find all payments for a user
     */
    List<Payment> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find all payments for an appointment
     */
    List<Payment> findByAppointmentIdOrderByCreatedAtDesc(UUID appointmentId);

    /**
     * Find payments by status
     */
    List<Payment> findByStatus(Payment.PaymentStatus status);

    /**
     * Find payments by user and status
     */
    List<Payment> findByUserIdAndStatus(UUID userId, Payment.PaymentStatus status);

    /**
     * Check if payment exists for PayPal payment ID
     */
    boolean existsByPaypalPaymentId(String paypalPaymentId);
}
