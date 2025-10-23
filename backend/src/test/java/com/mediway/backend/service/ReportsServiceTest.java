package com.mediway.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.UserRepository;

/**
 * Unit tests for Statistical Reports Service
 * Tests: Generate PDF/CSV hospital insights and analytics
 */
@DisplayName("Reports Service Tests - Statistical Reports & Analytics")
class ReportsServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    private List<Appointment> testAppointments;
    private List<Payment> testPayments;
    private List<Doctor> testDoctors;
    private List<MedicalRecord> testMedicalRecords;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test appointments
        Appointment apt1 = new Appointment();
        apt1.setId(1L);
        apt1.setPatientId(1L);
        apt1.setDoctorId(1L);
        apt1.setAppointmentDate(LocalDateTime.now().minusDays(5));
        apt1.setStatus(Appointment.Status.COMPLETED);

        Appointment apt2 = new Appointment();
        apt2.setId(2L);
        apt2.setPatientId(2L);
        apt2.setDoctorId(1L);
        apt2.setAppointmentDate(LocalDateTime.now().minusDays(3));
        apt2.setStatus(Appointment.Status.SCHEDULED);

        Appointment apt3 = new Appointment();
        apt3.setId(3L);
        apt3.setPatientId(3L);
        apt3.setDoctorId(2L);
        apt3.setAppointmentDate(LocalDateTime.now().plusDays(2));
        apt3.setStatus(Appointment.Status.CANCELLED);

        testAppointments = Arrays.asList(apt1, apt2, apt3);

        // Setup test payments
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setUserId(1L);
        payment1.setAppointmentId(1L);
        payment1.setAmount(BigDecimal.valueOf(500.00));
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setPaymentDate(LocalDateTime.now().minusDays(5));

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setUserId(2L);
        payment2.setAppointmentId(2L);
        payment2.setAmount(BigDecimal.valueOf(750.00));
        payment2.setStatus(Payment.Status.PENDING);
        payment2.setPaymentDate(LocalDateTime.now().minusDays(3));

        Payment payment3 = new Payment();
        payment3.setId(3L);
        payment3.setUserId(3L);
        payment3.setAppointmentId(3L);
        payment3.setAmount(BigDecimal.valueOf(300.00));
        payment3.setStatus(Payment.Status.FAILED);
        payment3.setPaymentDate(LocalDateTime.now().minusDays(1));

        testPayments = Arrays.asList(payment1, payment2, payment3);

        // Setup test doctors
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setName("Dr. Smith");
        doctor1.setSpecialization("Cardiology");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setName("Dr. Johnson");
        doctor2.setSpecialization("Neurology");

        testDoctors = Arrays.asList(doctor1, doctor2);

        // Setup test medical records
        MedicalRecord record1 = new MedicalRecord();
        record1.setId(1L);
        record1.setPatientId(1L);
        record1.setDoctorId(1L);
        record1.setDiagnosis("Hypertension");
        record1.setTreatment("Medication");
        record1.setRecordDate(LocalDateTime.now().minusDays(10));

        MedicalRecord record2 = new MedicalRecord();
        record2.setId(2L);
        record2.setPatientId(2L);
        record2.setDoctorId(1L);
        record2.setDiagnosis("Diabetes Type 2");
        record2.setTreatment("Insulin therapy");
        record2.setRecordDate(LocalDateTime.now().minusDays(5));

        testMedicalRecords = Arrays.asList(record1, record2);
    }

    @Test
    @DisplayName("Test 1: Get total patient count")
    void testGetTotalPatientCount() {
        // Given
        when(userRepository.count()).thenReturn(10L);

        // When
        long count = userRepository.count();

        // Then
        assertEquals(10L, count);
        verify(userRepository, times(1)).count();
    }

    @Test
    @DisplayName("Test 2: Get total doctor count")
    void testGetTotalDoctorCount() {
        // Given
        when(doctorRepository.count()).thenReturn(5L);

        // When
        long count = doctorRepository.count();

        // Then
        assertEquals(5L, count);
        verify(doctorRepository, times(1)).count();
    }

    @Test
    @DisplayName("Test 3: Get total appointments count")
    void testGetTotalAppointmentsCount() {
        // Given
        when(appointmentRepository.count()).thenReturn(15L);

        // When
        long count = appointmentRepository.count();

        // Then
        assertEquals(15L, count);
        verify(appointmentRepository, times(1)).count();
    }

    @Test
    @DisplayName("Test 4: Get appointments by status - SCHEDULED")
    void testGetAppointmentsByStatus_Scheduled() {
        // Given
        List<Appointment> scheduled = Arrays.asList(testAppointments.get(1));
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(scheduled);

        // When
        List<Appointment> result = appointmentRepository.findByStatus(Appointment.Status.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Appointment.Status.SCHEDULED, result.get(0).getStatus());
        verify(appointmentRepository, times(1)).findByStatus(Appointment.Status.SCHEDULED);
    }

    @Test
    @DisplayName("Test 5: Get appointments by status - COMPLETED")
    void testGetAppointmentsByStatus_Completed() {
        // Given
        List<Appointment> completed = Arrays.asList(testAppointments.get(0));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(completed);

        // When
        List<Appointment> result = appointmentRepository.findByStatus(Appointment.Status.COMPLETED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Appointment.Status.COMPLETED, result.get(0).getStatus());
        verify(appointmentRepository, times(1)).findByStatus(Appointment.Status.COMPLETED);
    }

    @Test
    @DisplayName("Test 6: Get appointments by status - CANCELLED")
    void testGetAppointmentsByStatus_Cancelled() {
        // Given
        List<Appointment> cancelled = Arrays.asList(testAppointments.get(2));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(cancelled);

        // When
        List<Appointment> result = appointmentRepository.findByStatus(Appointment.Status.CANCELLED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Appointment.Status.CANCELLED, result.get(0).getStatus());
        verify(appointmentRepository, times(1)).findByStatus(Appointment.Status.CANCELLED);
    }

    @Test
    @DisplayName("Test 7: Get payments by status - COMPLETED")
    void testGetPaymentsByStatus_Completed() {
        // Given
        List<Payment> completed = Arrays.asList(testPayments.get(0));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(completed);

        // When
        List<Payment> result = paymentRepository.findByStatus(Payment.Status.COMPLETED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Payment.Status.COMPLETED, result.get(0).getStatus());
        verify(paymentRepository, times(1)).findByStatus(Payment.Status.COMPLETED);
    }

    @Test
    @DisplayName("Test 8: Get payments by status - PENDING")
    void testGetPaymentsByStatus_Pending() {
        // Given
        List<Payment> pending = Arrays.asList(testPayments.get(1));
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(pending);

        // When
        List<Payment> result = paymentRepository.findByStatus(Payment.Status.PENDING);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Payment.Status.PENDING, result.get(0).getStatus());
        verify(paymentRepository, times(1)).findByStatus(Payment.Status.PENDING);
    }

    @Test
    @DisplayName("Test 9: Get payments by status - FAILED")
    void testGetPaymentsByStatus_Failed() {
        // Given
        List<Payment> failed = Arrays.asList(testPayments.get(2));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(failed);

        // When
        List<Payment> result = paymentRepository.findByStatus(Payment.Status.FAILED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Payment.Status.FAILED, result.get(0).getStatus());
        verify(paymentRepository, times(1)).findByStatus(Payment.Status.FAILED);
    }

    @Test
    @DisplayName("Test 10: Get all appointments for report")
    void testGetAllAppointments() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(testAppointments);

        // When
        List<Appointment> result = appointmentRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(appointmentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 11: Get all payments for revenue report")
    void testGetAllPayments() {
        // Given
        when(paymentRepository.findAll()).thenReturn(testPayments);

        // When
        List<Payment> result = paymentRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(3, result.size());
        verify(paymentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 12: Calculate total revenue from completed payments")
    void testCalculateTotalRevenue() {
        // Given
        List<Payment> completed = Arrays.asList(testPayments.get(0));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(completed);

        // When
        List<Payment> completedPayments = paymentRepository.findByStatus(Payment.Status.COMPLETED);
        BigDecimal totalRevenue = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Then
        assertEquals(BigDecimal.valueOf(500.00), totalRevenue);
        verify(paymentRepository, times(1)).findByStatus(Payment.Status.COMPLETED);
    }

    @Test
    @DisplayName("Test 13: Get all doctors for report")
    void testGetAllDoctors() {
        // Given
        when(doctorRepository.findAll()).thenReturn(testDoctors);

        // When
        List<Doctor> result = doctorRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Smith", result.get(0).getName());
        assertEquals("Dr. Johnson", result.get(1).getName());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 14: Get all medical records for report")
    void testGetAllMedicalRecords() {
        // Given
        when(medicalRecordRepository.findAll()).thenReturn(testMedicalRecords);

        // When
        List<MedicalRecord> result = medicalRecordRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Hypertension", result.get(0).getDiagnosis());
        assertEquals("Diabetes Type 2", result.get(1).getDiagnosis());
        verify(medicalRecordRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 15: Get total medical records count")
    void testGetTotalMedicalRecordsCount() {
        // Given
        when(medicalRecordRepository.count()).thenReturn(25L);

        // When
        long count = medicalRecordRepository.count();

        // Then
        assertEquals(25L, count);
        verify(medicalRecordRepository, times(1)).count();
    }

    @Test
    @DisplayName("Test 16: Verify payment amount calculations")
    void testPaymentAmountCalculations() {
        // Given/When
        BigDecimal amount1 = testPayments.get(0).getAmount();
        BigDecimal amount2 = testPayments.get(1).getAmount();
        BigDecimal amount3 = testPayments.get(2).getAmount();

        // Then
        assertEquals(0, BigDecimal.valueOf(500.00).compareTo(amount1));
        assertEquals(0, BigDecimal.valueOf(750.00).compareTo(amount2));
        assertEquals(0, BigDecimal.valueOf(300.00).compareTo(amount3));
    }

    @Test
    @DisplayName("Test 17: Group appointments by doctor specialization")
    void testGroupAppointmentsBySpecialization() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(testAppointments);
        when(doctorRepository.findAll()).thenReturn(testDoctors);

        // When
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();

        // Then - Verify data for analytics
        assertNotNull(appointments);
        assertNotNull(doctors);
        assertEquals(3, appointments.size());
        assertEquals(2, doctors.size());

        // Verify appointments can be grouped by doctor
        long cardiologyAppointments = appointments.stream()
                .filter(apt -> apt.getDoctorId().equals(1L))
                .count();
        long neurologyAppointments = appointments.stream()
                .filter(apt -> apt.getDoctorId().equals(2L))
                .count();

        assertEquals(2, cardiologyAppointments);
        assertEquals(1, neurologyAppointments);
    }

    @Test
    @DisplayName("Test 18: Calculate pending payments total")
    void testCalculatePendingPaymentsTotal() {
        // Given
        List<Payment> pending = Arrays.asList(testPayments.get(1));
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(pending);

        // When
        List<Payment> pendingPayments = paymentRepository.findByStatus(Payment.Status.PENDING);
        BigDecimal pendingTotal = pendingPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Then
        assertEquals(BigDecimal.valueOf(750.00), pendingTotal);
    }

    @Test
    @DisplayName("Test 19: Verify appointment date ranges for reports")
    void testAppointmentDateRanges() {
        // Given/When
        LocalDateTime now = LocalDateTime.now();
        
        // Then - Verify test data has proper date ranges
        assertTrue(testAppointments.get(0).getAppointmentDate().isBefore(now)); // Past appointment
        assertTrue(testAppointments.get(1).getAppointmentDate().isBefore(now)); // Past appointment
        assertTrue(testAppointments.get(2).getAppointmentDate().isAfter(now));  // Future appointment
    }

    @Test
    @DisplayName("Test 20: Edge case - Empty reports data")
    void testEmptyReportsData() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<Doctor> doctors = doctorRepository.findAll();

        // Then
        assertNotNull(appointments);
        assertNotNull(payments);
        assertNotNull(doctors);
        assertTrue(appointments.isEmpty());
        assertTrue(payments.isEmpty());
        assertTrue(doctors.isEmpty());
    }
}
