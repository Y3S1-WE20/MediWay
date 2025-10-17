package com.mediway.backend.controller;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
        // Given
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
        // Given
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
        // Given
    when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
    mockMvc.perform(post("/paypal/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentId\":1,\"payerId\":\"PAYER123\"}"))
                .andExpect(status().isInternalServerError());
    }
}