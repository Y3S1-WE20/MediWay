package com.mediway.backend.service;

import com.mediway.backend.dto.response.ReceiptResponse;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.entity.Receipt;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.ReceiptRepository;
import com.mediway.backend.repository.UserRepository;
import com.paypal.api.payments.Sale;
import com.paypal.api.payments.Transaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for generating and managing payment receipts
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final UserRepository userRepository;

    /**
     * Generate receipt after successful payment
     */
    @Transactional
    public Receipt generateReceipt(Payment payment, com.paypal.api.payments.Payment paypalPayment) {
        log.info("Generating receipt for payment: {}", payment.getPaymentId());

        // Check if receipt already exists
        if (receiptRepository.existsByPaymentId(payment.getPaymentId())) {
            log.warn("Receipt already exists for payment: {}", payment.getPaymentId());
            return receiptRepository.findByPaymentId(payment.getPaymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Receipt not found"));
        }

        // Extract transaction details from PayPal payment
        Transaction transaction = paypalPayment.getTransactions().get(0);
        Sale sale = transaction.getRelatedResources().get(0).getSale();

        // Get payer information
        String payerEmail = paypalPayment.getPayer().getPayerInfo().getEmail();
        String payerName = paypalPayment.getPayer().getPayerInfo().getFirstName() + " " +
                paypalPayment.getPayer().getPayerInfo().getLastName();

        // Generate unique receipt number
        String receiptNumber = generateUniqueReceiptNumber();

        // Create receipt
        Receipt receipt = Receipt.builder()
                .receiptNumber(receiptNumber)
                .paymentId(payment.getPaymentId())
                .userId(payment.getUserId())
                .appointmentId(payment.getAppointmentId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .paymentMethod(payment.getPaymentMethod().name())
                .transactionId(sale.getId())
                .payerEmail(payerEmail)
                .payerName(payerName)
                .description(payment.getDescription())
                .paymentDate(payment.getCompletedAt())
                .build();

        Receipt savedReceipt = receiptRepository.save(receipt);
        log.info("Receipt generated successfully: {}", receiptNumber);

        return savedReceipt;
    }

    /**
     * Get receipt by ID
     */
    public ReceiptResponse getReceiptById(UUID receiptId) {
        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with ID: " + receiptId));

        return mapToReceiptResponse(receipt);
    }

    /**
     * Get receipt by receipt number
     */
    public ReceiptResponse getReceiptByNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with number: " + receiptNumber));

        return mapToReceiptResponse(receipt);
    }

    /**
     * Get receipt by payment ID
     */
    public ReceiptResponse getReceiptByPaymentId(UUID paymentId) {
        Receipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for payment ID: " + paymentId));

        return mapToReceiptResponse(receipt);
    }

    /**
     * Get all receipts for authenticated user
     */
    public List<ReceiptResponse> getUserReceipts() {
        User user = getAuthenticatedUser();
        List<Receipt> receipts = receiptRepository.findByUserIdOrderByCreatedAtDesc(user.getUserId());

        return receipts.stream()
                .map(this::mapToReceiptResponse)
                .toList();
    }

    /**
     * Get receipts by appointment
     */
    public List<ReceiptResponse> getReceiptsByAppointment(UUID appointmentId) {
        List<Receipt> receipts = receiptRepository.findByAppointmentIdOrderByCreatedAtDesc(appointmentId);

        return receipts.stream()
                .map(this::mapToReceiptResponse)
                .toList();
    }

    /**
     * Generate unique receipt number
     */
    private String generateUniqueReceiptNumber() {
        String receiptNumber;
        do {
            receiptNumber = Receipt.generateReceiptNumber();
        } while (receiptRepository.existsByReceiptNumber(receiptNumber));

        return receiptNumber;
    }

    /**
     * Map Receipt entity to ReceiptResponse DTO
     */
    private ReceiptResponse mapToReceiptResponse(Receipt receipt) {
        return ReceiptResponse.builder()
                .receiptId(receipt.getReceiptId())
                .receiptNumber(receipt.getReceiptNumber())
                .paymentId(receipt.getPaymentId())
                .userId(receipt.getUserId())
                .appointmentId(receipt.getAppointmentId())
                .amount(receipt.getAmount())
                .currency(receipt.getCurrency())
                .paymentMethod(receipt.getPaymentMethod())
                .transactionId(receipt.getTransactionId())
                .payerEmail(receipt.getPayerEmail())
                .payerName(receipt.getPayerName())
                .description(receipt.getDescription())
                .paymentDate(receipt.getPaymentDate())
                .createdAt(receipt.getCreatedAt())
                .pdfPath(receipt.getPdfPath())
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
