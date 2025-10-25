package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (ReceiptControllerTest):
 * - generate receipt for completed payment            : Positive
 * - return existing receipt if already generated      : Edge (idempotent)
 * - return 400 when payment not found                 : Negative
 * - return 403 when payment does not belong to user   : Negative (forbidden)
 * - return 400 when payment not completed             : Negative
 * - get receipt by payment ID - Success               : Positive
 * - get receipt by payment ID - Not Found             : Negative
 * - get all receipts for user - Success               : Positive
 * - default userId when null                          : Edge
 * - error handling for generate/get/getMyReceipts     : Negative (exception paths)
 * - additional tests: download PDF, get by number etc : Mix (Positive/Negative/Edge)
 */

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.entity.Receipt;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.ReceiptRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Receipt Controller Tests")
class ReceiptControllerTest {

    @Mock
    private ReceiptRepository receiptRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private ReceiptController receiptController;

    private Payment testPayment;
    private Receipt testReceipt;
    private User testUser;
    private Appointment testAppointment;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhone("1234567890");

        // Setup test doctor
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setSpecialization("Cardiology");

        // Setup test appointment
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(1L);
        testAppointment.setDoctorId(1L);
        testAppointment.setStatus(Appointment.Status.COMPLETED);

        // Setup test payment
        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setUserId(1L);
        testPayment.setAppointmentId(1L);
        testPayment.setAmount(new BigDecimal("100.00"));
        testPayment.setStatus(Payment.Status.COMPLETED);
        testPayment.setPaymentDate(LocalDateTime.now());

        // Setup test receipt
        testReceipt = new Receipt();
        testReceipt.setId(1L);
        testReceipt.setPaymentId(1L);
        testReceipt.setUserId(1L);
        testReceipt.setAppointmentId(1L);
        testReceipt.setAmount(new BigDecimal("100.00"));
        testReceipt.setIssueDate(LocalDateTime.now());
        testReceipt.setPatientName("John Doe");
    }

    @Test
    @DisplayName("Should generate receipt for completed payment")
    void testGenerateReceipt_Success() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(testReceipt);

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    @Test
    @DisplayName("Should return existing receipt if already generated")
    void testGenerateReceipt_AlreadyExists() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(true);
        when(receiptRepository.findByPaymentId(1L)).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(receiptRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return 400 when payment not found")
    void testGenerateReceipt_PaymentNotFound() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when payment does not belong to user")
    void testGenerateReceipt_Forbidden() {
        testPayment.setUserId(2L); // Different user
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when payment not completed")
    void testGenerateReceipt_PaymentNotCompleted() {
        testPayment.setStatus(Payment.Status.PENDING);
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get receipt by payment ID")
    void testGetReceiptByPayment_Success() {
        when(receiptRepository.findByPaymentId(1L)).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByPayment(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when receipt not found by payment")
    void testGetReceiptByPayment_NotFound() {
        when(receiptRepository.findByPaymentId(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = receiptController.getReceiptByPayment(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when accessing another user's receipt by payment")
    void testGetReceiptByPayment_Forbidden() {
        testReceipt.setUserId(2L); // Different user's receipt
        when(receiptRepository.findByPaymentId(1L)).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByPayment(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get all receipts for user")
    void testGetMyReceipts_Success() {
        List<Receipt> receipts = Arrays.asList(testReceipt);
        when(receiptRepository.findByUserIdOrderByIssueDateDesc(1L)).thenReturn(receipts);

        ResponseEntity<?> response = receiptController.getMyReceipts(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId when null")
    void testGenerateReceipt_DefaultUserId() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(testReceipt);

        ResponseEntity<?> response = receiptController.generateReceipt(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in generateReceipt")
    void testGenerateReceipt_Exception() {
        when(receiptRepository.existsByPaymentId(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in getReceiptByPayment")
    void testGetReceiptByPayment_Exception() {
        when(receiptRepository.findByPaymentId(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = receiptController.getReceiptByPayment(1L, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in getMyReceipts")
    void testGetMyReceipts_Exception() {
        when(receiptRepository.findByUserIdOrderByIssueDateDesc(1L))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = receiptController.getMyReceipts(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when user not found during receipt generation")
    void testGenerateReceipt_UserNotFound() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findById(1L)).thenReturn(Optional.empty()); // User not found

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 400 when appointment not found during receipt generation")
    void testGenerateReceipt_AppointmentNotFound() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty()); // Appointment not found

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle appointment with null doctorId")
    void testGenerateReceipt_NullDoctorId() {
        testAppointment.setDoctorId(null); // Null doctor ID
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(receiptRepository.save(any(Receipt.class))).thenReturn(testReceipt);

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    @Test
    @DisplayName("Should handle doctor not found in database")
    void testGenerateReceipt_DoctorNotFound() {
        when(receiptRepository.existsByPaymentId(1L)).thenReturn(false);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(testPayment));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty()); // Doctor not found
        when(receiptRepository.save(any(Receipt.class))).thenReturn(testReceipt);

        ResponseEntity<?> response = receiptController.generateReceipt(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(receiptRepository, times(1)).save(any(Receipt.class));
    }

    @Test
    @DisplayName("Should get receipt by receipt number")
    void testGetReceiptByNumber_Success() {
        testReceipt.setReceiptNumber("REC-12345");
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByNumber("REC-12345", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when receipt not found by number")
    void testGetReceiptByNumber_NotFound() {
        when(receiptRepository.findByReceiptNumber("INVALID")).thenReturn(Optional.empty());

        ResponseEntity<?> response = receiptController.getReceiptByNumber("INVALID", 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when accessing another user's receipt by number")
    void testGetReceiptByNumber_Forbidden() {
        testReceipt.setUserId(2L); // Different user's receipt
        testReceipt.setReceiptNumber("REC-12345");
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByNumber("REC-12345", 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in getReceiptByNumber")
    void testGetReceiptByNumber_Exception() {
        when(receiptRepository.findByReceiptNumber(any())).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = receiptController.getReceiptByNumber("REC-12345", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should download PDF receipt successfully")
    void testDownloadReceiptPDF_Success() {
        testReceipt.setReceiptNumber("REC-12345");
        testReceipt.setPatientEmail("test@example.com");
        testReceipt.setDoctorName("Dr. Smith");
        testReceipt.setServiceDescription("Consultation");
        testReceipt.setPaymentMethod("PayPal");
        testReceipt.setTransactionId("TXN-123");
        testReceipt.setQrCode("QR-DATA");
        
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("REC-12345", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 404 when downloading PDF for non-existent receipt")
    void testDownloadReceiptPDF_NotFound() {
        when(receiptRepository.findByReceiptNumber("INVALID")).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("INVALID", 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should return 403 when downloading another user's PDF")
    void testDownloadReceiptPDF_Forbidden() {
        testReceipt.setUserId(2L); // Different user
        testReceipt.setReceiptNumber("REC-12345");
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("REC-12345", 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId when null in getReceiptByPayment")
    void testGetReceiptByPayment_DefaultUserId() {
        when(receiptRepository.findByPaymentId(1L)).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByPayment(1L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId when null in getReceiptByNumber")
    void testGetReceiptByNumber_DefaultUserId() {
        testReceipt.setReceiptNumber("REC-12345");
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<?> response = receiptController.getReceiptByNumber("REC-12345", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId when null in getMyReceipts")
    void testGetMyReceipts_DefaultUserId() {
        List<Receipt> receipts = Arrays.asList(testReceipt);
        when(receiptRepository.findByUserIdOrderByIssueDateDesc(1L)).thenReturn(receipts);

        ResponseEntity<?> response = receiptController.getMyReceipts(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId when null in downloadReceiptPDF")
    void testDownloadReceiptPDF_DefaultUserId() {
        testReceipt.setReceiptNumber("REC-12345");
        testReceipt.setPatientEmail("test@example.com");
        testReceipt.setDoctorName("Dr. Smith");
        testReceipt.setServiceDescription("Consultation");
        testReceipt.setPaymentMethod("PayPal");
        testReceipt.setTransactionId("TXN-123");
        testReceipt.setQrCode("QR-DATA");
        
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("REC-12345", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should generate PDF without QR code when null")
    void testDownloadReceiptPDF_NullQrCode() {
        testReceipt.setReceiptNumber("REC-12345");
        testReceipt.setPatientEmail("test@example.com");
        testReceipt.setDoctorName("Dr. Smith");
        testReceipt.setServiceDescription("Consultation");
        testReceipt.setPaymentMethod("PayPal");
        testReceipt.setTransactionId("TXN-123");
        testReceipt.setQrCode(null); // No QR code
        
        when(receiptRepository.findByReceiptNumber("REC-12345")).thenReturn(Optional.of(testReceipt));

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("REC-12345", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception during PDF generation")
    void testDownloadReceiptPDF_Exception() {
        when(receiptRepository.findByReceiptNumber("REC-12345"))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<byte[]> response = receiptController.downloadReceiptPDF("REC-12345", 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
