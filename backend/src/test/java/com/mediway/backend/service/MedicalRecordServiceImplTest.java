package com.mediway.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediway.backend.dto.request.MedicalRecordRequest;
import com.mediway.backend.dto.response.MedicalRecordResponse;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("MedicalRecordServiceImpl Tests")
class MedicalRecordServiceImplTest {
    
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @InjectMocks
    private MedicalRecordServiceImpl medicalRecordService;
    
    private User patient;
    private Doctor doctor;
    private MedicalRecord medicalRecord;
    private MedicalRecordRequest request;
    private UUID patientId;
    private UUID doctorId;
    private UUID recordId;
    
    @BeforeEach
    void setUp() {
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        recordId = UUID.randomUUID();
        
        // Create test patient
        patient = User.builder()
                .userId(patientId)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .passwordHash("password123")
                .phone("123-456-7890")
                .role(User.Role.PATIENT)
                .build();
        
        // Create test doctor
        doctor = Doctor.builder()
                .doctorId(doctorId)
                .name("Dr. Jane Smith")
                .specialization("Cardiology")
                .email("jane.smith@hospital.com")
                .phone("123-456-7890")
                .build();
        
        // Create test medical record
        medicalRecord = new MedicalRecord();
        medicalRecord.setRecordId(recordId);
        medicalRecord.setPatient(patient);
        medicalRecord.setDoctor(doctor);
        medicalRecord.setDiagnosis("Hypertension");
        medicalRecord.setMedications("Lisinopril 10mg daily");
        medicalRecord.setNotes("Regular monitoring needed");
        medicalRecord.setCreatedAt(LocalDateTime.now());
        medicalRecord.setUpdatedAt(LocalDateTime.now());
        
        // Create test request
        request = new MedicalRecordRequest();
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        request.setMedications("Lisinopril 10mg daily");
        request.setNotes("Regular monitoring needed");
    }
    
    @Test
    @DisplayName("Should create medical record successfully")
    void shouldCreateMedicalRecordSuccessfully() {
        // Arrange
        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        
        // Act
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);
        
        // Assert
        assertNotNull(response);
        assertEquals(recordId, response.getRecordId());
        assertEquals(patientId, response.getPatientId());
        assertEquals("John Doe", response.getPatientName());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals("Dr. Jane Smith", response.getDoctorName());
        assertEquals("Hypertension", response.getDiagnosis());
        assertEquals("Lisinopril 10mg daily", response.getMedications());
        assertEquals("Regular monitoring needed", response.getNotes());
        
        verify(userRepository).findById(patientId);
        verify(doctorRepository).findById(doctorId);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }
    
    @Test
    @DisplayName("Should throw exception when patient not found during creation")
    void shouldThrowExceptionWhenPatientNotFoundDuringCreation() {
        // Arrange
        when(userRepository.findById(patientId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.createMedicalRecord(request);
        });
        
        assertEquals("Patient not found with id: " + patientId, exception.getMessage());
        verify(userRepository).findById(patientId);
        verify(doctorRepository, never()).findById(any());
        verify(medicalRecordRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when doctor not found during creation")
    void shouldThrowExceptionWhenDoctorNotFoundDuringCreation() {
        // Arrange
        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.createMedicalRecord(request);
        });
        
        assertEquals("Doctor not found with id: " + doctorId, exception.getMessage());
        verify(userRepository).findById(patientId);
        verify(doctorRepository).findById(doctorId);
        verify(medicalRecordRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should update medical record successfully")
    void shouldUpdateMedicalRecordSuccessfully() {
        // Arrange
        request.setDiagnosis("Updated Hypertension");
        request.setMedications("Updated Lisinopril 20mg");
        request.setNotes("Updated monitoring plan");
        
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(medicalRecord));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        
        // Act
        MedicalRecordResponse response = medicalRecordService.updateMedicalRecord(recordId, request);
        
        // Assert
        assertNotNull(response);
        assertEquals(recordId, response.getRecordId());
        assertEquals("Updated Hypertension", response.getDiagnosis());
        assertEquals("Updated Lisinopril 20mg", response.getMedications());
        assertEquals("Updated monitoring plan", response.getNotes());
        
        verify(medicalRecordRepository).findById(recordId);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }
    
    @Test
    @DisplayName("Should throw exception when medical record not found during update")
    void shouldThrowExceptionWhenMedicalRecordNotFoundDuringUpdate() {
        // Arrange
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.updateMedicalRecord(recordId, request);
        });
        
        assertEquals("Medical record not found with id: " + recordId, exception.getMessage());
        verify(medicalRecordRepository).findById(recordId);
        verify(medicalRecordRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should throw exception when doctor does not own record during update")
    void shouldThrowExceptionWhenDoctorDoesNotOwnRecordDuringUpdate() {
        // Arrange
        UUID differentDoctorId = UUID.randomUUID();
        request.setDoctorId(differentDoctorId);
        
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(medicalRecord));
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.updateMedicalRecord(recordId, request);
        });
        
        assertEquals("Medical record not found or access denied", exception.getMessage());
        verify(medicalRecordRepository).findById(recordId);
        verify(medicalRecordRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should get medical record by ID successfully")
    void shouldGetMedicalRecordByIdSuccessfully() {
        // Arrange
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.of(medicalRecord));
        
        // Act
        MedicalRecordResponse response = medicalRecordService.getMedicalRecordById(recordId);
        
        // Assert
        assertNotNull(response);
        assertEquals(recordId, response.getRecordId());
        assertEquals(patientId, response.getPatientId());
        assertEquals("John Doe", response.getPatientName());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals("Dr. Jane Smith", response.getDoctorName());
        assertEquals("Hypertension", response.getDiagnosis());
        
        verify(medicalRecordRepository).findById(recordId);
    }
    
    @Test
    @DisplayName("Should throw exception when medical record not found by ID")
    void shouldThrowExceptionWhenMedicalRecordNotFoundById() {
        // Arrange
        when(medicalRecordRepository.findById(recordId)).thenReturn(Optional.empty());
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.getMedicalRecordById(recordId);
        });
        
        assertEquals("Medical record not found with id: " + recordId, exception.getMessage());
        verify(medicalRecordRepository).findById(recordId);
    }
    
    @Test
    @DisplayName("Should get medical records by patient ID successfully")
    void shouldGetMedicalRecordsByPatientIdSuccessfully() {
        // Arrange
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(userRepository.existsById(patientId)).thenReturn(true);
        when(medicalRecordRepository.findByPatientUserIdOrderByCreatedAtDesc(patientId)).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.getMedicalRecordsByPatientId(patientId);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        MedicalRecordResponse response = responses.get(0);
        assertEquals(recordId, response.getRecordId());
        assertEquals(patientId, response.getPatientId());
        assertEquals("John Doe", response.getPatientName());
        
        verify(userRepository).existsById(patientId);
        verify(medicalRecordRepository).findByPatientUserIdOrderByCreatedAtDesc(patientId);
    }
    
    @Test
    @DisplayName("Should throw exception when patient not found during get by patient ID")
    void shouldThrowExceptionWhenPatientNotFoundDuringGetByPatientId() {
        // Arrange
        when(userRepository.existsById(patientId)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.getMedicalRecordsByPatientId(patientId);
        });
        
        assertEquals("Patient not found with id: " + patientId, exception.getMessage());
        verify(userRepository).existsById(patientId);
        verify(medicalRecordRepository, never()).findByPatientUserIdOrderByCreatedAtDesc(any());
    }
    
    @Test
    @DisplayName("Should get medical records by doctor ID successfully")
    void shouldGetMedicalRecordsByDoctorIdSuccessfully() {
        // Arrange
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(doctorRepository.existsById(doctorId)).thenReturn(true);
        when(medicalRecordRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctorId)).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.getMedicalRecordsByDoctorId(doctorId);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        MedicalRecordResponse response = responses.get(0);
        assertEquals(recordId, response.getRecordId());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals("Dr. Jane Smith", response.getDoctorName());
        
        verify(doctorRepository).existsById(doctorId);
        verify(medicalRecordRepository).findByDoctorDoctorIdOrderByCreatedAtDesc(doctorId);
    }
    
    @Test
    @DisplayName("Should throw exception when doctor not found during get by doctor ID")
    void shouldThrowExceptionWhenDoctorNotFoundDuringGetByDoctorId() {
        // Arrange
        when(doctorRepository.existsById(doctorId)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.getMedicalRecordsByDoctorId(doctorId);
        });
        
        assertEquals("Doctor not found with id: " + doctorId, exception.getMessage());
        verify(doctorRepository).existsById(doctorId);
        verify(medicalRecordRepository, never()).findByDoctorDoctorIdOrderByCreatedAtDesc(any());
    }
    
    @Test
    @DisplayName("Should delete medical record successfully")
    void shouldDeleteMedicalRecordSuccessfully() {
        // Arrange
        when(medicalRecordRepository.existsById(recordId)).thenReturn(true);
        
        // Act
        medicalRecordService.deleteMedicalRecord(recordId);
        
        // Assert
        verify(medicalRecordRepository).existsById(recordId);
        verify(medicalRecordRepository).deleteById(recordId);
    }
    
    @Test
    @DisplayName("Should throw exception when medical record not found during deletion")
    void shouldThrowExceptionWhenMedicalRecordNotFoundDuringDeletion() {
        // Arrange
        when(medicalRecordRepository.existsById(recordId)).thenReturn(false);
        
        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            medicalRecordService.deleteMedicalRecord(recordId);
        });
        
        assertEquals("Medical record not found with id: " + recordId, exception.getMessage());
        verify(medicalRecordRepository).existsById(recordId);
        verify(medicalRecordRepository, never()).deleteById(any());
    }
    
    @Test
    @DisplayName("Should search medical records successfully")
    void shouldSearchMedicalRecordsSuccessfully() {
        // Arrange
        String searchQuery = "Hypertension";
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordRepository.findAll()).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.searchMedicalRecords(searchQuery);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        MedicalRecordResponse response = responses.get(0);
        assertEquals(recordId, response.getRecordId());
        assertEquals("Hypertension", response.getDiagnosis());
        
        verify(medicalRecordRepository).findAll();
    }
    
    @Test
    @DisplayName("Should return empty list when no medical records match search query")
    void shouldReturnEmptyListWhenNoMedicalRecordsMatchSearchQuery() {
        // Arrange
        String searchQuery = "NonExistentCondition";
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordRepository.findAll()).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.searchMedicalRecords(searchQuery);
        
        // Assert
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        
        verify(medicalRecordRepository).findAll();
    }
    
    @Test
    @DisplayName("Should search medical records by patient name")
    void shouldSearchMedicalRecordsByPatientName() {
        // Arrange
        String searchQuery = "John";
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordRepository.findAll()).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.searchMedicalRecords(searchQuery);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        MedicalRecordResponse response = responses.get(0);
        assertEquals("John Doe", response.getPatientName());
        
        verify(medicalRecordRepository).findAll();
    }
    
    @Test
    @DisplayName("Should handle case insensitive search")
    void shouldHandleCaseInsensitiveSearch() {
        // Arrange
        String searchQuery = "hypertension";
        List<MedicalRecord> records = Arrays.asList(medicalRecord);
        when(medicalRecordRepository.findAll()).thenReturn(records);
        
        // Act
        List<MedicalRecordResponse> responses = medicalRecordService.searchMedicalRecords(searchQuery);
        
        // Assert
        assertNotNull(responses);
        assertEquals(1, responses.size());
        
        MedicalRecordResponse response = responses.get(0);
        assertEquals("Hypertension", response.getDiagnosis());
        
        verify(medicalRecordRepository).findAll();
    }
    
    @Test
    @DisplayName("Should handle null values in optional fields during creation")
    void shouldHandleNullValuesInOptionalFieldsDuringCreation() {
        // Arrange
        request.setMedications(null);
        request.setNotes(null);
        
        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        
        // Act
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);
        
        // Assert
        assertNotNull(response);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }
    
    @Test
    @DisplayName("Should handle empty strings in optional fields during creation")
    void shouldHandleEmptyStringsInOptionalFieldsDuringCreation() {
        // Arrange
        request.setMedications("");
        request.setNotes("");
        
        when(userRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(medicalRecord);
        
        // Act
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);
        
        // Assert
        assertNotNull(response);
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }
}
