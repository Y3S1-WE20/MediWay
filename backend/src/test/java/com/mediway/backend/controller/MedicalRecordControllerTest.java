package com.mediway.backend.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import com.mediway.backend.dto.request.MedicalRecordRequest;
import com.mediway.backend.dto.response.MedicalRecordResponse;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.service.MedicalRecordService;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordController Tests")
class MedicalRecordControllerTest {
    
    @Mock
    private MedicalRecordService medicalRecordService;
    
    @InjectMocks
    private MedicalRecordController medicalRecordController;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private MedicalRecordRequest request;
    private MedicalRecordResponse response;
    private UUID recordId;
    private UUID patientId;
    private UUID doctorId;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(medicalRecordController).build();
        objectMapper = new ObjectMapper();
        
        recordId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        
        // Create test request
        request = new MedicalRecordRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        request.setMedications("Lisinopril 10mg daily");
        request.setNotes("Regular monitoring needed");
        
        // Create test response
        response = new MedicalRecordResponse();
        response.setRecordId(recordId);
        response.setPatientId(patientId);
        response.setPatientName("John Doe");
        response.setDoctorId(doctorId);
        response.setDoctorName("Dr. Jane Smith");
        response.setDiagnosis("Hypertension");
        response.setMedications("Lisinopril 10mg daily");
        response.setNotes("Regular monitoring needed");
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("Should create medical record successfully")
    void shouldCreateMedicalRecordSuccessfully() throws Exception {
        // Arrange
        when(medicalRecordService.createMedicalRecord(any(MedicalRecordRequest.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(post("/api/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(recordId.toString()))
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.patientName").value("John Doe"))
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.doctorName").value("Dr. Jane Smith"))
                .andExpect(jsonPath("$.diagnosis").value("Hypertension"))
                .andExpect(jsonPath("$.medications").value("Lisinopril 10mg daily"))
                .andExpect(jsonPath("$.notes").value("Regular monitoring needed"));
        
        verify(medicalRecordService).createMedicalRecord(any(MedicalRecordRequest.class));
    }
    
    @Test
    @DisplayName("Should return bad request for invalid request body")
    void shouldReturnBadRequestForInvalidRequestBody() throws Exception {
        // Arrange
        MedicalRecordRequest invalidRequest = new MedicalRecordRequest();
        invalidRequest.setPatientId(null); // Invalid: null patient ID
        invalidRequest.setDoctorId(doctorId);
        invalidRequest.setDiagnosis(""); // Invalid: empty diagnosis
        
        // Act & Assert
        mockMvc.perform(post("/api/medical-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
        
        verify(medicalRecordService, never()).createMedicalRecord(any());
    }
    
    @Test
    @DisplayName("Should update medical record successfully")
    void shouldUpdateMedicalRecordSuccessfully() throws Exception {
        // Arrange
        request.setDiagnosis("Updated Hypertension");
        request.setMedications("Updated Lisinopril 20mg");
        request.setNotes("Updated monitoring plan");
        
        response.setDiagnosis("Updated Hypertension");
        response.setMedications("Updated Lisinopril 20mg");
        response.setNotes("Updated monitoring plan");
        
        when(medicalRecordService.updateMedicalRecord(eq(recordId), any(MedicalRecordRequest.class))).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(put("/api/medical-records/{recordId}", recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(recordId.toString()))
                .andExpect(jsonPath("$.diagnosis").value("Updated Hypertension"))
                .andExpect(jsonPath("$.medications").value("Updated Lisinopril 20mg"))
                .andExpect(jsonPath("$.notes").value("Updated monitoring plan"));
        
        verify(medicalRecordService).updateMedicalRecord(eq(recordId), any(MedicalRecordRequest.class));
    }
    
    @Test
    @DisplayName("Should return not found when updating non-existent medical record")
    void shouldReturnNotFoundWhenUpdatingNonExistentMedicalRecord() throws Exception {
        // Arrange
        when(medicalRecordService.updateMedicalRecord(eq(recordId), any(MedicalRecordRequest.class)))
                .thenThrow(new ResourceNotFoundException("Medical record not found with id: " + recordId));
        
        // Act & Assert
        mockMvc.perform(put("/api/medical-records/{recordId}", recordId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        
        verify(medicalRecordService).updateMedicalRecord(eq(recordId), any(MedicalRecordRequest.class));
    }
    
    @Test
    @DisplayName("Should get medical record by ID successfully")
    void shouldGetMedicalRecordByIdSuccessfully() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordById(recordId)).thenReturn(response);
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/{recordId}", recordId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recordId").value(recordId.toString()))
                .andExpect(jsonPath("$.patientId").value(patientId.toString()))
                .andExpect(jsonPath("$.patientName").value("John Doe"))
                .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$.doctorName").value("Dr. Jane Smith"))
                .andExpect(jsonPath("$.diagnosis").value("Hypertension"));
        
        verify(medicalRecordService).getMedicalRecordById(recordId);
    }
    
    @Test
    @DisplayName("Should return not found when getting non-existent medical record")
    void shouldReturnNotFoundWhenGettingNonExistentMedicalRecord() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordById(recordId))
                .thenThrow(new ResourceNotFoundException("Medical record not found with id: " + recordId));
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/{recordId}", recordId))
                .andExpect(status().isNotFound());
        
        verify(medicalRecordService).getMedicalRecordById(recordId);
    }
    
    @Test
    @DisplayName("Should get medical records by patient ID successfully")
    void shouldGetMedicalRecordsByPatientIdSuccessfully() throws Exception {
        // Arrange
        List<MedicalRecordResponse> responses = Arrays.asList(response);
        when(medicalRecordService.getMedicalRecordsByPatientId(patientId)).thenReturn(responses);
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].recordId").value(recordId.toString()))
                .andExpect(jsonPath("$[0].patientId").value(patientId.toString()))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"))
                .andExpect(jsonPath("$[0].diagnosis").value("Hypertension"));
        
        verify(medicalRecordService).getMedicalRecordsByPatientId(patientId);
    }
    
    @Test
    @DisplayName("Should return empty list when patient has no medical records")
    void shouldReturnEmptyListWhenPatientHasNoMedicalRecords() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordsByPatientId(patientId)).thenReturn(Arrays.asList());
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/patient/{patientId}", patientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(medicalRecordService).getMedicalRecordsByPatientId(patientId);
    }
    
    @Test
    @DisplayName("Should return not found when patient does not exist")
    void shouldReturnNotFoundWhenPatientDoesNotExist() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordsByPatientId(patientId))
                .thenThrow(new ResourceNotFoundException("Patient not found with id: " + patientId));
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/patient/{patientId}", patientId))
                .andExpect(status().isNotFound());
        
        verify(medicalRecordService).getMedicalRecordsByPatientId(patientId);
    }
    
    @Test
    @DisplayName("Should get medical records by doctor ID successfully")
    void shouldGetMedicalRecordsByDoctorIdSuccessfully() throws Exception {
        // Arrange
        List<MedicalRecordResponse> responses = Arrays.asList(response);
        when(medicalRecordService.getMedicalRecordsByDoctorId(doctorId)).thenReturn(responses);
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/doctor/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].recordId").value(recordId.toString()))
                .andExpect(jsonPath("$[0].doctorId").value(doctorId.toString()))
                .andExpect(jsonPath("$[0].doctorName").value("Dr. Jane Smith"))
                .andExpect(jsonPath("$[0].diagnosis").value("Hypertension"));
        
        verify(medicalRecordService).getMedicalRecordsByDoctorId(doctorId);
    }
    
    @Test
    @DisplayName("Should return empty list when doctor has no medical records")
    void shouldReturnEmptyListWhenDoctorHasNoMedicalRecords() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordsByDoctorId(doctorId)).thenReturn(Arrays.asList());
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/doctor/{doctorId}", doctorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(medicalRecordService).getMedicalRecordsByDoctorId(doctorId);
    }
    
    @Test
    @DisplayName("Should return not found when doctor does not exist")
    void shouldReturnNotFoundWhenDoctorDoesNotExist() throws Exception {
        // Arrange
        when(medicalRecordService.getMedicalRecordsByDoctorId(doctorId))
                .thenThrow(new ResourceNotFoundException("Doctor not found with id: " + doctorId));
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/doctor/{doctorId}", doctorId))
                .andExpect(status().isNotFound());
        
        verify(medicalRecordService).getMedicalRecordsByDoctorId(doctorId);
    }
    
    @Test
    @DisplayName("Should delete medical record successfully")
    void shouldDeleteMedicalRecordSuccessfully() throws Exception {
        // Arrange
        doNothing().when(medicalRecordService).deleteMedicalRecord(recordId);
        
        // Act & Assert
        mockMvc.perform(delete("/api/medical-records/{recordId}", recordId))
                .andExpect(status().isNoContent());
        
        verify(medicalRecordService).deleteMedicalRecord(recordId);
    }
    
    @Test
    @DisplayName("Should return not found when deleting non-existent medical record")
    void shouldReturnNotFoundWhenDeletingNonExistentMedicalRecord() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Medical record not found with id: " + recordId))
                .when(medicalRecordService).deleteMedicalRecord(recordId);
        
        // Act & Assert
        mockMvc.perform(delete("/api/medical-records/{recordId}", recordId))
                .andExpect(status().isNotFound());
        
        verify(medicalRecordService).deleteMedicalRecord(recordId);
    }
    
    @Test
    @DisplayName("Should search medical records successfully")
    void shouldSearchMedicalRecordsSuccessfully() throws Exception {
        // Arrange
        String searchQuery = "Hypertension";
        List<MedicalRecordResponse> responses = Arrays.asList(response);
        when(medicalRecordService.searchMedicalRecords(searchQuery)).thenReturn(responses);
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].recordId").value(recordId.toString()))
                .andExpect(jsonPath("$[0].diagnosis").value("Hypertension"));
        
        verify(medicalRecordService).searchMedicalRecords(searchQuery);
    }
    
    @Test
    @DisplayName("Should return empty list when no medical records match search query")
    void shouldReturnEmptyListWhenNoMedicalRecordsMatchSearchQuery() throws Exception {
        // Arrange
        String searchQuery = "NonExistentCondition";
        when(medicalRecordService.searchMedicalRecords(searchQuery)).thenReturn(Arrays.asList());
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(medicalRecordService).searchMedicalRecords(searchQuery);
    }
    
    @Test
    @DisplayName("Should handle search with empty query")
    void shouldHandleSearchWithEmptyQuery() throws Exception {
        // Arrange
        String searchQuery = "";
        when(medicalRecordService.searchMedicalRecords(searchQuery)).thenReturn(Arrays.asList());
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(medicalRecordService).searchMedicalRecords(searchQuery);
    }
    
    @Test
    @DisplayName("Should handle search with special characters")
    void shouldHandleSearchWithSpecialCharacters() throws Exception {
        // Arrange
        String searchQuery = "test@#$%^&*()";
        when(medicalRecordService.searchMedicalRecords(searchQuery)).thenReturn(Arrays.asList());
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
        
        verify(medicalRecordService).searchMedicalRecords(searchQuery);
    }
    
    @Test
    @DisplayName("Should handle multiple medical records in search results")
    void shouldHandleMultipleMedicalRecordsInSearchResults() throws Exception {
        // Arrange
        MedicalRecordResponse response2 = new MedicalRecordResponse();
        response2.setRecordId(UUID.randomUUID());
        response2.setPatientId(UUID.randomUUID());
        response2.setPatientName("Jane Doe");
        response2.setDoctorId(doctorId);
        response2.setDoctorName("Dr. Jane Smith");
        response2.setDiagnosis("Diabetes");
        response2.setMedications("Metformin 500mg");
        response2.setNotes("Blood sugar monitoring");
        
        String searchQuery = "monitoring";
        List<MedicalRecordResponse> responses = Arrays.asList(response, response2);
        when(medicalRecordService.searchMedicalRecords(searchQuery)).thenReturn(responses);
        
        // Act & Assert
        mockMvc.perform(get("/api/medical-records/search")
                .param("query", searchQuery))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].recordId").value(recordId.toString()))
                .andExpect(jsonPath("$[1].recordId").value(response2.getRecordId().toString()));
        
        verify(medicalRecordService).searchMedicalRecords(searchQuery);
    }
}
