package com.mediway.backend.dto.request;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@DisplayName("MedicalRecordRequest DTO Tests")
class MedicalRecordRequestTest {
    
    private MedicalRecordRequest request;
    private Validator validator;
    private UUID patientId;
    private UUID doctorId;
    
    @BeforeEach
    void setUp() {
        request = new MedicalRecordRequest();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
    }
    
    @Test
    @DisplayName("Should create valid medical record request")
    void shouldCreateValidMedicalRecordRequest() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        request.setMedications("Lisinopril 10mg daily");
        request.setNotes("Regular monitoring needed");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        assertEquals(patientId, request.getPatientId());
        assertEquals(doctorId, request.getDoctorId());
        assertEquals("Hypertension", request.getDiagnosis());
        assertEquals("Lisinopril 10mg daily", request.getMedications());
        assertEquals("Regular monitoring needed", request.getNotes());
    }
    
    @Test
    @DisplayName("Should fail validation when patient ID is null")
    void shouldFailValidationWhenPatientIdIsNull() {
        request.setPatientId(null);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasPatientIdViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("patientId") &&
                             v.getMessage().equals("Patient ID is required"));
        assertTrue(hasPatientIdViolation);
    }
    
    @Test
    @DisplayName("Should fail validation when doctor ID is null")
    void shouldFailValidationWhenDoctorIdIsNull() {
        request.setPatientId(patientId);
        request.setDoctorId(null);
        request.setDiagnosis("Hypertension");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDoctorIdViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("doctorId") &&
                             v.getMessage().equals("Doctor ID is required"));
        assertTrue(hasDoctorIdViolation);
    }
    
    @Test
    @DisplayName("Should fail validation when diagnosis is null")
    void shouldFailValidationWhenDiagnosisIsNull() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis(null);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDiagnosisViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("diagnosis") &&
                             v.getMessage().equals("Diagnosis is required"));
        assertTrue(hasDiagnosisViolation);
    }
    
    @Test
    @DisplayName("Should fail validation when diagnosis is blank")
    void shouldFailValidationWhenDiagnosisIsBlank() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDiagnosisViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("diagnosis") &&
                             v.getMessage().equals("Diagnosis is required"));
        assertTrue(hasDiagnosisViolation);
    }
    
    @Test
    @DisplayName("Should fail validation when diagnosis contains only whitespace")
    void shouldFailValidationWhenDiagnosisContainsOnlyWhitespace() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("   ");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        
        boolean hasDiagnosisViolation = violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("diagnosis") &&
                             v.getMessage().equals("Diagnosis is required"));
        assertTrue(hasDiagnosisViolation);
    }
    
    @Test
    @DisplayName("Should pass validation when optional fields are null")
    void shouldPassValidationWhenOptionalFieldsAreNull() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        request.setMedications(null);
        request.setNotes(null);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should pass validation when optional fields are empty")
    void shouldPassValidationWhenOptionalFieldsAreEmpty() {
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Hypertension");
        request.setMedications("");
        request.setNotes("");
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }
    
    @Test
    @DisplayName("Should handle long text in diagnosis")
    void shouldHandleLongTextInDiagnosis() {
        String longDiagnosis = "This is a very long diagnosis that contains detailed information about the patient's medical condition, including symptoms, severity assessment, and comprehensive medical evaluation results that span multiple sentences and provide extensive clinical details.";
        
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis(longDiagnosis);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(longDiagnosis, request.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should handle long text in medications")
    void shouldHandleLongTextInMedications() {
        String longMedications = "Patient is prescribed multiple medications including Metformin 500mg twice daily with meals, Lisinopril 10mg once daily in the morning, Atorvastatin 20mg once daily at bedtime, and additional supplements as needed based on patient response and monitoring.";
        
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Diabetes");
        request.setMedications(longMedications);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(longMedications, request.getMedications());
    }
    
    @Test
    @DisplayName("Should handle long text in notes")
    void shouldHandleLongTextInNotes() {
        String longNotes = "Patient requires comprehensive monitoring including regular blood pressure checks, blood glucose monitoring, and periodic laboratory tests. Follow-up appointment scheduled in 3 months. Patient has been advised to maintain a healthy diet, regular exercise routine, and medication adherence. Additional consultations with specialists may be required based on patient's response to treatment.";
        
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis("Multiple conditions");
        request.setNotes(longNotes);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        assertEquals(longNotes, request.getNotes());
    }
    
    @Test
    @DisplayName("Should set and get patient ID correctly")
    void shouldSetAndGetPatientIdCorrectly() {
        UUID newPatientId = UUID.randomUUID();
        request.setPatientId(newPatientId);
        assertEquals(newPatientId, request.getPatientId());
    }
    
    @Test
    @DisplayName("Should set and get doctor ID correctly")
    void shouldSetAndGetDoctorIdCorrectly() {
        UUID newDoctorId = UUID.randomUUID();
        request.setDoctorId(newDoctorId);
        assertEquals(newDoctorId, request.getDoctorId());
    }
    
    @Test
    @DisplayName("Should set and get diagnosis correctly")
    void shouldSetAndGetDiagnosisCorrectly() {
        String diagnosis = "New diagnosis";
        request.setDiagnosis(diagnosis);
        assertEquals(diagnosis, request.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should set and get medications correctly")
    void shouldSetAndGetMedicationsCorrectly() {
        String medications = "New medications";
        request.setMedications(medications);
        assertEquals(medications, request.getMedications());
    }
    
    @Test
    @DisplayName("Should set and get notes correctly")
    void shouldSetAndGetNotesCorrectly() {
        String notes = "New notes";
        request.setNotes(notes);
        assertEquals(notes, request.getNotes());
    }
    
    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        String diagnosisWithSpecialChars = "Diagnosis with special chars: @#$%^&*()_+-=[]{}|;':\",./<>?";
        String medicationsWithSpecialChars = "Medications: @#$%^&*()_+-=[]{}|;':\",./<>?";
        String notesWithSpecialChars = "Notes: @#$%^&*()_+-=[]{}|;':\",./<>?";
        
        request.setPatientId(patientId);
        request.setDoctorId(doctorId);
        request.setDiagnosis(diagnosisWithSpecialChars);
        request.setMedications(medicationsWithSpecialChars);
        request.setNotes(notesWithSpecialChars);
        
        Set<ConstraintViolation<MedicalRecordRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
        
        assertEquals(diagnosisWithSpecialChars, request.getDiagnosis());
        assertEquals(medicationsWithSpecialChars, request.getMedications());
        assertEquals(notesWithSpecialChars, request.getNotes());
    }
}
