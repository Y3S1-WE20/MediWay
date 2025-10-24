package com.mediway.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MedicalRecordResponse DTO Tests")
class MedicalRecordResponseTest {
    
    private MedicalRecordResponse response;
    private UUID recordId;
    private UUID patientId;
    private UUID doctorId;
    private LocalDateTime testTime;
    
    @BeforeEach
    void setUp() {
        response = new MedicalRecordResponse();
        recordId = UUID.randomUUID();
        patientId = UUID.randomUUID();
        doctorId = UUID.randomUUID();
        testTime = LocalDateTime.now();
    }
    
    @Test
    @DisplayName("Should create medical record response with default constructor")
    void shouldCreateMedicalRecordResponseWithDefaultConstructor() {
        assertNotNull(response);
        assertNull(response.getRecordId());
        assertNull(response.getPatientId());
        assertNull(response.getPatientName());
        assertNull(response.getDoctorId());
        assertNull(response.getDoctorName());
        assertNull(response.getDiagnosis());
        assertNull(response.getMedications());
        assertNull(response.getNotes());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should set and get record ID")
    void shouldSetAndGetRecordId() {
        response.setRecordId(recordId);
        assertEquals(recordId, response.getRecordId());
    }
    
    @Test
    @DisplayName("Should set and get patient ID")
    void shouldSetAndGetPatientId() {
        response.setPatientId(patientId);
        assertEquals(patientId, response.getPatientId());
    }
    
    @Test
    @DisplayName("Should set and get patient name")
    void shouldSetAndGetPatientName() {
        String patientName = "John Doe";
        response.setPatientName(patientName);
        assertEquals(patientName, response.getPatientName());
    }
    
    @Test
    @DisplayName("Should set and get doctor ID")
    void shouldSetAndGetDoctorId() {
        response.setDoctorId(doctorId);
        assertEquals(doctorId, response.getDoctorId());
    }
    
    @Test
    @DisplayName("Should set and get doctor name")
    void shouldSetAndGetDoctorName() {
        String doctorName = "Dr. Jane Smith";
        response.setDoctorName(doctorName);
        assertEquals(doctorName, response.getDoctorName());
    }
    
    @Test
    @DisplayName("Should set and get diagnosis")
    void shouldSetAndGetDiagnosis() {
        String diagnosis = "Hypertension";
        response.setDiagnosis(diagnosis);
        assertEquals(diagnosis, response.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should set and get medications")
    void shouldSetAndGetMedications() {
        String medications = "Lisinopril 10mg daily";
        response.setMedications(medications);
        assertEquals(medications, response.getMedications());
    }
    
    @Test
    @DisplayName("Should set and get notes")
    void shouldSetAndGetNotes() {
        String notes = "Regular monitoring needed";
        response.setNotes(notes);
        assertEquals(notes, response.getNotes());
    }
    
    @Test
    @DisplayName("Should set and get created at timestamp")
    void shouldSetAndGetCreatedAt() {
        response.setCreatedAt(testTime);
        assertEquals(testTime, response.getCreatedAt());
    }
    
    @Test
    @DisplayName("Should set and get updated at timestamp")
    void shouldSetAndGetUpdatedAt() {
        response.setUpdatedAt(testTime);
        assertEquals(testTime, response.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should handle null values for all fields")
    void shouldHandleNullValuesForAllFields() {
        response.setRecordId(null);
        response.setPatientId(null);
        response.setPatientName(null);
        response.setDoctorId(null);
        response.setDoctorName(null);
        response.setDiagnosis(null);
        response.setMedications(null);
        response.setNotes(null);
        response.setCreatedAt(null);
        response.setUpdatedAt(null);
        
        assertNull(response.getRecordId());
        assertNull(response.getPatientId());
        assertNull(response.getPatientName());
        assertNull(response.getDoctorId());
        assertNull(response.getDoctorName());
        assertNull(response.getDiagnosis());
        assertNull(response.getMedications());
        assertNull(response.getNotes());
        assertNull(response.getCreatedAt());
        assertNull(response.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should handle empty strings for text fields")
    void shouldHandleEmptyStringsForTextFieldFields() {
        response.setPatientName("");
        response.setDoctorName("");
        response.setDiagnosis("");
        response.setMedications("");
        response.setNotes("");
        
        assertEquals("", response.getPatientName());
        assertEquals("", response.getDoctorName());
        assertEquals("", response.getDiagnosis());
        assertEquals("", response.getMedications());
        assertEquals("", response.getNotes());
    }
    
    @Test
    @DisplayName("Should handle long text in diagnosis field")
    void shouldHandleLongTextInDiagnosisField() {
        String longDiagnosis = "This is a very long diagnosis that contains detailed information about the patient's medical condition, including comprehensive symptoms analysis, severity assessment, differential diagnosis considerations, and extensive clinical evaluation results that span multiple sentences and provide thorough medical documentation.";
        response.setDiagnosis(longDiagnosis);
        assertEquals(longDiagnosis, response.getDiagnosis());
    }
    
    @Test
    @DisplayName("Should handle long text in medications field")
    void shouldHandleLongTextInMedicationsField() {
        String longMedications = "Patient is prescribed a comprehensive medication regimen including Metformin 500mg twice daily with meals for blood glucose control, Lisinopril 10mg once daily in the morning for hypertension management, Atorvastatin 20mg once daily at bedtime for cholesterol control, and additional supplements including vitamin D and omega-3 fatty acids as needed based on patient response and regular monitoring.";
        response.setMedications(longMedications);
        assertEquals(longMedications, response.getMedications());
    }
    
    @Test
    @DisplayName("Should handle long text in notes field")
    void shouldHandleLongTextInNotesField() {
        String longNotes = "Patient requires comprehensive monitoring including regular blood pressure measurements, blood glucose monitoring with target ranges, periodic laboratory tests including lipid panel and kidney function tests, and regular follow-up appointments. Patient has been extensively counseled on maintaining a healthy diet with specific nutritional guidelines, regular exercise routine with appropriate intensity levels, and strict medication adherence. Additional consultations with endocrinologist and cardiologist may be required based on patient's response to treatment and ongoing assessment.";
        response.setNotes(longNotes);
        assertEquals(longNotes, response.getNotes());
    }
    
    @Test
    @DisplayName("Should handle special characters in text fields")
    void shouldHandleSpecialCharactersInTextFields() {
        String diagnosisWithSpecialChars = "Diagnosis with special chars: @#$%^&*()_+-=[]{}|;':\",./<>?";
        String medicationsWithSpecialChars = "Medications: @#$%^&*()_+-=[]{}|;':\",./<>?";
        String notesWithSpecialChars = "Notes: @#$%^&*()_+-=[]{}|;':\",./<>?";
        
        response.setDiagnosis(diagnosisWithSpecialChars);
        response.setMedications(medicationsWithSpecialChars);
        response.setNotes(notesWithSpecialChars);
        
        assertEquals(diagnosisWithSpecialChars, response.getDiagnosis());
        assertEquals(medicationsWithSpecialChars, response.getMedications());
        assertEquals(notesWithSpecialChars, response.getNotes());
    }
    
    @Test
    @DisplayName("Should handle unicode characters in text fields")
    void shouldHandleUnicodeCharactersInTextFields() {
        String diagnosisWithUnicode = "Diagnosis with unicode: 中文 العربية हिन्दी русский";
        String medicationsWithUnicode = "Medications with unicode: 中文 العربية हिन्दी русский";
        String notesWithUnicode = "Notes with unicode: 中文 العربية हिन्दी русский";
        
        response.setDiagnosis(diagnosisWithUnicode);
        response.setMedications(medicationsWithUnicode);
        response.setNotes(notesWithUnicode);
        
        assertEquals(diagnosisWithUnicode, response.getDiagnosis());
        assertEquals(medicationsWithUnicode, response.getMedications());
        assertEquals(notesWithUnicode, response.getNotes());
    }
    
    @Test
    @DisplayName("Should handle different UUID formats")
    void shouldHandleDifferentUuidFormats() {
        UUID uuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        UUID uuid2 = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
        UUID uuid3 = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");
        
        response.setRecordId(uuid1);
        response.setPatientId(uuid2);
        response.setDoctorId(uuid3);
        
        assertEquals(uuid1, response.getRecordId());
        assertEquals(uuid2, response.getPatientId());
        assertEquals(uuid3, response.getDoctorId());
    }
    
    @Test
    @DisplayName("Should handle timestamp precision")
    void shouldHandleTimestampPrecision() {
        LocalDateTime preciseTime = LocalDateTime.of(2024, 1, 15, 14, 30, 45, 123456789);
        
        response.setCreatedAt(preciseTime);
        response.setUpdatedAt(preciseTime);
        
        assertEquals(preciseTime, response.getCreatedAt());
        assertEquals(preciseTime, response.getUpdatedAt());
    }
    
    @Test
    @DisplayName("Should create complete medical record response")
    void shouldCreateCompleteMedicalRecordResponse() {
        response.setRecordId(recordId);
        response.setPatientId(patientId);
        response.setPatientName("John Doe");
        response.setDoctorId(doctorId);
        response.setDoctorName("Dr. Jane Smith");
        response.setDiagnosis("Hypertension");
        response.setMedications("Lisinopril 10mg daily");
        response.setNotes("Regular monitoring needed");
        response.setCreatedAt(testTime);
        response.setUpdatedAt(testTime);
        
        assertEquals(recordId, response.getRecordId());
        assertEquals(patientId, response.getPatientId());
        assertEquals("John Doe", response.getPatientName());
        assertEquals(doctorId, response.getDoctorId());
        assertEquals("Dr. Jane Smith", response.getDoctorName());
        assertEquals("Hypertension", response.getDiagnosis());
        assertEquals("Lisinopril 10mg daily", response.getMedications());
        assertEquals("Regular monitoring needed", response.getNotes());
        assertEquals(testTime, response.getCreatedAt());
        assertEquals(testTime, response.getUpdatedAt());
    }
}
