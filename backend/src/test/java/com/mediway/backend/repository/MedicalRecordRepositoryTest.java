package com.mediway.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.User;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MedicalRecord Repository Tests")
class MedicalRecordRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    private User patient1;
    private User patient2;
    private Doctor doctor1;
    private Doctor doctor2;
    private MedicalRecord record1;
    private MedicalRecord record2;
    private MedicalRecord record3;
    
    @BeforeEach
    void setUp() {
        // Create test patients
        patient1 = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .passwordHash("password123")
                .phone("123-456-7890")
                .role(User.Role.PATIENT)
                .build();
        entityManager.persistAndFlush(patient1);
        
        patient2 = User.builder()
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .passwordHash("password123")
                .phone("123-456-7891")
                .role(User.Role.PATIENT)
                .build();
        entityManager.persistAndFlush(patient2);
        
        // Create test doctors
        doctor1 = Doctor.builder()
                .name("Dr. Alice Johnson")
                .specialization("Cardiology")
                .email("alice.johnson@hospital.com")
                .phone("123-456-7890")
                .build();
        entityManager.persistAndFlush(doctor1);
        
        doctor2 = Doctor.builder()
                .name("Dr. Bob Wilson")
                .specialization("Neurology")
                .email("bob.wilson@hospital.com")
                .phone("123-456-7891")
                .build();
        entityManager.persistAndFlush(doctor2);
        
        // Create test medical records
        record1 = new MedicalRecord(patient1, doctor1, "Hypertension", "Lisinopril 10mg", "Regular monitoring needed");
        record1.setCreatedAt(LocalDateTime.now().minusDays(1));
        entityManager.persistAndFlush(record1);
        
        record2 = new MedicalRecord(patient1, doctor2, "Diabetes", "Metformin 500mg", "Blood sugar monitoring");
        record2.setCreatedAt(LocalDateTime.now().minusHours(12));
        entityManager.persistAndFlush(record2);
        
        record3 = new MedicalRecord(patient2, doctor1, "Migraine", "Sumatriptan", "Pain management");
        record3.setCreatedAt(LocalDateTime.now().minusHours(6));
        entityManager.persistAndFlush(record3);
        
        entityManager.clear();
    }
    
    @Test
    @DisplayName("Should find medical records by patient ID ordered by creation date descending")
    void shouldFindMedicalRecordsByPatientIdOrderedByCreatedAtDesc() {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientUserIdOrderByCreatedAtDesc(patient1.getUserId());
        
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // Verify ordering (newest first)
        assertTrue(records.get(0).getCreatedAt().isAfter(records.get(1).getCreatedAt()));
        
        // Verify all records belong to the same patient
        assertTrue(records.stream().allMatch(record -> record.getPatient().getUserId().equals(patient1.getUserId())));
    }
    
    @Test
    @DisplayName("Should find medical records by doctor ID ordered by creation date descending")
    void shouldFindMedicalRecordsByDoctorIdOrderedByCreatedAtDesc() {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctor1.getDoctorId());
        
        assertNotNull(records);
        assertEquals(2, records.size());
        
        // Verify ordering (newest first)
        assertTrue(records.get(0).getCreatedAt().isAfter(records.get(1).getCreatedAt()));
        
        // Verify all records belong to the same doctor
        assertTrue(records.stream().allMatch(record -> record.getDoctor().getDoctorId().equals(doctor1.getDoctorId())));
    }
    
    @Test
    @DisplayName("Should find medical records by patient and doctor combination")
    void shouldFindMedicalRecordsByPatientAndDoctor() {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientAndDoctor(patient1.getUserId(), doctor1.getDoctorId());
        
        assertNotNull(records);
        assertEquals(1, records.size());
        
        MedicalRecord record = records.get(0);
        assertEquals(patient1.getUserId(), record.getPatient().getUserId());
        assertEquals(doctor1.getDoctorId(), record.getDoctor().getDoctorId());
        assertEquals("Hypertension", record.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should return empty list when no records found for patient")
    void shouldReturnEmptyListWhenNoRecordsFoundForPatient() {
        UUID nonExistentPatientId = UUID.randomUUID();
        List<MedicalRecord> records = medicalRecordRepository.findByPatientUserIdOrderByCreatedAtDesc(nonExistentPatientId);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    @DisplayName("Should return empty list when no records found for doctor")
    void shouldReturnEmptyListWhenNoRecordsFoundForDoctor() {
        UUID nonExistentDoctorId = UUID.randomUUID();
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(nonExistentDoctorId);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    @DisplayName("Should return empty list when no records found for patient and doctor combination")
    void shouldReturnEmptyListWhenNoRecordsFoundForPatientAndDoctorCombination() {
        UUID nonExistentPatientId = UUID.randomUUID();
        UUID nonExistentDoctorId = UUID.randomUUID();
        List<MedicalRecord> records = medicalRecordRepository.findByPatientAndDoctor(nonExistentPatientId, nonExistentDoctorId);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }
    
    @Test
    @DisplayName("Should check if medical record exists by record ID and doctor ID")
    void shouldCheckIfMedicalRecordExistsByRecordIdAndDoctorId() {
        boolean exists = medicalRecordRepository.existsByRecordIdAndDoctorDoctorId(record1.getRecordId(), doctor1.getDoctorId());
        assertTrue(exists);
    }
    
    @Test
    @DisplayName("Should return false when medical record does not exist by record ID and doctor ID")
    void shouldReturnFalseWhenMedicalRecordDoesNotExistByRecordIdAndDoctorId() {
        boolean exists = medicalRecordRepository.existsByRecordIdAndDoctorDoctorId(record1.getRecordId(), doctor2.getDoctorId());
        assertFalse(exists);
    }
    
    @Test
    @DisplayName("Should return false when record ID does not exist")
    void shouldReturnFalseWhenRecordIdDoesNotExist() {
        UUID nonExistentRecordId = UUID.randomUUID();
        boolean exists = medicalRecordRepository.existsByRecordIdAndDoctorDoctorId(nonExistentRecordId, doctor1.getDoctorId());
        assertFalse(exists);
    }
    
    @Test
    @DisplayName("Should save and find medical record")
    void shouldSaveAndFindMedicalRecord() {
        MedicalRecord newRecord = new MedicalRecord(patient2, doctor2, "Allergy", "Antihistamine", "Seasonal allergy");
        MedicalRecord savedRecord = medicalRecordRepository.save(newRecord);
        
        Optional<MedicalRecord> foundRecord = medicalRecordRepository.findById(savedRecord.getRecordId());
        
        assertTrue(foundRecord.isPresent());
        assertEquals(savedRecord.getRecordId(), foundRecord.get().getRecordId());
        assertEquals("Allergy", foundRecord.get().getDiagnosis());
        assertEquals(patient2.getUserId(), foundRecord.get().getPatient().getUserId());
        assertEquals(doctor2.getDoctorId(), foundRecord.get().getDoctor().getDoctorId());
    }
    
    @Test
    @DisplayName("Should update medical record")
    void shouldUpdateMedicalRecord() {
        record1.setDiagnosis("Updated Hypertension");
        record1.setMedications("Updated Lisinopril 20mg");
        record1.setNotes("Updated monitoring plan");
        
        MedicalRecord updatedRecord = medicalRecordRepository.save(record1);
        
        assertEquals("Updated Hypertension", updatedRecord.getDiagnosis());
        assertEquals("Updated Lisinopril 20mg", updatedRecord.getMedications());
        assertEquals("Updated monitoring plan", updatedRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should delete medical record")
    void shouldDeleteMedicalRecord() {
        UUID recordId = record1.getRecordId();
        medicalRecordRepository.deleteById(recordId);
        
        Optional<MedicalRecord> deletedRecord = medicalRecordRepository.findById(recordId);
        assertFalse(deletedRecord.isPresent());
    }
    
    @Test
    @DisplayName("Should find all medical records")
    void shouldFindAllMedicalRecords() {
        List<MedicalRecord> allRecords = medicalRecordRepository.findAll();
        
        assertNotNull(allRecords);
        assertEquals(3, allRecords.size());
    }
    
    @Test
    @DisplayName("Should verify correct ordering by creation date for patient records")
    void shouldVerifyCorrectOrderingByCreationDateForPatientRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientUserIdOrderByCreatedAtDesc(patient1.getUserId());
        
        // Verify that records are ordered by creation date descending (newest first)
        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue(records.get(i).getCreatedAt().isAfter(records.get(i + 1).getCreatedAt()) ||
                      records.get(i).getCreatedAt().isEqual(records.get(i + 1).getCreatedAt()));
        }
    }
    
    @Test
    @DisplayName("Should verify correct ordering by creation date for doctor records")
    void shouldVerifyCorrectOrderingByCreationDateForDoctorRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctor1.getDoctorId());
        
        // Verify that records are ordered by creation date descending (newest first)
        for (int i = 0; i < records.size() - 1; i++) {
            assertTrue(records.get(i).getCreatedAt().isAfter(records.get(i + 1).getCreatedAt()) ||
                      records.get(i).getCreatedAt().isEqual(records.get(i + 1).getCreatedAt()));
        }
    }
}
