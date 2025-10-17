package com.mediway.backend.repository;

import com.mediway.backend.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    
    Optional<Receipt> findByReceiptNumber(String receiptNumber);
    
    Optional<Receipt> findByPaymentId(Long paymentId);
    
    List<Receipt> findByUserIdOrderByIssueDateDesc(Long userId);
    
    @Query("SELECT r FROM Receipt r WHERE r.userId = :userId AND r.appointmentId = :appointmentId")
    List<Receipt> findByUserIdAndAppointmentId(@Param("userId") Long userId, @Param("appointmentId") Long appointmentId);
    
    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    boolean existsByPaymentId(Long paymentId);
}