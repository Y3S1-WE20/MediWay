package com.mediway.backend.service;

import com.mediway.backend.dto.request.PaymentRequest;
import com.mediway.backend.dto.response.PaymentResponse;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.UserRepository;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for PayPal payment integration
 * Handles payment creation, execution, and cancellation
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PayPalService {

    private final APIContext apiContext;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ReceiptService receiptService;

    /**
     * Create a PayPal payment
     */
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) throws PayPalRESTException {
        log.info("Creating PayPal payment for amount: {} {}", request.getAmount(), request.getCurrency());

        // Get authenticated user
        User user = getAuthenticatedUser();

        // Create PayPal Amount
        Amount amount = new Amount();
        amount.setCurrency(request.getCurrency());
        amount.setTotal(String.format("%.2f", request.getAmount()));

        // Create transaction
        Transaction transaction = new Transaction();
        transaction.setDescription(request.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        // Set payer
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");

        // Create payment
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // Set redirect URLs
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(request.getCancelUrl());
        redirectUrls.setReturnUrl(request.getReturnUrl());
        payment.setRedirectUrls(redirectUrls);

        // Execute PayPal API call
        com.paypal.api.payments.Payment createdPayment = payment.create(apiContext);

        // Get approval URL
        String approvalUrl = createdPayment.getLinks().stream()
                .filter(link -> link.getRel().equals("approval_url"))
                .findFirst()
                .map(Links::getHref)
                .orElseThrow(() -> new RuntimeException("Approval URL not found"));

        // Save payment to database
        Payment paymentEntity = Payment.builder()
                .userId(user.getUserId())
                .appointmentId(request.getAppointmentId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status(Payment.PaymentStatus.CREATED)
                .paymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()))
                .paypalPaymentId(createdPayment.getId())
                .description(request.getDescription())
                .returnUrl(request.getReturnUrl())
                .cancelUrl(request.getCancelUrl())
                .approvalUrl(approvalUrl)
                .build();

        Payment savedPayment = paymentRepository.save(paymentEntity);

        log.info("Payment created successfully with ID: {}", savedPayment.getPaymentId());

        return mapToPaymentResponse(savedPayment, "Payment created successfully. Please complete the payment.");
    }

    /**
     * Execute a PayPal payment after user approval
     */
    @Transactional
    public PaymentResponse executePayment(String paymentId, String payerId) throws PayPalRESTException {
        log.info("Executing PayPal payment: {} with payer: {}", paymentId, payerId);

        // Find payment in database
        Payment paymentEntity = paymentRepository.findByPaypalPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with PayPal ID: " + paymentId));

        // Execute PayPal payment
        com.paypal.api.payments.Payment payment = new com.paypal.api.payments.Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        com.paypal.api.payments.Payment executedPayment = payment.execute(apiContext, paymentExecution);

        // Update payment status
        if (executedPayment.getState().equals("approved")) {
            paymentEntity.setStatus(Payment.PaymentStatus.COMPLETED);
            paymentEntity.setPayerId(payerId);
            paymentEntity.setCompletedAt(LocalDateTime.now());
            paymentRepository.save(paymentEntity);

            // Generate receipt
            receiptService.generateReceipt(paymentEntity, executedPayment);

            log.info("Payment executed successfully: {}", paymentId);
            return mapToPaymentResponse(paymentEntity, "Payment completed successfully!");
        } else {
            paymentEntity.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(paymentEntity);

            log.error("Payment execution failed: {}", paymentId);
            throw new RuntimeException("Payment execution failed");
        }
    }

    /**
     * Cancel a payment
     */
    @Transactional
    public PaymentResponse cancelPayment(String paymentId) {
        log.info("Cancelling payment: {}", paymentId);

        Payment payment = paymentRepository.findByPaypalPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with PayPal ID: " + paymentId));

        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        Payment savedPayment = paymentRepository.save(payment);

        log.info("Payment cancelled: {}", paymentId);
        return mapToPaymentResponse(savedPayment, "Payment cancelled successfully.");
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        return mapToPaymentResponse(payment, null);
    }

    /**
     * Get all payments for authenticated user
     */
    public List<PaymentResponse> getUserPayments() {
        User user = getAuthenticatedUser();
        List<Payment> payments = paymentRepository.findByUserIdOrderByCreatedAtDesc(user.getUserId());

        return payments.stream()
                .map(payment -> mapToPaymentResponse(payment, null))
                .toList();
    }

    /**
     * Get payments by appointment
     */
    public List<PaymentResponse> getPaymentsByAppointment(UUID appointmentId) {
        List<Payment> payments = paymentRepository.findByAppointmentIdOrderByCreatedAtDesc(appointmentId);

        return payments.stream()
                .map(payment -> mapToPaymentResponse(payment, null))
                .toList();
    }

    /**
     * Map Payment entity to PaymentResponse DTO
     */
    private PaymentResponse mapToPaymentResponse(Payment payment, String message) {
        return PaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .appointmentId(payment.getAppointmentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus().name())
                .paymentMethod(payment.getPaymentMethod().name())
                .paypalPaymentId(payment.getPaypalPaymentId())
                .description(payment.getDescription())
                .approvalUrl(payment.getApprovalUrl())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .message(message)
                .build();
    }

    /**
     * Get authenticated user from security context
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
}
