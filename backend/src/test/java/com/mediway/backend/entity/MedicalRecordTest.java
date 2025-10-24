package com.mediway.backend.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MedicalRecord Entity Tests")
class MedicalRecordTest {
    
    private MedicalRecord medicalRecord;
    private User patient;
    private Doctor doctor;
    private UUID recordId;
    private LocalDateTime testTime;
    
    @BeforeEach
    void setUp() {
        recordId = UUID.randomUUID();
        testTime = LocalDateTime.now();
        
        // Create test patient
        patient = User.builder()
                .userId(UUID.randomUUID())
                .fullName("John Doe")
                .email("john.doe@example.com")
                .passwordHash("password123")
                .phone("123-456-7890")
                .role(User.Role.PATIENT)
                .build();
        
        // Create test doctor
        doctor = Doctor.builder()
                .doctorId(UUID.randomUUID())
                .name("Dr. Jane Smith")
                .specialization("Cardiology")
                .email("jane.smith@hospital.com")
                .phone("123-456-7890")
                .build();
        
        // Create medical record
        medicalRecord = new MedicalRecord();
    }
    
    @Test
    @DisplayName("Should create medical record with default constructor")
    void shouldCreateMedicalRecordWithDefaultConstructor() {
        assertNotNull(medicalRecord);
        assertNull(medicalRecord.getRecordId());
        assertNull(medicalRecord.getPatient());
        assertNull(medicalRecord.getDoctor());
        assertNull(medicalRecord.getDiagnosis());
        assertNull(medicalRecord.getMedications());
        assertNull(medicalRecord.getNotes());
        assertNull(medicalRecord.getCreatedAt());
        assertNull(medicalRecord.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should create medical record with parameterized constructor")
    void shouldCreateMedicalRecordWithParameterizedConstructor() {
        String diagnosis = "Hypertension";
        String medications = "Lisinopril 10mg daily";
        String notes = "Patient shows improvement";
        
        MedicalRecord newRecord = new MedicalRecord(patient, doctor, diagnosis, medications, notes);
        
        assertNotNull(newRecord);
        assertEquals(patient, newRecord.getPatient());
        assertEquals(doctor, newRecord.getDoctor());
        assertEquals(diagnosis, newRecord.getDiagnosis());
        assertEquals(medications, newRecord.getMedications());
        assertEquals(notes, newRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should set and get record ID")
    void shouldSetAndGetRecordId() {
        medicalRecord.setRecordId(recordId);
        assertEquals(recordId, medicalRecord.getRecordId());
    }
    
    @Test
    @DisplayName("Should set and get patient")
    void shouldSetAndGetPatient() {
        medicalRecord.setPatient(patient);
        assertEquals(patient, medicalRecord.getPatient());
    }
    
    @Test
    @DisplayName("Should set and get doctor")
    void shouldSetAndGetDoctor() {
        medicalRecord.setDoctor(doctor);
        assertEquals(doctor, medicalRecord.getDoctor());
    }
    
    @Test
    @DisplayName("Should set and get diagnosis")
    void shouldSetAndGetDiagnosis() {
        String diagnosis = "Diabetes Type 2";
        medicalRecord.setDiagnosis(diagnosis);
        assertEquals(diagnosis, medicalRecord.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should set and get medications")
    void shouldSetAndGetMedications() {
        String medications = "Metformin 500mg twice daily";
        medicalRecord.setMedications(medications);
        assertEquals(medications, medicalRecord.getMedications());
    }
    
    @Test
    @DisplayName("Should set and get notes")
    void shouldSetAndGetNotes() {
        String notes = "Patient needs regular monitoring";
        medicalRecord.setNotes(notes);
        assertEquals(notes, medicalRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should set and get created at timestamp")
    void shouldSetAndGetCreatedAt() {
        medicalRecord.setCreatedAt(testTime);
        assertEquals(testTime, medicalRecord.getCreatedAt());
    }
    
    @Test
    @DisplayName("Should set and get updated at timestamp")
    void shouldSetAndGetUpdatedAt() {
        medicalRecord.setUpdatedAt(testTime);
        assertEquals(testTime, medicalRecord.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should handle null values for optional fields")
    void shouldHandleNullValuesForOptionalFields() {
        medicalRecord.setMedications(null);
        medicalRecord.setNotes(null);
        
        assertNull(medicalRecord.getMedications());
        assertNull(medicalRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should handle empty strings for optional fields")
    void shouldHandleEmptyStringsForOptionalFields() {
        medicalRecord.setMedications("");
        medicalRecord.setNotes("");
        
        assertEquals("", medicalRecord.getMedications());
        assertEquals("", medicalRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should handle long text for diagnosis field")
    void shouldHandleLongTextForDiagnosisField() {
        String longDiagnosis = "This is a very long diagnosis that might contain detailed information about the patient's condition, including symptoms, severity, and other relevant medical details that could span multiple sentences and paragraphs.";
        medicalRecord.setDiagnosis(longDiagnosis);
        assertEquals(longDiagnosis, medicalRecord.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should handle long text for medications field")
    void shouldHandleLongTextForMedicationsField() {
        String longMedications = "Patient is prescribed multiple medications including Metformin 500mg twice daily, Lisinopril 10mg once daily, and Atorvastatin 20mg once daily. Additional medications may be added based on patient response.";
        medicalRecord.setMedications(longMedications);
        assertEquals(longMedications, medicalRecord.getMedications());
    }
    
    @Test
    @DisplayName("Should handle long text for notes field")
    void shouldHandleLongTextForNotesField() {
        String longNotes = "Patient requires regular monitoring of blood pressure and blood glucose levels. Follow-up appointment scheduled in 3 months. Patient has been advised to maintain a healthy diet and exercise routine. Additional tests may be required based on patient's response to treatment.";
        medicalRecord.setNotes(longNotes);
        assertEquals(longNotes, medicalRecord.getNotes());
    }
    
    @Test
    @DisplayName("Should maintain referential integrity with patient")
    void shouldMaintainReferentialIntegrityWithPatient() {
        medicalRecord.setPatient(patient);
        assertSame(patient, medicalRecord.getPatient());
        
        // Verify patient's UUID is accessible
        assertEquals(patient.getUserId(), medicalRecord.getPatient().getUserId());
    }
    
    @Test
    @DisplayName("Should maintain referential integrity with doctor")
    void shouldMaintainReferentialIntegrityWithDoctor() {
        medicalRecord.setDoctor(doctor);
        assertSame(doctor, medicalRecord.getDoctor());
        
        // Verify doctor's UUID is accessible
        assertEquals(doctor.getDoctorId(), medicalRecord.getDoctor().getDoctorId());
    }
}
