package com.mediway.backend.repository;

import com.mediway.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserIdOrderByPaymentDateDesc(Long userId);
    List<Payment> findByAppointmentIdOrderByPaymentDateDesc(Long appointmentId);
    List<Payment> findByStatus(Payment.Status status);
}
