package com.mediway.backend.controller;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/paypal")
@CrossOrigin(origins = "http://localhost:5174")
public class SimplePayPalController {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    // GET PAYMENT DETAILS FOR FRONTEND
    @GetMapping("/details/{appointmentId}")
    public ResponseEntity<?> getPaymentDetails(
            @PathVariable Long appointmentId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Verify appointment exists
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new Exception("Appointment not found"));

            // Check if payment already exists (simplified for now)
            // TODO: Add payment completion check later
            System.out.println("Getting payment details for appointment: " + appointmentId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentCompleted", false,
                "appointmentId", appointmentId,
                "amount", 50.00, // Standard consultation fee
                "currency", "USD"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Error getting payment details: " + e.getMessage()
            ));
        }
    }

    // COMPLETE PAYMENT (after PayPal frontend processing)
    @PostMapping("/complete")
    public ResponseEntity<?> completePayment(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("PayPal completion request received: " + request);

            // Defensive checks and logging
            Object apptObj = request.get("appointmentId");
            Object orderObj = request.get("paypalOrderId");
            Object amountObj = request.get("amount");
            Object userObj = request.get("userId");

            System.out.println("Raw appointmentId: " + apptObj + ", paypalOrderId: " + orderObj + ", amount: " + amountObj + ", userId: " + userObj);

            if (apptObj == null) throw new Exception("appointmentId missing in request payload");
            if (orderObj == null) throw new Exception("paypalOrderId missing in request payload");
            if (amountObj == null) throw new Exception("amount missing in request payload");

            Long appointmentId = Long.parseLong(apptObj.toString());
            String paypalOrderId = orderObj.toString();
            Double amount = Double.parseDouble(amountObj.toString());
            Long userId = userObj != null ? Long.parseLong(userObj.toString()) : 1L;

            System.out.println("Processing PayPal payment - Order ID: " + paypalOrderId + ", Appointment: " + appointmentId + ", Amount: " + amount + ", User: " + userId);

            // Verify appointment exists
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new Exception("Appointment not found"));

            // Check if payment already exists for this order ID to prevent duplicates
            List<Payment> existingPayments = paymentRepository.findAll();
            for (Payment existingPayment : existingPayments) {
                if (paypalOrderId.equals(existingPayment.getTransactionId())) {
                    System.out.println("Payment already exists for order ID: " + paypalOrderId);
                    return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Payment already completed!",
                        "paymentId", existingPayment.getId(),
                        "appointmentId", appointmentId,
                        "transactionId", paypalOrderId
                    ));
                }
            }

            // Create payment record
            Payment payment = new Payment();
            payment.setAppointmentId(appointmentId);
            payment.setUserId(userId);
            payment.setAmount(BigDecimal.valueOf(amount));
            payment.setStatus(Payment.Status.COMPLETED);
            payment.setPaymentMethod("PAYPAL");
            payment.setTransactionId(paypalOrderId);
            payment.setPaymentDate(LocalDateTime.now());
            
            Payment savedPayment = paymentRepository.save(payment);
            System.out.println("Payment saved with ID: " + savedPayment.getId());

            // Update appointment status to COMPLETED
            if (appointment.getStatus() == Appointment.Status.SCHEDULED) {
                appointment.setStatus(Appointment.Status.COMPLETED);
                appointmentRepository.save(appointment);
                System.out.println("Appointment status updated to COMPLETED");
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment completed successfully! Appointment confirmed.",
                "paymentId", savedPayment.getId(),
                "appointmentId", appointmentId,
                "transactionId", paypalOrderId
            ));
        } catch (Exception e) {
            System.err.println("PayPal completion error: " + e.getClass().getName() + " - " + e.getMessage());
            e.printStackTrace();
            // Return detailed error in development to help debugging
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment completion failed: " + e.getMessage(),
                "errorClass", e.getClass().getName()
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

}
