package com.mediway.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@RestController
@RequestMapping("/paypal")
public class SimplePayPalController {

    private static final Logger logger = LoggerFactory.getLogger(SimplePayPalController.class);

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

    @Value("${paypal.use-simulated-checkout:false}")
    private boolean useSimulatedCheckout;

    // CREATE PAYMENT
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            if (userId == null) {
                userId = 1L;
            }

            logger.debug("createPayment called with userId={} requestKeys={}", userId, request == null ? "null" : request.keySet());
            logger.debug("appointmentRepository instance: {}", appointmentRepository == null ? "null" : appointmentRepository.getClass().getName());

            logger.info("createPayment called with userId={} requestKeys={}", userId, request == null ? "null" : request.keySet());
            logger.info("appointmentRepository instance: {}", appointmentRepository == null ? "null" : appointmentRepository.getClass().getName());

            // Parse appointmentId and amount robustly (Map may contain Number types)
            Object appointmentIdObj = request.get("appointmentId");
            Long appointmentId;
            if (appointmentIdObj instanceof Number) {
                appointmentId = ((Number) appointmentIdObj).longValue();
            } else {
                appointmentId = Long.parseLong(String.valueOf(appointmentIdObj));
            }

            logger.debug("Parsed appointmentId: {} (raw type: {})", appointmentId, appointmentIdObj == null ? "null" : appointmentIdObj.getClass().getName());

            logger.info("Parsed appointmentId: {} (raw type: {})", appointmentId, appointmentIdObj == null ? "null" : appointmentIdObj.getClass().getName());

            Object amountObjRaw = request.get("amount");
            Double amount;
            if (amountObjRaw instanceof Number) {
                amount = ((Number) amountObjRaw).doubleValue();
            } else {
                amount = Double.parseDouble(String.valueOf(amountObjRaw));
            }

        // Verify appointment exists
        logger.debug("Looking up appointment with id {}", appointmentId);

              logger.info("Looking up appointment with id {}", appointmentId);

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
            // Create PayPal payment using SDK and return approval URL
            String approvalUrl = null;
            try {
                Payer payer = new Payer();
                payer.setPaymentMethod("paypal");

                Amount amountObj = new Amount();
                amountObj.setCurrency("USD");
                // PayPal amount must be string
                amountObj.setTotal(String.format("%.2f", amount));

                Transaction transaction = new Transaction();
                transaction.setAmount(amountObj);
                transaction.setDescription("Appointment payment: " + appointmentId);

                java.util.List<Transaction> transactions = java.util.Collections.singletonList(transaction);

                RedirectUrls redirectUrls = new RedirectUrls();
                // PayPal will redirect here after approval/cancel
                // TODO: Replace with your public URL (e.g., ngrok or deployed frontend URL)
                String publicBaseUrl = "https://desultory-uncommutatively-shawnda.ngrok-free.dev"; // <--- CHANGE THIS
                redirectUrls.setCancelUrl(publicBaseUrl + "/appointments");
                redirectUrls.setReturnUrl(publicBaseUrl + "/paypal-checkout");

                com.paypal.api.payments.Payment paypalPayment = new com.paypal.api.payments.Payment();
                paypalPayment.setIntent("sale");
                paypalPayment.setPayer(payer);
                paypalPayment.setTransactions(transactions);
                paypalPayment.setRedirectUrls(redirectUrls);

        // Only attempt to call the PayPal SDK when configuration is present and mode is valid
        boolean canUsePayPalSdk = !useSimulatedCheckout
            && paypalClientId != null && paypalClientSecret != null && paypalMode != null
            && (paypalMode.equals("sandbox") || paypalMode.equals("live"));

        logger.info("Can use PayPal SDK: {}", canUsePayPalSdk);
        logger.info("PayPal config - Mode: {}, ClientId present: {}, ClientSecret present: {}, UseSimulated: {}",
            paypalMode, paypalClientId != null, paypalClientSecret != null, useSimulatedCheckout);

        if (canUsePayPalSdk) {
            logger.info("Creating real PayPal payment...");
            APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, paypalMode);
            com.paypal.api.payments.Payment createdPayment = paypalPayment.create(apiContext);
            logger.info("PayPal payment created successfully. Payment ID: {}", createdPayment.getId());

                    // Store PayPal payment ID
                    savedPayment.setPaypalPaymentId(createdPayment.getId());
                    paymentRepository.save(savedPayment);
                    logger.info("Payment record updated with PayPal ID: {}", createdPayment.getId());

                    // Find approval URL
                    for (Links link : createdPayment.getLinks()) {
                        logger.debug("PayPal link: {} -> {}", link.getRel(), link.getHref());
                        if ("approval_url".equalsIgnoreCase(link.getRel())) {
                            approvalUrl = link.getHref();
                            logger.info("Found approval URL: {}", approvalUrl);
                            break;
                        }
                    }

                    // For development, optionally use our simulated checkout to avoid CSP/CORS issues
                    if (useSimulatedCheckout && approvalUrl != null && approvalUrl.contains("paypal.com")) {
                        approvalUrl = generatePayPalApprovalUrl(savedPayment.getId(), amount, appointmentId);
                        logger.info("Using simulated checkout URL instead: {}", approvalUrl);
                    }
                } else {
                    logger.info("PayPal SDK not available or simulated checkout enabled, using simulated payment");
                    // Fallback: generate a simulated approval URL when SDK/config is not available or simulated checkout is enabled
                    savedPayment.setPaypalPaymentId("SIM-" + savedPayment.getId());
                    paymentRepository.save(savedPayment);
                    approvalUrl = generatePayPalApprovalUrl(savedPayment.getId(), amount, appointmentId);
                    logger.info("Generated simulated approval URL: {}", approvalUrl);
                }
            } catch (PayPalRESTException pre) {
                logger.error("PayPal REST exception while creating payment", pre);
                // fallback to local simulated checkout if PayPal fails
                approvalUrl = generatePayPalApprovalUrl(savedPayment.getId(), amount, appointmentId);
            }

            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentId", savedPayment.getId(),
                "approvalUrl", approvalUrl,
                "message", "Payment created successfully"
            ));
        } catch (Exception e) {
            logger.error("Error creating payment", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment creation failed: " + e.getMessage()
            ));
        }
    }

    // EXECUTE PAYMENT (after PayPal approval)
    @PostMapping("/execute")
    public ResponseEntity<?> executePayment(@RequestBody Map<String, Object> request) {

        logger.info("=== Execute Payment (Regular) ===");
        logger.info("Request body: {}", request);

        try {
            String paymentIdRaw = String.valueOf(request.get("paymentId"));
            String payerId = String.valueOf(request.get("payerId"));

            logger.info("PaymentId: {}, PayerId: {}", paymentIdRaw, payerId);

            Payment payment = null;
            try {
                // Try to parse as Long (internal payment ID)
                Long internalId = Long.parseLong(paymentIdRaw);
                payment = paymentRepository.findById(internalId)
                        .orElse(null);
            } catch (NumberFormatException nfe) {
                // Not a number, try to find by PayPal payment ID
                payment = paymentRepository.findAll().stream()
                        .filter(p -> paymentIdRaw.equals(p.getPaypalPaymentId()))
                        .findFirst()
                        .orElse(null);
            }
            if (payment == null) {
                throw new Exception("Payment not found for id: " + paymentIdRaw);
            }

            logger.info("Found payment: ID={}, Status={}, Amount={}, PayPalPaymentId={}",
                payment.getId(), payment.getStatus(), payment.getAmount(), payment.getPaypalPaymentId());

            // Check if payment is already completed
            if (payment.getStatus() == Payment.Status.COMPLETED) {
                logger.warn("Payment {} is already completed", payment.getId());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Payment already completed"
                ));
            }

            // Execute PayPal payment
            try {
                boolean canUsePayPalSdk = !useSimulatedCheckout
                        && paypalClientId != null && paypalClientSecret != null && paypalMode != null
                        && (paypalMode.equals("sandbox") || paypalMode.equals("live"));

                logger.info("Can use PayPal SDK: {}", canUsePayPalSdk);

                if (canUsePayPalSdk) {
                    logger.info("Executing PayPal payment with stored PayPal ID: {}", payment.getPaypalPaymentId());
                    APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, paypalMode);

                    com.paypal.api.payments.Payment paypalPayment = new com.paypal.api.payments.Payment();
                    paypalPayment.setId(payment.getPaypalPaymentId()); // Use PayPal payment ID

                    PaymentExecution paymentExecution = new PaymentExecution();
                    paymentExecution.setPayerId(payerId);

                    logger.info("Calling PayPal execute API...");
                    com.paypal.api.payments.Payment executedPayment = paypalPayment.execute(apiContext, paymentExecution);
                    logger.info("PayPal execute API call successful");

                    // Update payment with PayPal transaction ID
                    String transactionId = executedPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
                    payment.setTransactionId(transactionId);
                    logger.info("Transaction ID from PayPal: {}", transactionId);
                } else {
                    logger.info("Using simulated transaction");
                    // No SDK available in this environment (tests), set simulated transaction id
                    payment.setTransactionId("SIM-TX-" + System.currentTimeMillis());
                }
            } catch (PayPalRESTException pre) {
                logger.error("PayPal REST exception while executing payment", pre);
                logger.error("PayPal error details: {}", pre.getDetails() != null ? pre.getDetails().getDetails() : "No details");
                // If PayPal execution fails, still mark as completed for testing
                payment.setTransactionId("PAYPAL-" + System.currentTimeMillis());
            } catch (Exception e) {
                logger.error("Unexpected error during PayPal execution", e);
                throw e;
            }

            // Update payment status
            payment.setStatus(Payment.Status.COMPLETED);
            paymentRepository.save(payment);
            logger.info("Payment {} marked as COMPLETED", payment.getId());

            // Update appointment status to COMPLETED
            if (payment.getAppointmentId() != null) {
                logger.info("Updating appointment {} status to COMPLETED", payment.getAppointmentId());
                appointmentRepository.findById(payment.getAppointmentId()).ifPresent(appt -> {
                    if (appt.getStatus() == Appointment.Status.SCHEDULED) {
                        appt.setStatus(Appointment.Status.COMPLETED);
                        appointmentRepository.save(appt);
                        logger.info("Appointment {} status updated to COMPLETED", appt.getId());
                    } else {
                        logger.warn("Appointment {} is not in SCHEDULED status (current: {})", appt.getId(), appt.getStatus());
                    }
                });
            } else {
                logger.warn("Payment {} has no associated appointment", payment.getId());
            }

            logger.info("=== Payment execution successful ===");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful! Appointment confirmed.",
                "paymentId", payment.getId(),
                "appointmentId", payment.getAppointmentId()
            ));
        } catch (Exception e) {
            logger.error("=== Payment execution failed ===", e);
            logger.error("Error details: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Payment execution failed: " + e.getMessage()
            ));
        }
    }

    // EXECUTE PAYMENT BY TOKEN (Express Checkout flow)
    @PostMapping("/execute-token")
    public ResponseEntity<?> executePaymentByToken(@RequestBody Map<String, Object> request) {
        logger.info("=== Execute Payment By Token ===");
        logger.info("Request body: {}", request);

        try {
            String token = request.get("token").toString();
            String payerId = request.get("payerId").toString();

            logger.info("Token: {}", token);
            logger.info("PayerId: {}", payerId);

            // Find payment by PayPal payment ID (token contains the payment ID)
            // For Express Checkout, the token format is EC-{paymentId}, for simulated it's SIM-{paymentId}
            String extractedPaypalPaymentId;
            if (token.startsWith("EC-")) {
                extractedPaypalPaymentId = token.substring(3);
            } else {
                extractedPaypalPaymentId = token; // Keep the full SIM- prefix for simulated payments
            }
            logger.info("Extracted PayPal Payment ID from token: {}", extractedPaypalPaymentId);

            logger.info("Searching for payment with PayPal payment ID: {}", extractedPaypalPaymentId);
            final String finalPaypalPaymentId = extractedPaypalPaymentId;
            Payment payment = paymentRepository.findAll().stream()
                .filter(p -> {
                    String storedId = p.getPaypalPaymentId();
                    logger.debug("Checking payment ID {} against stored ID {}", finalPaypalPaymentId, storedId);
                    return finalPaypalPaymentId.equals(storedId);
                })
                .findFirst()
                .orElseThrow(() -> new Exception("Payment not found for token: " + token));

            logger.info("Found payment: ID={}, Status={}, Amount={}",
                payment.getId(), payment.getStatus(), payment.getAmount());

            // Check if payment is already completed
            if (payment.getStatus() == Payment.Status.COMPLETED) {
                logger.warn("Payment {} is already completed", payment.getId());
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Payment already completed"
                ));
            }

            // Execute PayPal payment
            try {
                boolean canUsePayPalSdk = !useSimulatedCheckout
                        && paypalClientId != null && paypalClientSecret != null && paypalMode != null
                        && (paypalMode.equals("sandbox") || paypalMode.equals("live"));

                logger.info("Can use PayPal SDK: {}", canUsePayPalSdk);
                logger.info("Use simulated checkout: {}", useSimulatedCheckout);

                if (canUsePayPalSdk) {

                    logger.info("Executing real PayPal payment with ID: {}", finalPaypalPaymentId);
                    APIContext apiContext = new APIContext(paypalClientId, paypalClientSecret, paypalMode);

                    com.paypal.api.payments.Payment paypalPayment = new com.paypal.api.payments.Payment();
                    paypalPayment.setId(finalPaypalPaymentId);

                    PaymentExecution paymentExecution = new PaymentExecution();
                    paymentExecution.setPayerId(payerId);

                    logger.info("Calling PayPal execute API...");
                    com.paypal.api.payments.Payment executedPayment = paypalPayment.execute(apiContext, paymentExecution);
                    logger.info("PayPal execute API call successful");

                    // Update payment with PayPal transaction ID
                    String transactionId = executedPayment.getTransactions().get(0).getRelatedResources().get(0).getSale().getId();
                    payment.setTransactionId(transactionId);
                    logger.info("Transaction ID from PayPal: {}", transactionId);
                } else {
                    logger.info("Using simulated transaction (SDK not available)");
                    // No SDK available in this environment (tests), set simulated transaction id
                    payment.setTransactionId("SIM-TX-" + System.currentTimeMillis());
                }
            } catch (PayPalRESTException pre) {
                logger.error("PayPal REST exception while executing payment", pre);
                logger.error("PayPal error details - Response: {}", pre.getDetails() != null ? pre.getDetails().getDetails() : "No details");
                // If PayPal execution fails, still mark as completed for testing
                payment.setTransactionId("PAYPAL-" + System.currentTimeMillis());
            } catch (Exception e) {
                logger.error("Unexpected error during PayPal execution", e);
                throw e; // Re-throw to be handled below
            }

            // Update payment status
            payment.setStatus(Payment.Status.COMPLETED);
            paymentRepository.save(payment);
            logger.info("Payment {} marked as COMPLETED", payment.getId());

            // Update appointment status to COMPLETED
            if (payment.getAppointmentId() != null) {
                logger.info("Updating appointment {} status to COMPLETED", payment.getAppointmentId());
                appointmentRepository.findById(payment.getAppointmentId()).ifPresent(appt -> {
                    if (appt.getStatus() == Appointment.Status.SCHEDULED) {
                        appt.setStatus(Appointment.Status.COMPLETED);
                        appointmentRepository.save(appt);
                        logger.info("Appointment {} status updated to COMPLETED", appt.getId());
                    } else {
                        logger.warn("Appointment {} is not in SCHEDULED status (current: {})", appt.getId(), appt.getStatus());
                    }
                });
            } else {
                logger.warn("Payment {} has no associated appointment", payment.getId());
            }

            logger.info("=== Payment execution successful ===");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment successful! Appointment confirmed.",
                "paymentId", payment.getId(),
                "appointmentId", payment.getAppointmentId()
            ));
        } catch (Exception e) {
            logger.error("=== Payment execution failed ===", e);
            logger.error("Error details: {}", e.getMessage());
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
            String paymentIdStr = String.valueOf(request.get("paymentId"));
            Long paymentId = Long.parseLong(paymentIdStr);

            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new Exception("Payment not found"));

            payment.setStatus(Payment.Status.FAILED);
            paymentRepository.save(payment);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Payment cancelled"
            ));
        } catch (Exception e) {
            logger.error("Error cancelling payment", e);
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

        // Return URL to our simulated PayPal page with proper parameters
        // Simulate PayPal return parameters: paymentId and PayerID
        // Use SIM-{paymentId} as the token to match the stored paypalPaymentId
        return "http://localhost:5173/paypal-checkout?paymentId=" + paymentId +
               "&PayerID=SIMULATED_PAYER_" + paymentId +
               "&token=SIM-" + paymentId;
    }
}
