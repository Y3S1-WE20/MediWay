package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimpleMedicalRecordControllerTest):
 * - Get all medical records - Success                 : Positive
 * - Get medical record by ID - Success / Not Found   : Positive / Negative
 * - Create medical record - Success                  : Positive
 * - Update medical record - Success / Not Found      : Positive / Negative
 * - Delete medical record - Success / Not Found      : Positive / Negative
 * - Get medical records by patient/doctor ID         : Positive
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.repository.MedicalRecordRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("SimpleMedicalRecordController Tests")
class SimpleMedicalRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @InjectMocks
    private SimpleMedicalRecordController medicalRecordController;

    private ObjectMapper objectMapper;
    private MedicalRecord testMedicalRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
        objectMapper = new ObjectMapper();
        
        // Setup test medical record
        testMedicalRecord = new MedicalRecord();
        testMedicalRecord.setId(1L);
        testMedicalRecord.setPatientId(1L);
        testMedicalRecord.setDoctorId(1L);
        testMedicalRecord.setDiagnosis("Test diagnosis");
        testMedicalRecord.setTreatment("Test treatment");
        testMedicalRecord.setPrescription("Test prescription");
    }

    // Positive: Get all medical records - Success
    @Test
    @DisplayName("Get all medical records - Success")
    void getAllMedicalRecords_Success() throws Exception {
        // Given
        when(medicalRecordRepository.findAll()).thenReturn(Arrays.asList(testMedicalRecord));

        // When & Then
        mockMvc.perform(get("/medical-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis").value("Test diagnosis"))
                .andExpect(jsonPath("$[0].treatment").value("Test treatment"));

        verify(medicalRecordRepository).findAll();
    }

    // Positive: Get medical record by ID - Success
    @Test
    @DisplayName("Get medical record by ID - Success")
    void getMedicalRecordById_Success() throws Exception {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testMedicalRecord));

        // When & Then
        mockMvc.perform(get("/medical-records/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diagnosis").value("Test diagnosis"))
                .andExpect(jsonPath("$.treatment").value("Test treatment"));

        verify(medicalRecordRepository).findById(1L);
    }

    // Negative: Get medical record by ID - Not Found
    @Test
    @DisplayName("Get medical record by ID - Not Found")
    void getMedicalRecordById_NotFound() throws Exception {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/medical-records/1"))
                .andExpect(status().isNotFound());

        verify(medicalRecordRepository).findById(1L);
    }

    // Positive: Create medical record - Success
    @Test
    @DisplayName("Create medical record - Success")
    void createMedicalRecord_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "patientId": 1,
                "doctorId": 1,
                "diagnosis": "Test diagnosis",
                "treatment": "Test treatment",
                "prescription": "Test prescription"
            }
            """;

        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testMedicalRecord);

        // When & Then
        mockMvc.perform(post("/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.record.diagnosis").value("Test diagnosis"));

        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    // Positive: Update medical record - Success
    @Test
    @DisplayName("Update medical record - Success")
    void updateMedicalRecord_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "diagnosis": "Updated diagnosis",
                "treatment": "Updated treatment",
                "prescription": "Updated prescription"
            }
            """;

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testMedicalRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testMedicalRecord);

        // When & Then
        mockMvc.perform(put("/medical-records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(medicalRecordRepository).findById(1L);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    // Negative: Update medical record - Not Found
    @Test
    @DisplayName("Update medical record - Not Found")
    void updateMedicalRecord_NotFound() throws Exception {
        // Given
        String requestBody = """
            {
                "diagnosis": "Updated diagnosis",
                "treatment": "Updated treatment"
            }
            """;

        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/medical-records/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(medicalRecordRepository).findById(1L);
        verify(medicalRecordRepository, never()).save(any());
    }

    // Positive: Delete medical record - Success
    @Test
    @DisplayName("Delete medical record - Success")
    void deleteMedicalRecord_Success() throws Exception {
        // Given
        when(medicalRecordRepository.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/medical-records/1"))
                .andExpect(status().isOk());

        verify(medicalRecordRepository).existsById(1L);
        verify(medicalRecordRepository).deleteById(1L);
    }

    // Negative: Delete medical record - Not Found
    @Test
    @DisplayName("Delete medical record - Not Found")
    void deleteMedicalRecord_NotFound() throws Exception {
        // Given
        when(medicalRecordRepository.existsById(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/medical-records/1"))
                .andExpect(status().isNotFound());

        verify(medicalRecordRepository).existsById(1L);
        verify(medicalRecordRepository, never()).deleteById(any());
    }

    // Positive: Get medical records by patient ID
    @Test
    @DisplayName("Get medical records by patient ID - Success")
    void getMedicalRecordsByPatientId_Success() throws Exception {
        // Given
    List<Map<String, Object>> mockResult = new ArrayList<>();
    Map<String, Object> recordMap = new HashMap<>();
    recordMap.put("id", 1);
    recordMap.put("diagnosis", "Test diagnosis");
    mockResult.add(recordMap);

    when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(anyLong())).thenReturn((List)mockResult);

        // When & Then
        mockMvc.perform(get("/medical-records/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].diagnosis").value("Test diagnosis"));

        verify(medicalRecordRepository).findByPatientIdOrderByRecordDateDesc(1L);
    }

    // Positive: Get medical records by doctor ID
    @Test
    @DisplayName("Get medical records by doctor ID - Success")
    void getMedicalRecordsByDoctorId_Success() throws Exception {
        // Given
        List<Map<String, Object>> mockResult = new ArrayList<>();
        Map<String, Object> recordMap = new HashMap<>();
        recordMap.put("id", 1);
        recordMap.put("diagnosis", "Test diagnosis");
        recordMap.put("treatment", "Test treatment");
        mockResult.add(recordMap);

        when(medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(anyLong())).thenReturn((List)mockResult);

        // When & Then
        mockMvc.perform(get("/medical-records/doctor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].treatment").value("Test treatment"));

        verify(medicalRecordRepository).findByDoctorIdOrderByRecordDateDesc(1L);
    }
}