package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (PatientControllerTest / PatientControllerFixedTest):
 * - Get all patients - Success                     : Positive
 * - Get patient by ID - Success                    : Positive
 * - Get patient by ID - Not Found                  : Negative
 * - Create patient - Success                       : Positive
 * - Update patient - Success                       : Positive
 * - Update patient - Not Found                     : Negative
 * - Delete patient - Success                       : Positive
 * - Delete patient - Not Found                     : Negative
 * - Search patients by name - Success              : Positive
 * - Get patients by gender - Success               : Positive
 */

import java.time.LocalDate;
import java.util.Arrays;
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
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.service.PatientService;

@ExtendWith(MockitoExtension.class)
@DisplayName("PatientController Tests")
class PatientControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PatientService patientService;

    @InjectMocks
    private PatientController patientController;

    private ObjectMapper objectMapper;
    private User testPatient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();
        
        // Setup test patient (User with PATIENT role)
        testPatient = new User();
        testPatient.setId(1L);
        testPatient.setName("John Doe");
        testPatient.setEmail("john.doe@example.com");
        testPatient.setPhone("1234567890");
        testPatient.setAddress("123 Main St");
        testPatient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testPatient.setGender("Male");
        testPatient.setRole(User.Role.PATIENT);
    }

    @Test
    @DisplayName("Get all patients - Success")
    void getAllPatients_Success() throws Exception {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testPatient));

        // When & Then
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John Doe"))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get patient by ID - Success")
    void getPatientById_Success() throws Exception {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testPatient));

        // When & Then
        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Get patient by ID - Not Found")
    void getPatientById_NotFound() throws Exception {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Create patient - Success")
    void createPatient_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "John Doe",
                "email": "john.doe@example.com",
                "phone": "1234567890",
                "address": "123 Main St",
                "dateOfBirth": "1990-01-01",
                "gender": "Male"
            }
            """;

        when(userRepository.save(any(User.class))).thenReturn(testPatient);

        // When & Then
        mockMvc.perform(post("/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update patient - Success")
    void updatePatient_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "John Doe Updated",
                "email": "john.updated@example.com",
                "phone": "0987654321",
                "address": "456 Oak St",
                "gender": "Male"
            }
            """;

        when(userRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(userRepository.save(any(User.class))).thenReturn(testPatient);

        // When & Then
        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Update patient - Not Found")
    void updatePatient_NotFound() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "John Doe Updated",
                "email": "john.updated@example.com"
            }
            """;

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete patient - Success")
    void deletePatient_Success() throws Exception {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isOk());

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete patient - Not Found")
    void deletePatient_NotFound() throws Exception {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/patients/1"))
                .andExpect(status().isNotFound());

        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Search patients by name - Success")
    void searchPatientsByName_Success() throws Exception {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testPatient));

        // When & Then
        mockMvc.perform(get("/patients/search")
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Get patients by gender - Success")
    void getPatientsByGender_Success() throws Exception {
        // Given
        when(userRepository.findAll()).thenReturn(Arrays.asList(testPatient));

        // When & Then
        mockMvc.perform(get("/patients/gender/Male"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].gender").value("Male"));

        verify(userRepository).findAll();
    }
}