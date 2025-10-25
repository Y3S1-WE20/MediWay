package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimpleAppointmentControllerTest):
 * - Get all appointments - Success                   : Positive
 * - Get all appointments - Empty list                : Edge
 * - Get my appointments (with/without header)        : Positive / Edge
 * - Filter my appointments by userId                : Positive
 * - Error handling for get my appointments           : Negative
 * - Get appointment by ID - Success / Not Found      : Positive / Negative
 * - Create appointment - Success (with/without user) : Positive
 * - Create appointment variations (optional fields)  : Edge
 * - Update appointment - Success / Not Found         : Positive / Negative
 * - Delete appointment - Success / Not Found         : Positive / Negative
 * - appointmentToMap variations                      : Edge (mapping with/without details)
 */

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimpleAppointmentController Tests")
class SimpleAppointmentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SimpleAppointmentController appointmentController;

    private ObjectMapper objectMapper;
    private Appointment testAppointment;
    private Doctor testDoctor;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(appointmentController).build();
        objectMapper = new ObjectMapper();
        
        // Setup test doctor
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Test Doctor");
        testDoctor.setSpecialization("Cardiology");

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        // Setup test appointment
        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(1L);
        testAppointment.setDoctorId(1L);
        testAppointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(Appointment.Status.SCHEDULED);
        testAppointment.setNotes("Test appointment");
    }

    // Positive: Get all appointments - Success
    @Test
    @DisplayName("Get all appointments - Success")
    void getAllAppointments_Success() throws Exception {
        // Given
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].appointmentId").value(1))
                .andExpect(jsonPath("$[0].status").value("SCHEDULED"))
                .andExpect(jsonPath("$[0].doctorName").value("Dr. Test Doctor"));

        verify(appointmentRepository).findAll();
    }

    // Edge: Get all appointments - Empty list
    @Test
    @DisplayName("Get all appointments - Empty list")
    void getAllAppointments_EmptyList() throws Exception {
        // Given
        when(appointmentRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(appointmentRepository).findAll();
    }

    // Positive: Get my appointments - With userId header
    @Test
    @DisplayName("Get my appointments - With userId header")
    void getMyAppointments_WithUserId() throws Exception {
        // Given
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/appointments/my")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].appointmentId").value(1))
                .andExpect(jsonPath("$[0].patientId").value(1));

        verify(appointmentRepository).findAll();
    }

    // Edge: Get my appointments - Without userId header
    @Test
    @DisplayName("Get my appointments - Without userId header")
    void getMyAppointments_WithoutUserId() throws Exception {
        // Given
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/appointments/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].appointmentId").value(1));

        verify(appointmentRepository).findAll();
    }

    // Positive: Get my appointments - Filter by userId
    @Test
    @DisplayName("Get my appointments - Filter by userId")
    void getMyAppointments_FilterByUserId() throws Exception {
        // Given
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setPatientId(2L);
        appointment2.setDoctorId(1L);
        appointment2.setAppointmentDate(LocalDateTime.now().plusDays(2));
        appointment2.setStatus(Appointment.Status.SCHEDULED);

        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment, appointment2));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then - Only return appointment with patientId = 1
        mockMvc.perform(get("/appointments/my")
                .header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].appointmentId").value(1));

        verify(appointmentRepository).findAll();
    }

    // Negative: Get my appointments - Error handling
    @Test
    @DisplayName("Get my appointments - Error handling")
    void getMyAppointments_Error() throws Exception {
        // Given
        when(appointmentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/appointments/my")
                .header("X-User-Id", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Error fetching appointments: Database error"));

        verify(appointmentRepository).findAll();
    }

    // Positive: Get appointment by ID - Success
    @Test
    @DisplayName("Get appointment by ID - Success")
    void getAppointmentById_Success() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andExpect(jsonPath("$.doctorName").value("Dr. Test Doctor"));

        verify(appointmentRepository).findById(1L);
    }

    // Negative: Get appointment by ID - Not Found
    @Test
    @DisplayName("Get appointment by ID - Not Found")
    void getAppointmentById_NotFound() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isNotFound());

        verify(appointmentRepository).findById(1L);
    }

    // Positive: Create appointment - Success with header
    @Test
    @DisplayName("Create appointment - Success with header")
    void createAppointment_SuccessWithHeader() throws Exception {
        // Given
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("doctorId", "1");
        requestMap.put("appointmentDate", "2025-10-25");
        requestMap.put("appointmentTime", "10:00");
        requestMap.put("reason", "Regular checkup");

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/appointments")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.status").value("SCHEDULED"));

        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Positive: Create appointment - Success without userId
    @Test
    @DisplayName("Create appointment - Success without userId (defaults to 1)")
    void createAppointment_SuccessWithoutUserId() throws Exception {
        // Given
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("doctorId", "1");
        requestMap.put("appointmentDate", "2025-10-25");
        requestMap.put("appointmentTime", "10:00");
        requestMap.put("reason", "Regular checkup");

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));

        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Edge: Create appointment - Without reason
    @Test
    @DisplayName("Create appointment - Without reason (optional field)")
    void createAppointment_WithoutReason() throws Exception {
        // Given
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("doctorId", "1");
        requestMap.put("appointmentDate", "2025-10-25");
        requestMap.put("appointmentTime", "10:00");
        // No reason field

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(post("/appointments")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));

        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Negative: Create appointment - Error handling
    @Test
    @DisplayName("Create appointment - Error handling")
    void createAppointment_Error() throws Exception {
        // Given
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("doctorId", "invalid");
        requestMap.put("appointmentDate", "2025-10-25");
        requestMap.put("appointmentTime", "10:00");

        // When & Then
        mockMvc.perform(post("/appointments")
                .header("X-User-Id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestMap)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").exists());

        verify(appointmentRepository, never()).save(any());
    }

    // Positive: Update appointment - Success
    @Test
    @DisplayName("Update appointment - Success")
    void updateAppointment_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "appointmentDate": "2025-10-26T10:00:00",
                "notes": "Updated appointment"
            }
            """;

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(put("/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1));

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    // Negative: Update appointment - Not Found
    @Test
    @DisplayName("Update appointment - Not Found")
    void updateAppointment_NotFound() throws Exception {
        // Given
        String requestBody = """
            {
                "appointmentDate": "2025-10-26T10:00:00",
                "notes": "Updated appointment"
            }
            """;

        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository, never()).save(any());
    }

    // Positive: Delete appointment - Success
    @Test
    @DisplayName("Delete appointment - Success")
    void deleteAppointment_Success() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When & Then
        mockMvc.perform(delete("/appointments/1"))
                .andExpect(status().isOk());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).delete(testAppointment);
    }

    // Negative: Delete appointment - Not Found
    @Test
    @DisplayName("Delete appointment - Not Found")
    void deleteAppointment_NotFound() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(delete("/appointments/1"))
                .andExpect(status().isNotFound());

        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository, never()).delete(any());
    }

    // Edge: Appointment to map - With doctor and patient details
    @Test
    @DisplayName("Appointment to map - With doctor and patient details")
    void appointmentToMap_WithDoctorAndPatient() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        
        User patient = new User();
        patient.setId(1L);
        patient.setName("John Patient");
        patient.setEmail("patient@example.com");
        patient.setPhone("1234567890");
        when(userRepository.findById(1L)).thenReturn(Optional.of(patient));

        // When & Then
        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.doctorId").value(1))
                .andExpect(jsonPath("$.doctorName").value("Dr. Test Doctor"))
                .andExpect(jsonPath("$.doctorSpecialization").value("Cardiology"))
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.patientName").value("John Patient"))
                .andExpect(jsonPath("$.patientEmail").value("patient@example.com"))
                .andExpect(jsonPath("$.patientPhone").value("1234567890"))
                .andExpect(jsonPath("$.consultationFee").value(500.00))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.isPaid").value(false));

        verify(doctorRepository).findById(1L);
        verify(userRepository).findById(1L);
    }

    // Edge: Appointment to map - Without doctor details
    @Test
    @DisplayName("Appointment to map - Without doctor details")
    void appointmentToMap_WithoutDoctor() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.doctorName").doesNotExist());

        verify(doctorRepository).findById(1L);
    }

    // Edge: Appointment to map - Without patient details
    @Test
    @DisplayName("Appointment to map - Without patient details")
    void appointmentToMap_WithoutPatient() throws Exception {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.appointmentId").value(1))
                .andExpect(jsonPath("$.patientName").doesNotExist());

        verify(userRepository).findById(1L);
    }
}