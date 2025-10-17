package com.mediway.backend.controller;

import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:5174")
public class SimplePaymentController {

    @Autowired
    private PaymentRepository paymentRepository;

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(payment -> ResponseEntity.ok(payment))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Payment>> getUserPayments(@PathVariable Long userId) {
        List<Payment> payments = paymentRepository.findByUserIdOrderByPaymentDateDesc(userId);
        return ResponseEntity.ok(payments);
    }

    // Frontend expects /api/payments/my-payments
    @GetMapping("/my-payments")
    public ResponseEntity<List<Payment>> getMyPayments() {
        // For prototype, return payments for user ID 1
        List<Payment> payments = paymentRepository.findByUserIdOrderByPaymentDateDesc(1L);
        return ResponseEntity.ok(payments);
    }

    // Frontend expects /api/payments/receipts/my-receipts
    @GetMapping("/receipts/my-receipts")
    public ResponseEntity<List<Payment>> getMyReceipts() {
        // For prototype, return completed payments as receipts for user ID 1
        List<Payment> payments = paymentRepository.findByUserIdOrderByPaymentDateDesc(1L);
        return ResponseEntity.ok(payments);
    }

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentRepository.save(payment);
        return ResponseEntity.ok(savedPayment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Payment> updatePayment(@PathVariable Long id, @RequestBody Payment paymentDetails) {
        return paymentRepository.findById(id)
                .map(payment -> {
                    payment.setStatus(paymentDetails.getStatus());
                    payment.setTransactionId(paymentDetails.getTransactionId());
                    return ResponseEntity.ok(paymentRepository.save(payment));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}