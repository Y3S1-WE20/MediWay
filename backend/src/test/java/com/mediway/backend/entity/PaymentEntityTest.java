package com.mediway.backend.entity;

/*
 * TESTS SUMMARY (PaymentEntityTest):
 * - Default constructor and defaults                    : Positive
 * - Parameterized constructor                           : Positive
 * - Getters/Setters and nullable handling               : Positive / Edge
 * - Status enum coverage (PENDING/COMPLETED/FAILED)    : Edge
 * - Decimal/zero/large amount handling                  : Edge
 * - Payment ID / transaction ID formatting tests       : Edge
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Payment Entity Tests")
class PaymentEntityTest {

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
    }

    // Positive: Create Payment with default constructor and default values
    @Test
    @DisplayName("Should create Payment with default constructor and default values")
    void testDefaultConstructor() {
        Payment newPayment = new Payment();
        assertNotNull(newPayment);
        assertNull(newPayment.getId());
        assertEquals(Payment.Status.PENDING, newPayment.getStatus());
        assertNotNull(newPayment.getPaymentDate());
    }

    // Positive: Create Payment with parameterized constructor
    @Test
    @DisplayName("Should create Payment with parameterized constructor")
    void testParameterizedConstructor() {
        BigDecimal amount = new BigDecimal("150.50");
        Payment newPayment = new Payment(1L, 2L, amount, "PayPal");
        
        assertEquals(1L, newPayment.getUserId());
        assertEquals(2L, newPayment.getAppointmentId());
        assertEquals(amount, newPayment.getAmount());
        assertEquals("PayPal", newPayment.getPaymentMethod());
        assertEquals(Payment.Status.PENDING, newPayment.getStatus());
    }

    // Positive: Set and get all Payment fields correctly
    @Test
    @DisplayName("Should set and get all Payment fields correctly")
    void testGettersAndSetters() {
        BigDecimal amount = new BigDecimal("250.75");
        LocalDateTime paymentDate = LocalDateTime.of(2025, 10, 23, 16, 0);

        payment.setId(1L);
        payment.setUserId(10L);
        payment.setAppointmentId(20L);
        payment.setAmount(amount);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setPaymentMethod("Credit Card");
        payment.setTransactionId("TXN123456");
        payment.setPaypalPaymentId("PAY-789");
        payment.setPaymentDate(paymentDate);

        assertEquals(1L, payment.getId());
        assertEquals(10L, payment.getUserId());
        assertEquals(20L, payment.getAppointmentId());
        assertEquals(amount, payment.getAmount());
        assertEquals(Payment.Status.COMPLETED, payment.getStatus());
        assertEquals("Credit Card", payment.getPaymentMethod());
        assertEquals("TXN123456", payment.getTransactionId());
        assertEquals("PAY-789", payment.getPaypalPaymentId());
        assertEquals(paymentDate, payment.getPaymentDate());
    }

    // Edge: Handle null values for optional fields
    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullableFields() {
        payment.setPaymentMethod(null);
        payment.setTransactionId(null);
        payment.setPaypalPaymentId(null);

        assertNull(payment.getPaymentMethod());
        assertNull(payment.getTransactionId());
        assertNull(payment.getPaypalPaymentId());
    }

    // Edge: Handle all status values
    @Test
    @DisplayName("Should handle all status values")
    void testAllStatusValues() {
        payment.setStatus(Payment.Status.PENDING);
        assertEquals(Payment.Status.PENDING, payment.getStatus());

        payment.setStatus(Payment.Status.COMPLETED);
        assertEquals(Payment.Status.COMPLETED, payment.getStatus());

        payment.setStatus(Payment.Status.FAILED);
        assertEquals(Payment.Status.FAILED, payment.getStatus());
    }

    // Edge: Handle different payment methods
    @Test
    @DisplayName("Should handle different payment methods")
    void testDifferentPaymentMethods() {
        String[] methods = {"PayPal", "Credit Card", "Debit Card", "Cash", "Bank Transfer"};

        for (String method : methods) {
            payment.setPaymentMethod(method);
            assertEquals(method, payment.getPaymentMethod());
        }
    }

    // Edge: Handle PrePersist onCreate callback
    @Test
    @DisplayName("Should handle PrePersist onCreate callback")
    void testOnCreate() {
        Payment newPayment = new Payment();
        newPayment.setPaymentDate(null);
        newPayment.setStatus(null);
        newPayment.onCreate();

        assertNotNull(newPayment.getPaymentDate());
        assertEquals(Payment.Status.PENDING, newPayment.getStatus());
    }

    // Edge: Not override existing values in onCreate
    @Test
    @DisplayName("Should not override existing values in onCreate")
    void testOnCreateWithExistingValues() {
        LocalDateTime existingDate = LocalDateTime.of(2023, 5, 15, 12, 0);
        payment.setPaymentDate(existingDate);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.onCreate();

        assertEquals(existingDate, payment.getPaymentDate());
        assertEquals(Payment.Status.COMPLETED, payment.getStatus());
    }

    // Edge: Handle decimal amounts correctly
    @Test
    @DisplayName("Should handle decimal amounts correctly")
    void testDecimalAmounts() {
        payment.setAmount(new BigDecimal("0.01"));
        assertEquals(new BigDecimal("0.01"), payment.getAmount());

        payment.setAmount(new BigDecimal("9999.99"));
        assertEquals(new BigDecimal("9999.99"), payment.getAmount());

        payment.setAmount(new BigDecimal("123.456"));
        assertEquals(new BigDecimal("123.456"), payment.getAmount());
    }

    // Edge: Handle zero amount
    @Test
    @DisplayName("Should handle zero amount")
    void testZeroAmount() {
        payment.setAmount(BigDecimal.ZERO);
        assertEquals(BigDecimal.ZERO, payment.getAmount());
    }

    // Edge: Handle large amounts
    @Test
    @DisplayName("Should handle large amounts")
    void testLargeAmounts() {
        BigDecimal largeAmount = new BigDecimal("99999999.99");
        payment.setAmount(largeAmount);
        assertEquals(largeAmount, payment.getAmount());
    }

    // Edge: Handle PayPal payment ID format
    @Test
    @DisplayName("Should handle PayPal payment ID format")
    void testPayPalPaymentIdFormat() {
        payment.setPaypalPaymentId("PAYID-M123456-789012345");
        assertEquals("PAYID-M123456-789012345", payment.getPaypalPaymentId());
    }

    // Edge: Handle transaction ID format
    @Test
    @DisplayName("Should handle transaction ID format")
    void testTransactionIdFormat() {
        payment.setTransactionId("TXN-2025-10-23-123456");
        assertEquals("TXN-2025-10-23-123456", payment.getTransactionId());
    }
}
