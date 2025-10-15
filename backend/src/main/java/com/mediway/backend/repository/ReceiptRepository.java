package com.mediway.backend.repository;

import com.mediway.backend.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Receipt entity
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

    /**
     * Find receipt by receipt number
     */
    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    /**
     * Find receipt by payment ID
     */
    Optional<Receipt> findByPaymentId(UUID paymentId);

    /**
     * Find all receipts for a user
     */
    List<Receipt> findByUserIdOrderByCreatedAtDesc(UUID userId);

    /**
     * Find all receipts for an appointment
     */
    List<Receipt> findByAppointmentIdOrderByCreatedAtDesc(UUID appointmentId);

    /**
     * Check if receipt exists for payment
     */
    boolean existsByPaymentId(UUID paymentId);

    /**
     * Check if receipt number exists
     */
    boolean existsByReceiptNumber(String receiptNumber);
}
