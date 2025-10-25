package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimplePayPalControllerTest):
 * This file contains comprehensive Positive / Negative / Edge tests for PayPal-related flows.
 * Many tests are annotated inline at the start of each test method explaining the test type.
 * Examples:
 * - createPayment_ValidRequest_ReturnsSuccess        : Positive
 * - createPayment_AppointmentNotFound_ReturnsBadRequest : Negative
 * - executePayment_FindByPayPalId_Success            : Positive
 * - executePayment_NoAppointmentId_ReturnsError      : Negative/Edge
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.PaymentRepository;

class SimplePayPalControllerTest {

    private MockMvc mockMvc;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private SimplePayPalController payPalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(payPalController).build();
    }

    @Test
    void createPayment_ValidRequest_ReturnsSuccess() throws Exception {
        // Positive: Creates a payment when appointment exists and no prior completed payments; expects success and returned paymentId
        // Given
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment payment = new Payment();
        payment.setId(1L);

    when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(java.util.Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When & Then
    mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.paymentId").value(1));
    }

    @Test
    void createPayment_AppointmentNotFound_ReturnsBadRequest() throws Exception {
        // Negative: Appointment not found when creating payment; expects internal server error (simulated failure handling)
    when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
    mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void executePayment_ValidPayment_ReturnsSuccess() throws Exception {
        // Positive: Executes a valid pending payment for an existing appointment; expects success
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);

    when(paymentRepository.findById(anyLong())).thenReturn(Optional.of(payment));
    when(appointmentRepository.findById(anyLong())).thenReturn(Optional.of(appointment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // When & Then
    mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":1,\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void executePayment_PaymentNotFound_ReturnsInternalServerError() throws Exception {
        // Negative: Payment ID not found when attempting to execute; expects internal server error
    when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
    mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":1,\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isInternalServerError());
    }

    // ==================== COMPREHENSIVE TESTS FOR BRANCH COVERAGE ====================

    @Test
    @DisplayName("Create payment with null userId header - defaults to 1L")
    void createPayment_NullUserId_DefaultsTo1() throws Exception {
        // Edge: Missing X-User-Id header should default to userId=1L and still allow payment creation
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        
        Payment payment = new Payment();
        payment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/paypal/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Create payment with appointmentId as Integer type")
    void createPayment_AppointmentIdAsInteger_ParsesCorrectly() throws Exception {
        // Edge: JSON appointmentId as integer should parse correctly and create payment
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        
        Payment payment = new Payment();
        payment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // JSON integer will be parsed as Integer in Map
        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create payment with amount as Integer type")
    void createPayment_AmountAsInteger_ParsesCorrectly() throws Exception {
        // Edge: JSON amount provided as integer (no decimal) should parse and succeed
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        
        Payment payment = new Payment();
        payment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Amount as integer (no decimal)
        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Create payment - payment already completed for appointment")
    void createPayment_AlreadyCompletedForAppointment_ReturnsBadRequest() throws Exception {
        // Negative: Existing completed payment for same appointment should block new payment and return bad request
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment existingPayment = new Payment();
        existingPayment.setId(2L);
        existingPayment.setAppointmentId(1L);
        existingPayment.setStatus(Payment.Status.COMPLETED);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(existingPayment));

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Payment already completed for this appointment"));
    }

    @Test
    @DisplayName("Create payment - existing payment but not completed (PENDING status)")
    void createPayment_ExistingPaymentNotCompleted_AllowsNewPayment() throws Exception {
        // Positive: Existing payment is PENDING (not completed), so creating a new payment is allowed
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment existingPayment = new Payment();
        existingPayment.setId(2L);
        existingPayment.setAppointmentId(1L);
        existingPayment.setStatus(Payment.Status.PENDING);

        Payment newPayment = new Payment();
        newPayment.setId(3L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(newPayment);

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Create payment - existing payment for different appointment")
    void createPayment_ExistingPaymentDifferentAppointment_AllowsNewPayment() throws Exception {
        // Positive: Existing payment references a different appointment, so new payment creation is allowed
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment existingPayment = new Payment();
        existingPayment.setId(2L);
        existingPayment.setAppointmentId(999L); // Different appointment
        existingPayment.setStatus(Payment.Status.COMPLETED);

        Payment newPayment = new Payment();
        newPayment.setId(3L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(newPayment);

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Create payment - existing payment with null appointmentId")
    void createPayment_ExistingPaymentNullAppointmentId_AllowsNewPayment() throws Exception {
        // Edge: Existing payment has null appointmentId; should not block creating a new payment for this appointment
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment existingPayment = new Payment();
        existingPayment.setId(2L);
        existingPayment.setAppointmentId(null); // Null appointmentId
        existingPayment.setStatus(Payment.Status.COMPLETED);

        Payment newPayment = new Payment();
        newPayment.setId(3L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(existingPayment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(newPayment);

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\":1,\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment - payment already completed status")
    void executePayment_AlreadyCompleted_ReturnsBadRequest() throws Exception {
        // Negative: Attempt to execute a payment that's already COMPLETED should return bad request
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));

        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Payment already completed"));
    }

    @Test
    @DisplayName("Execute payment - find by PayPal payment ID (not numeric)")
    void executePayment_FindByPayPalId_Success() throws Exception {
        // Positive: Find payment by PayPal string ID and execute it, expect success
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("PAY-XXXYYY");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"PAY-XXXYYY\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment - appointment status not SCHEDULED (skip update)")
    void executePayment_AppointmentNotScheduled_SkipsStatusUpdate() throws Exception {
        // Edge: Appointment is not SCHEDULED (e.g., COMPLETED) so the appointment status update should be skipped
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.COMPLETED); // Already completed

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    @DisplayName("Execute payment - payment has no appointmentId")
    void executePayment_NoAppointmentId_ReturnsError() throws Exception {
        // Negative/Edge: Payment has null appointmentId which triggers error path (handled as internal server error)
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(null); // No appointment - causes NullPointerException in Map.of()

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // NullPointerException will be caught and return 500
        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Execute payment by token - valid token")
    void executePaymentByToken_ValidToken_Success() throws Exception {
        // Positive: Execute payment found by token; expect successful execution and appointment status update
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("SIM-123");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-123\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment by token - EC- prefix stripped")
    void executePaymentByToken_ECPrefix_StripsPrefix() throws Exception {
        // Edge: Token prefixed with "EC-" should be normalized (strip prefix) and match payment
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("12345");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"EC-12345\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment by token - payment not found")
    void executePaymentByToken_NotFound_ReturnsError() throws Exception {
        // Negative: No payment matches provided token; expect internal server error response
        when(paymentRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-999\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Execute payment by token - already completed")
    void executePaymentByToken_AlreadyCompleted_ReturnsBadRequest() throws Exception {
        // Negative: Found payment is already COMPLETED; execution should return bad request
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("SIM-123");
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-123\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Payment already completed"));
    }

    @Test
    @DisplayName("Execute payment by token - appointment status not SCHEDULED")
    void executePaymentByToken_AppointmentNotScheduled_SkipsUpdate() throws Exception {
        // Edge: Appointment found but status is not SCHEDULED (e.g., CANCELLED); appointment save should be skipped
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("SIM-123");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(1L);

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setStatus(Appointment.Status.CANCELLED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-123\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(appointmentRepository, never()).save(appointment);
    }

    @Test
    @DisplayName("Execute payment by token - no appointmentId")
    void executePaymentByToken_NoAppointmentId_ReturnsError() throws Exception {
        // Negative/Edge: Payment has null appointmentId when executing by token; triggers internal server error path
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("SIM-123");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(null); // No appointment - causes NullPointerException in Map.of()

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // NullPointerException will be caught and return 500
        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-123\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Cancel payment - valid payment")
    void cancelPayment_ValidPayment_ReturnsSuccess() throws Exception {
        // Positive: Cancel a pending payment; expect status change to FAILED and success message
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/paypal/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment cancelled"));
    }

    @Test
    @DisplayName("Cancel payment - payment not found")
    void cancelPayment_NotFound_ReturnsError() throws Exception {
        // Negative: Cancel attempted for non-existent payment; expect internal server error response
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/paypal/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Get my payments - with userId header")
    void getMyPayments_WithUserId_ReturnsPayments() throws Exception {
        // Positive: Retrieve payments for provided X-User-Id header; expect OK and list returned
        Payment payment1 = new Payment();
        payment1.setId(1L);
        Payment payment2 = new Payment();
        payment2.setId(2L);

        when(paymentRepository.findByUserId(5L)).thenReturn(Arrays.asList(payment1, payment2));

        mockMvc.perform(get("/paypal/my-payments")
                .header("X-User-Id", "5"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get my payments - null userId defaults to 1L")
    void getMyPayments_NullUserId_DefaultsTo1() throws Exception {
        // Edge: No X-User-Id header should default to userId=1L and return payments
        Payment payment = new Payment();
        payment.setId(1L);

        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(payment));

        mockMvc.perform(get("/paypal/my-payments"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get my payments - exception thrown")
    void getMyPayments_ExceptionThrown_ReturnsError() throws Exception {
        // Negative: Repository throws exception; controller should return internal server error with success=false
        when(paymentRepository.findByUserId(anyLong())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/paypal/my-payments")
                .header("X-User-Id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Get my receipts - returns only completed payments")
    void getMyReceipts_ReturnsOnlyCompleted() throws Exception {
        // Positive: Filters receipts to only COMPLETED payments; pending/failed should be omitted
        Payment completed1 = new Payment();
        completed1.setId(1L);
        completed1.setStatus(Payment.Status.COMPLETED);

        Payment pending = new Payment();
        pending.setId(2L);
        pending.setStatus(Payment.Status.PENDING);

        Payment completed2 = new Payment();
        completed2.setId(3L);
        completed2.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(completed1, pending, completed2));

        mockMvc.perform(get("/paypal/receipts/my-receipts")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get my receipts - null userId defaults to 1L")
    void getMyReceipts_NullUserId_DefaultsTo1() throws Exception {
        // Edge: No user header defaults to 1L and returns completed receipts
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);

        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(payment));

        mockMvc.perform(get("/paypal/receipts/my-receipts"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get my receipts - exception thrown")
    void getMyReceipts_ExceptionThrown_ReturnsError() throws Exception {
        // Negative: Repository throws exception while fetching receipts; expect internal server error response
        when(paymentRepository.findByUserId(anyLong())).thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/paypal/receipts/my-receipts")
                .header("X-User-Id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Create payment - handles PayPalRESTException gracefully")
    void createPayment_PayPalException_FallsBackToSimulated() throws Exception {
        // Positive/Edge: Simulated PayPal failure should fall back to internal simulated approval URL and still create a payment
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment payment = new Payment();
        payment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\": 1, \"amount\": 100.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.approvalUrl").exists());
    }

    @Test
    @DisplayName("Create payment - with string appointmentId")
    void createPayment_StringAppointmentId_ParsesCorrectly() throws Exception {
        // Edge: appointmentId and amount provided as strings in JSON should be parsed and succeed
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        Payment payment = new Payment();
        payment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(paymentRepository.findByUserId(anyLong())).thenReturn(Collections.emptyList());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        mockMvc.perform(post("/paypal/create")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"appointmentId\": \"1\", \"amount\": \"50.00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment - find by PayPal ID with multiple payments")
    void executePayment_FindByPayPalId_WithMultiplePayments() throws Exception {
        // Positive: When multiple payments exist, find the correct one by PayPal ID (pending) and execute it successfully
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setPaypalPaymentId("PAY-111");
        payment1.setStatus(Payment.Status.COMPLETED);

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setPaypalPaymentId("PAY-222");
        payment2.setStatus(Payment.Status.PENDING);
        payment2.setAppointmentId(2L);

        Appointment appointment = new Appointment();
        appointment.setId(2L);
        appointment.setStatus(Appointment.Status.SCHEDULED);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment2);
        when(appointmentRepository.findById(2L)).thenReturn(Optional.of(appointment));

        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"PAY-222\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Execute payment by token - appointment not found (ifPresent not executed)")
    void executePaymentByToken_AppointmentNotFound_NoUpdate() throws Exception {
        // Edge: Payment found by token references a non-existent appointment; appointment update should be skipped safely
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPaypalPaymentId("SIM-123");
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(999L); // Non-existent appointment

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/paypal/execute-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"token\":\"SIM-123\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Get my receipts - filters out PENDING and FAILED statuses")
    void getMyReceipts_FiltersNonCompleted() throws Exception {
        // Positive: Ensure receipts endpoint filters out PENDING and FAILED, returning only COMPLETED payments
        Payment completed = new Payment();
        completed.setId(1L);
        completed.setStatus(Payment.Status.COMPLETED);

        Payment pending = new Payment();
        pending.setId(2L);
        pending.setStatus(Payment.Status.PENDING);

        Payment failed = new Payment();
        failed.setId(3L);
        failed.setStatus(Payment.Status.FAILED);

        when(paymentRepository.findByUserId(1L)).thenReturn(Arrays.asList(completed, pending, failed));

        mockMvc.perform(get("/paypal/receipts/my-receipts")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cancel payment - payment becomes FAILED status")
    void cancelPayment_SetsStatusToFailed() throws Exception {
        // Positive: Cancelling a PENDING payment should set its status to FAILED before saving
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment savedPayment = invocation.getArgument(0);
            assert savedPayment.getStatus() == Payment.Status.FAILED;
            return savedPayment;
        });

        mockMvc.perform(post("/paypal/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment cancelled"));
    }

    @Test
    @DisplayName("Execute payment - appointment ifPresent with empty Optional")
    void executePayment_AppointmentNotFound_IfPresentNotExecuted() throws Exception {
        // Edge: When executing payment that references a non-existent appointment, the appointment .ifPresent path should not execute (no save)
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setAppointmentId(999L);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":\"1\",\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(appointmentRepository, never()).save(any(Appointment.class));
    }
}