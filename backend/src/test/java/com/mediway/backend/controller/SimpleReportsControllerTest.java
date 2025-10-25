package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimpleReportsControllerTest):
 * - Get dashboard stats - Success                     : Positive
 * - Appointments by department                        : Positive
 * - Monthly revenue                                   : Positive
 * - Daily appointments trend                          : Positive
 * - Top paying patients                               : Positive
 * - Revenue by doctor                                 : Positive
 * - Pending payments over time                        : Edge/Positive
 * Note: Many additional scenarios present; file focuses on analytics/reporting correctness and edge cases around empty datasets.
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Simple Reports Controller Tests")
class SimpleReportsControllerTest {

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

    @InjectMocks
    private SimpleReportsController reportsController;

    private Doctor testDoctor;
    private Appointment testAppointment;
    private Payment testPayment;

    @BeforeEach
    void setUp() {
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setSpecialization("Cardiology");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(1L);
        testAppointment.setDoctorId(1L);
        testAppointment.setAppointmentDate(LocalDateTime.now());
        testAppointment.setStatus(Appointment.Status.SCHEDULED);

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setUserId(1L);
        testPayment.setAppointmentId(1L);
        testPayment.setAmount(new BigDecimal("100.00"));
        testPayment.setStatus(Payment.Status.COMPLETED);
        testPayment.setPaymentDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Get dashboard stats - Success")
    void testGetDashboardStats() {
        // Setup
        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);

        List<Payment> pendingPayments = Arrays.asList(new Payment());
        List<Payment> completedPayments = Arrays.asList(testPayment);
        List<Payment> failedPayments = Arrays.asList();

        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(pendingPayments);
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(completedPayments);
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(failedPayments);

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(testPayment));

        // Execute
        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> stats = response.getBody();
        assertEquals(10L, stats.get("patientsCount"));
        assertEquals(5L, stats.get("doctorsCount"));
        assertEquals(20L, stats.get("appointmentsCount"));

        @SuppressWarnings("unchecked")
        Map<String, Long> paymentsByStatus = (Map<String, Long>) stats.get("paymentsByStatus");
        assertEquals(1L, paymentsByStatus.get("pending"));
        assertEquals(1L, paymentsByStatus.get("completed"));
        assertEquals(0L, paymentsByStatus.get("failed"));

        verify(userRepository).count();
        verify(doctorRepository).count();
        verify(appointmentRepository).count();
    }

    @Test
    @DisplayName("Get dashboard stats - With appointments by department")
    void testGetDashboardStats_AppointmentsByDepartment() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setSpecialization("Neurology");

        Appointment appt2 = new Appointment();
        appt2.setId(2L);
        appt2.setDoctorId(2L);
        appt2.setAppointmentDate(LocalDateTime.now());

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment, appt2));
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor, doctor2));
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("appointmentsByDepartment"));
    }

    @Test
    @DisplayName("Get dashboard stats - With monthly revenue")
    void testGetDashboardStats_MonthlyRevenue() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment payment1 = new Payment();
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setPaymentDate(LocalDateTime.now());
        payment1.setAmount(new BigDecimal("150.00"));

        Payment payment2 = new Payment();
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setPaymentDate(LocalDateTime.now().minusMonths(1));
        payment2.setAmount(new BigDecimal("200.00"));

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("monthlyRevenue"));
    }

    @Test
    @DisplayName("Get dashboard stats - With daily appointments trend")
    void testGetDashboardStats_DailyAppointments() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        Appointment todayAppt = new Appointment();
        todayAppt.setAppointmentDate(LocalDateTime.now());
        todayAppt.setDoctorId(1L);

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(todayAppt));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("dailyAppointments"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> dailyAppts = (List<Map<String, Object>>) response.getBody().get("dailyAppointments");
        assertEquals(30, dailyAppts.size()); // Last 30 days
    }

    @Test
    @DisplayName("Get dashboard stats - With top paying patients")
    void testGetDashboardStats_TopPayingPatients() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment payment1 = new Payment();
        payment1.setUserId(1L);
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setAmount(new BigDecimal("300.00"));
        payment1.setPaymentDate(LocalDateTime.now());

        Payment payment2 = new Payment();
        payment2.setUserId(1L);
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setPaymentDate(LocalDateTime.now());

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment1, payment2));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("topPayingPatients"));
    }

    @Test
    @DisplayName("Get dashboard stats - With revenue by doctor")
    void testGetDashboardStats_RevenueByDoctor() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Appointment appt = new Appointment();
        appt.setId(1L);
        appt.setDoctorId(1L);
        appt.setAppointmentDate(LocalDateTime.now());

        Payment payment = new Payment();
        payment.setAppointmentId(1L);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(new BigDecimal("250.00"));
        payment.setPaymentDate(LocalDateTime.now());

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt));
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("revenueByDoctor"));
    }

    @Test
    @DisplayName("Get dashboard stats - With pending payments over time")
    void testGetDashboardStats_PendingPaymentsOverTime() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment pendingPayment = new Payment();
        pendingPayment.setStatus(Payment.Status.PENDING);
        pendingPayment.setPaymentDate(LocalDateTime.now());

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(pendingPayment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("pendingPaymentsOverTime"));
    }

    @Test
    @DisplayName("Download CSV summary - Success")
    void testDownloadCsvSummary() {
        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(30L);

        List<Appointment> scheduledAppts = Arrays.asList(new Appointment(), new Appointment());
        List<Appointment> completedAppts = Arrays.asList(new Appointment());
        List<Appointment> cancelledAppts = Arrays.asList();

        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(scheduledAppts);
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(completedAppts);
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(cancelledAppts);

        List<Payment> pendingPayments = Arrays.asList(new Payment(), new Payment());
        List<Payment> completedPayments = Arrays.asList(testPayment);
        List<Payment> failedPayments = Arrays.asList();

        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(pendingPayments);
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(completedPayments);
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(failedPayments);

        ResponseEntity<String> response = reportsController.downloadCsvSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("Type,Count"));
        assertTrue(response.getBody().contains("Patients,10"));
        assertTrue(response.getBody().contains("Doctors,5"));
        assertEquals("text/csv", response.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=report_summary.csv", response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    @DisplayName("Download CSV summary - With all appointment statuses")
    void testDownloadCsvSummary_AllStatuses() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(3L);
        when(appointmentRepository.count()).thenReturn(10L);
        when(medicalRecordRepository.count()).thenReturn(8L);
        when(paymentRepository.count()).thenReturn(12L);

        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(Arrays.asList(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(Arrays.asList(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(Arrays.asList(new Appointment()));

        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(Arrays.asList(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(Arrays.asList(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(Arrays.asList(new Payment()));

        ResponseEntity<String> response = reportsController.downloadCsvSummary();

        String csv = response.getBody();
        assertNotNull(csv);
        assertTrue(csv.contains("AppointmentsScheduled,1"));
        assertTrue(csv.contains("AppointmentsCompleted,1"));
        assertTrue(csv.contains("AppointmentsCancelled,1"));
        assertTrue(csv.contains("PaymentsPending,1"));
        assertTrue(csv.contains("PaymentsCompleted,1"));
        assertTrue(csv.contains("PaymentsFailed,1"));
    }

    @Test
    @DisplayName("Download PDF summary - Success")
    void testDownloadPdfSummary() {
        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(30L);

        when(appointmentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("application/pdf", response.getHeaders().getFirst("Content-Type"));
        assertEquals("attachment; filename=report_summary.pdf", response.getHeaders().getFirst("Content-Disposition"));
    }

    @Test
    @DisplayName("Get dashboard stats - Empty data")
    void testGetDashboardStats_EmptyData() {
        when(userRepository.count()).thenReturn(0L);
        when(doctorRepository.count()).thenReturn(0L);
        when(appointmentRepository.count()).thenReturn(0L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> stats = response.getBody();
        assertEquals(0L, stats.get("patientsCount"));
        assertEquals(0L, stats.get("doctorsCount"));
        assertEquals(0L, stats.get("appointmentsCount"));
    }

    @Test
    @DisplayName("Get dashboard stats - Null payment amounts handled")
    void testGetDashboardStats_NullPaymentAmounts() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment nullAmountPayment = new Payment();
        nullAmountPayment.setStatus(Payment.Status.COMPLETED);
        nullAmountPayment.setPaymentDate(LocalDateTime.now());
        nullAmountPayment.setAmount(null); // Null amount

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(nullAmountPayment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get dashboard stats - Payment without date is skipped for monthly revenue")
    void testGetDashboardStats_PaymentWithoutDate() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment noDatePayment = new Payment();
        noDatePayment.setStatus(Payment.Status.COMPLETED);
        noDatePayment.setPaymentDate(null); // Null date
        noDatePayment.setAmount(new BigDecimal("100.00"));

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(noDatePayment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("monthlyRevenue"));
    }

    @Test
    @DisplayName("Get dashboard stats - Appointment without date is skipped")
    void testGetDashboardStats_AppointmentWithoutDate() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        Appointment noDateAppt = new Appointment();
        noDateAppt.setId(1L);
        noDateAppt.setDoctorId(1L);
        noDateAppt.setAppointmentDate(null); // Null date

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(noDateAppt));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("dailyAppointments"));
    }

    @Test
    @DisplayName("Get dashboard stats - Appointment with unknown doctor specialization")
    void testGetDashboardStats_UnknownSpecialization() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(paymentRepository.findAll()).thenReturn(Arrays.asList());

        // Appointment with doctorId not in doctor list
        Appointment unknownDocAppt = new Appointment();
        unknownDocAppt.setId(1L);
        unknownDocAppt.setDoctorId(999L); // Doctor not in list
        unknownDocAppt.setAppointmentDate(LocalDateTime.now());

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(unknownDocAppt));
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor)); // Only doctorId=1

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("appointmentsByDepartment"));
    }

    @Test
    @DisplayName("Get dashboard stats - Payment not completed is skipped for revenue")
    void testGetDashboardStats_NonCompletedPaymentSkipped() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment pendingPayment = new Payment();
        pendingPayment.setStatus(Payment.Status.PENDING); // Not COMPLETED
        pendingPayment.setPaymentDate(LocalDateTime.now());
        pendingPayment.setAmount(new BigDecimal("100.00"));
        pendingPayment.setUserId(1L);

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(pendingPayment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> monthlyRev = (List<Map<String, Object>>) response.getBody().get("monthlyRevenue");
        // Monthly revenue should be empty since no completed payments
        assertNotNull(monthlyRev);
    }

    @Test
    @DisplayName("Get dashboard stats - Appointment with null doctorId is skipped for revenue by doctor")
    void testGetDashboardStats_AppointmentWithNullDoctorId() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Appointment nullDocAppt = new Appointment();
        nullDocAppt.setId(1L);
        nullDocAppt.setDoctorId(null); // Null doctor ID
        nullDocAppt.setAppointmentDate(LocalDateTime.now());

        Payment payment = new Payment();
        payment.setAppointmentId(1L);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentDate(LocalDateTime.now());

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(nullDocAppt));
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("revenueByDoctor"));
    }

    @Test
    @DisplayName("Get dashboard stats - Payment with null appointmentId is skipped for doctor revenue")
    void testGetDashboardStats_PaymentWithNullAppointmentId() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Appointment appt = new Appointment();
        appt.setId(1L);
        appt.setDoctorId(1L);
        appt.setAppointmentDate(LocalDateTime.now());

        Payment payment = new Payment();
        payment.setAppointmentId(null); // Null appointment ID
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(new BigDecimal("100.00"));
        payment.setPaymentDate(LocalDateTime.now());

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(appt));
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(payment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get dashboard stats - Pending payment without date is skipped")
    void testGetDashboardStats_PendingPaymentWithoutDate() {
        when(userRepository.count()).thenReturn(5L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);

        when(paymentRepository.findByStatus(any())).thenReturn(Arrays.asList());
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList());
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        Payment pendingPayment = new Payment();
        pendingPayment.setStatus(Payment.Status.PENDING);
        pendingPayment.setPaymentDate(null); // Null date
        pendingPayment.setAmount(new BigDecimal("100.00"));

        when(paymentRepository.findAll()).thenReturn(Arrays.asList(pendingPayment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("pendingPaymentsOverTime"));
    }

    @Test
    @DisplayName("Get all medical records - Success")
    void testGetAllMedicalRecords() {
        MedicalRecord record1 = new MedicalRecord();
        record1.setId(1L);
        record1.setDiagnosis("Test");

        when(medicalRecordRepository.findAll()).thenReturn(Arrays.asList(record1));

        ResponseEntity<List<MedicalRecord>> response = reportsController.getAllMedicalRecords();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(medicalRecordRepository).findAll();
    }

    @Test
    @DisplayName("Get patient medical records - Success")
    void testGetPatientMedicalRecords() {
        MedicalRecord record1 = new MedicalRecord();
        record1.setId(1L);
        record1.setPatientId(1L);

        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(1L))
                .thenReturn(Arrays.asList(record1));

        ResponseEntity<List<MedicalRecord>> response = reportsController.getPatientMedicalRecords(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(medicalRecordRepository).findByPatientIdOrderByRecordDateDesc(1L);
    }

    @Test
    @DisplayName("Get all appointments - Success")
    void testGetAllAppointments() {
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));

        ResponseEntity<List<Appointment>> response = reportsController.getAllAppointments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(appointmentRepository).findAll();
    }

    @Test
    @DisplayName("Get all payments - Success")
    void testGetAllPayments() {
        when(paymentRepository.findAll()).thenReturn(Arrays.asList(testPayment));

        ResponseEntity<List<Payment>> response = reportsController.getAllPayments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(paymentRepository).findAll();
    }

    @Test
    @DisplayName("Download PDF summary - With exception handling")
    void testDownloadPdfSummary_Exception() {
        // Force an exception by not properly mocking dependencies
        when(userRepository.count()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Download PDF summary - With monthly revenue data")
    void testDownloadPdfSummary_WithMonthlyRevenue() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setAmount(new BigDecimal("150.00"));
        payment1.setPaymentDate(LocalDateTime.of(2024, 1, 15, 10, 0));

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setPaymentDate(LocalDateTime.of(2024, 1, 20, 11, 0));

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(8L);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(payment1, payment2));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("Download PDF summary - With appointments by department")
    void testDownloadPdfSummary_WithAppointmentsByDepartment() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setSpecialization("Cardiology");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctorId(1L);
        appointment.setAppointmentDate(LocalDateTime.now());

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(8L);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("Download PDF summary - With daily appointments")
    void testDownloadPdfSummary_WithDailyAppointments() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setAppointmentDate(LocalDateTime.now());

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(8L);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(doctorRepository.findAll()).thenReturn(List.of());

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("Download PDF summary - With pending payments over time")
    void testDownloadPdfSummary_WithPendingPaymentsOverTime() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.PENDING);
        payment.setPaymentDate(LocalDateTime.now());

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(8L);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of(payment));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(new Payment()));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment));
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("Download PDF summary - With empty revenue data")
    void testDownloadPdfSummary_WithEmptyRevenueData() {
        when(userRepository.count()).thenReturn(0L);
        when(doctorRepository.count()).thenReturn(0L);
        when(appointmentRepository.count()).thenReturn(0L);
        when(medicalRecordRepository.count()).thenReturn(0L);
        when(paymentRepository.count()).thenReturn(0L);
        when(appointmentRepository.findByStatus(any())).thenReturn(List.of());
        when(paymentRepository.findByStatus(any())).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    @DisplayName("Get dashboard stats - With multiple revenue entries")
    void testGetDashboardStats_MultipleRevenueEntries() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setAmount(new BigDecimal("100.00"));
        payment1.setPaymentDate(LocalDateTime.of(2024, 1, 15, 10, 0));

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setAmount(new BigDecimal("200.00"));
        payment2.setPaymentDate(LocalDateTime.of(2024, 2, 20, 11, 0));

        Payment payment3 = new Payment();
        payment3.setId(3L);
        payment3.setStatus(Payment.Status.COMPLETED);
        payment3.setAmount(new BigDecimal("150.00"));
        payment3.setPaymentDate(LocalDateTime.of(2024, 1, 25, 12, 0));

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(payment1, payment2, payment3));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2, payment3));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> monthlyRevenue = (List<Map<String, Object>>) body.get("monthlyRevenue");
        assertNotNull(monthlyRevenue);
        assertEquals(2, monthlyRevenue.size());
        
        // Verify revenue for January (payment1 + payment3)
        Map<String, Object> jan = monthlyRevenue.stream()
            .filter(m -> "2024-01".equals(m.get("month")))
            .findFirst()
            .orElse(null);
        assertNotNull(jan);
        assertEquals(new BigDecimal("250.00"), jan.get("revenue"));
    }

    @Test
    @DisplayName("Get dashboard stats - With zero amount payment")
    void testGetDashboardStats_ZeroAmountPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(BigDecimal.ZERO);
        payment.setPaymentDate(LocalDateTime.now());

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(payment));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Get dashboard stats - With multiple specializations")
    void testGetDashboardStats_MultipleSpecializations() {
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setSpecialization("Cardiology");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setSpecialization("Neurology");

        Appointment appt1 = new Appointment();
        appt1.setId(1L);
        appt1.setDoctorId(1L);

        Appointment appt2 = new Appointment();
        appt2.setId(2L);
        appt2.setDoctorId(2L);

        Appointment appt3 = new Appointment();
        appt3.setId(3L);
        appt3.setDoctorId(1L);

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(3L);
        when(paymentRepository.findByStatus(any())).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of(appt1, appt2, appt3));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
        when(paymentRepository.findAll()).thenReturn(List.of());

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> apptByDept = (List<Map<String, Object>>) body.get("appointmentsByDepartment");
        assertNotNull(apptByDept);
        assertEquals(2, apptByDept.size());
    }

    @Test
    @DisplayName("Get dashboard stats - Revenue by doctor with multiple doctors")
    void testGetDashboardStats_RevenueByDoctorMultiple() {
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);

        Appointment appt1 = new Appointment();
        appt1.setId(100L);
        appt1.setDoctorId(1L);

        Appointment appt2 = new Appointment();
        appt2.setId(101L);
        appt2.setDoctorId(2L);

        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setAppointmentId(100L);
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setAmount(new BigDecimal("150.00"));

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setAppointmentId(101L);
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setAmount(new BigDecimal("200.00"));

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(2L);
        when(appointmentRepository.count()).thenReturn(2L);
        when(paymentRepository.findByStatus(any())).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of(appt1, appt2));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor1, doctor2));
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> revenueByDoctor = (List<Map<String, Object>>) body.get("revenueByDoctor");
        assertNotNull(revenueByDoctor);
        assertEquals(2, revenueByDoctor.size());
    }

    @Test
    @DisplayName("Get dashboard stats - Top paying patients with multiple patients")
    void testGetDashboardStats_TopPayingPatientsMultiple() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setUserId(1L);
        payment1.setStatus(Payment.Status.COMPLETED);
        payment1.setAmount(new BigDecimal("100.00"));

        Payment payment2 = new Payment();
        payment2.setId(2L);
        payment2.setUserId(2L);
        payment2.setStatus(Payment.Status.COMPLETED);
        payment2.setAmount(new BigDecimal("300.00"));

        Payment payment3 = new Payment();
        payment3.setId(3L);
        payment3.setUserId(1L);
        payment3.setStatus(Payment.Status.COMPLETED);
        payment3.setAmount(new BigDecimal("150.00"));

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(payment1, payment2, payment3));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(appointmentRepository.findAll()).thenReturn(List.of());
        when(doctorRepository.findAll()).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment1, payment2, payment3));

        ResponseEntity<Map<String, Object>> response = reportsController.getDashboardStats();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> topPayingPatients = (List<Map<String, Object>>) body.get("topPayingPatients");
        assertNotNull(topPayingPatients);
        assertEquals(2, topPayingPatients.size());
        
        // Verify patient 2 is first (highest total)
        assertEquals(2L, topPayingPatients.get(0).get("patientId"));
        assertEquals(300.0, topPayingPatients.get(0).get("totalPaid"));
        
        // Verify patient 1 is second (payment1 + payment3 = 250)
        assertEquals(1L, topPayingPatients.get(1).get("patientId"));
        assertEquals(250.0, topPayingPatients.get(1).get("totalPaid"));
    }

    @Test
    @DisplayName("Download PDF summary - With all data populated")
    void testDownloadPdfSummary_AllDataPopulated() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setSpecialization("Cardiology");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctorId(1L);
        appointment.setAppointmentDate(LocalDateTime.now());

        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Payment.Status.COMPLETED);
        payment.setAmount(new BigDecimal("150.00"));
        payment.setPaymentDate(LocalDateTime.now());

        Payment pendingPayment = new Payment();
        pendingPayment.setId(2L);
        pendingPayment.setStatus(Payment.Status.PENDING);
        pendingPayment.setPaymentDate(LocalDateTime.now());

        when(userRepository.count()).thenReturn(10L);
        when(doctorRepository.count()).thenReturn(5L);
        when(appointmentRepository.count()).thenReturn(20L);
        when(medicalRecordRepository.count()).thenReturn(15L);
        when(paymentRepository.count()).thenReturn(8L);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(List.of(new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.COMPLETED)).thenReturn(List.of(new Appointment(), new Appointment()));
        when(appointmentRepository.findByStatus(Appointment.Status.CANCELLED)).thenReturn(List.of());
        when(paymentRepository.findByStatus(Payment.Status.PENDING)).thenReturn(List.of(pendingPayment));
        when(paymentRepository.findByStatus(Payment.Status.COMPLETED)).thenReturn(List.of(payment));
        when(paymentRepository.findByStatus(Payment.Status.FAILED)).thenReturn(List.of());
        when(paymentRepository.findAll()).thenReturn(List.of(payment, pendingPayment));
        when(appointmentRepository.findAll()).thenReturn(List.of(appointment));
        when(doctorRepository.findAll()).thenReturn(List.of(doctor));

        ResponseEntity<byte[]> response = reportsController.downloadPdfSummary();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
        assertEquals("attachment; filename=report_summary.pdf", response.getHeaders().getFirst("Content-Disposition"));
        assertEquals("application/pdf", response.getHeaders().getFirst("Content-Type"));
    }
}