package com.mediway.backend.controller;

import com.mediway.backend.dto.request.PaymentRequest;
import com.mediway.backend.dto.response.PaymentResponse;
import com.mediway.backend.dto.response.ReceiptResponse;
import com.mediway.backend.service.PayPalService;
import com.mediway.backend.service.ReceiptService;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for payment operations
 * Handles PayPal payment creation, execution, and receipt management
 */
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class PaymentController {

    private final PayPalService payPalService;
    private final ReceiptService receiptService;

    /**
     * Create a new payment
     * POST /api/payments/create
     */
    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest request) {
        try {
            log.info("Creating payment for amount: {} {}", request.getAmount(), request.getCurrency());
            PaymentResponse response = payPalService.createPayment(request);
            return ResponseEntity.ok(response);
        } catch (PayPalRESTException e) {
            log.error("PayPal error creating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("PayPal error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create payment: " + e.getMessage()));
        }
    }

    /**
     * Execute/complete a payment after user approval
     * POST /api/payments/execute
     */
    @PostMapping("/execute")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<?> executePayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        try {
            log.info("Executing payment: {} with payer: {}", paymentId, payerId);
            PaymentResponse response = payPalService.executePayment(paymentId, payerId);
            return ResponseEntity.ok(response);
        } catch (PayPalRESTException e) {
            log.error("PayPal error executing payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("PayPal error: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error executing payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to execute payment: " + e.getMessage()));
        }
    }

    /**
     * Cancel a payment
     * POST /api/payments/cancel
     */
    @PostMapping("/cancel")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<?> cancelPayment(@RequestParam("paymentId") String paymentId) {
        try {
            log.info("Cancelling payment: {}", paymentId);
            PaymentResponse response = payPalService.cancelPayment(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to cancel payment: " + e.getMessage()));
        }
    }

    /**
     * Get payment by ID
     * GET /api/payments/{paymentId}
     */
    @GetMapping("/{paymentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getPayment(@PathVariable UUID paymentId) {
        try {
            PaymentResponse response = payPalService.getPaymentById(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching payment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Payment not found: " + e.getMessage()));
        }
    }

    /**
     * Get all payments for authenticated user
     * GET /api/payments/my-payments
     */
    @GetMapping("/my-payments")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getUserPayments() {
        try {
            List<PaymentResponse> payments = payPalService.getUserPayments();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching user payments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch payments: " + e.getMessage()));
        }
    }

    /**
     * Get payments by appointment
     * GET /api/payments/appointment/{appointmentId}
     */
    @GetMapping("/appointment/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getPaymentsByAppointment(@PathVariable UUID appointmentId) {
        try {
            List<PaymentResponse> payments = payPalService.getPaymentsByAppointment(appointmentId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            log.error("Error fetching appointment payments: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch payments: " + e.getMessage()));
        }
    }

    /**
     * Get receipt by payment ID
     * GET /api/payments/receipt/payment/{paymentId}
     */
    @GetMapping("/receipt/payment/{paymentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getReceiptByPaymentId(@PathVariable UUID paymentId) {
        try {
            ReceiptResponse receipt = receiptService.getReceiptByPaymentId(paymentId);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            log.error("Error fetching receipt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Receipt not found: " + e.getMessage()));
        }
    }

    /**
     * Get receipt by receipt number
     * GET /api/payments/receipt/{receiptNumber}
     */
    @GetMapping("/receipt/{receiptNumber}")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getReceiptByNumber(@PathVariable String receiptNumber) {
        try {
            ReceiptResponse receipt = receiptService.getReceiptByNumber(receiptNumber);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            log.error("Error fetching receipt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Receipt not found: " + e.getMessage()));
        }
    }

    /**
     * Get all receipts for authenticated user
     * GET /api/payments/receipts/my-receipts
     */
    @GetMapping("/receipts/my-receipts")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR', 'ADMIN')")
    public ResponseEntity<?> getUserReceipts() {
        try {
            List<ReceiptResponse> receipts = receiptService.getUserReceipts();
            return ResponseEntity.ok(receipts);
        } catch (Exception e) {
            log.error("Error fetching user receipts: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to fetch receipts: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for payment service
     * GET /api/payments/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Payment Service");
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }

    /**
     * Create error response map
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }
}
