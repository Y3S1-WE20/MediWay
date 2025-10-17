package com.mediway.backend.controller;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/payments")
@CrossOrigin(origins = "http://localhost:5174")
public class SimplePayPalController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Value("${paypal.client.id:YOUR_SANDBOX_CLIENT_ID}")
    private String paypalClientId;

    @Value("${paypal.client.secret:YOUR_SANDBOX_CLIENT_SECRET}")
    private String paypalClientSecret;

    @Value("${paypal.mode:sandbox}")
    private String paypalMode;

    // CREATE PAYMENT
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            Long appointmentId = Long.parseLong(request.get("appointmentId").toString());
            Double amount = Double.parseDouble(request.get("amount").toString());

            // Verify appointment exists
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new Exception("Appointment not found"));

            // Check if payment already exists
            List<Payment> existingPayments = paymentRepository.findByUserId(userId);
            for (Payment p : existingPayments) {
                if (p.getAppointmentId() != null && p.getAppointmentId().equals(appointmentId) 
                    && p.getStatus() == Payment.Status.COMPLETED) {
                    return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Payment already completed for this appointment"
                    ));
                }
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setAppointmentId(appointmentId);
            payment.setUserId(userId);
            payment.setAmount(BigDecimal.valueOf(amount));
            payment.setStatus(Payment.Status.PENDING);
            payment.setPaymentMethod("PAYPAL");
            payment.setPaymentDate(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);

            // Generate PayPal approval URL (simplified for prototype)
            String approvalUrl = generatePayPalApprovalUrl(savedPayment.getId(), amount, appointmentId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentId", savedPayment.getId(),
                "approvalUrl", approvalUrl,
                "message", "Payment created successfully"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment creation failed: " + e.getMessage()
            ));
        }
    }

    // EXECUTE PAYMENT (after PayPal approval)
    @PostMapping("/execute")
    public ResponseEntity<?> executePayment(@RequestBody Map<String, Object> request) {
        try {
            Long paymentId = Long.parseLong(request.get("paymentId").toString());
            String transactionId = request.get("transactionId") != null ? 
                                 request.get("transactionId").toString() : "MOCK-" + System.currentTimeMillis();

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new Exception("Payment not found"));

            // Update payment status
            payment.setStatus(Payment.Status.COMPLETED);
            payment.setTransactionId(transactionId);
            paymentRepository.save(payment);

            // Update appointment status to COMPLETED
            if (payment.getAppointmentId() != null) {
                Appointment appointment = appointmentRepository.findById(payment.getAppointmentId())
                        .orElseThrow(() -> new Exception("Appointment not found"));

                if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
                    appointment.setStatus(Appointment.Status.COMPLETED);
                    appointmentRepository.save(appointment);
                }
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful! Appointment confirmed.",
                "paymentId", payment.getId(),
                "appointmentId", payment.getAppointmentId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment execution failed: " + e.getMessage()
            ));
        }
    }

    // CANCEL PAYMENT
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelPayment(@RequestBody Map<String, Object> request) {
        try {
            Long paymentId = Long.parseLong(request.get("paymentId").toString());

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new Exception("Payment not found"));

            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment cancelled"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment cancellation failed: " + e.getMessage()
            ));
        }
    }

    // GET MY PAYMENTS
    @GetMapping("/my-payments")
    public ResponseEntity<?> getMyPayments(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            List<Payment> payments = paymentRepository.findByUserId(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error fetching payments: " + e.getMessage()
            ));
        }
    }

    // GET MY RECEIPTS
    @GetMapping("/receipts/my-receipts")
    public ResponseEntity<?> getMyReceipts(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            List<Payment> payments = paymentRepository.findByUserId(userId).stream()
                    .filter(p -> p.getStatus() == Payment.Status.COMPLETED)
                    .toList();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error fetching receipts: " + e.getMessage()
            ));
        }
    }

    // Helper: Generate PayPal approval URL (PROTOTYPE - simplified)
    private String generatePayPalApprovalUrl(Long paymentId, Double amount, Long appointmentId) {
        // In production, use PayPal SDK to create payment
        // For prototype, redirect to our simulated PayPal checkout page
        
        // Return URL to our simulated PayPal page
        return "http://localhost:5174/paypal-checkout?paymentId=" + paymentId + 
               "&appointmentId=" + appointmentId + 
               "&amount=" + amount;
    }
}
