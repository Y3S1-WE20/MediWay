package com.mediway.backend.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MedicalRecord Entity Tests")
class MedicalRecordEntityTest {

    private MedicalRecord medicalRecord;

    @BeforeEach
    void setUp() {
        medicalRecord = new MedicalRecord();
    }

    @Test
    @DisplayName("Should create MedicalRecord with default constructor and default values")
    void testDefaultConstructor() {
        MedicalRecord newRecord = new MedicalRecord();
        assertNotNull(newRecord);
        assertNull(newRecord.getId());
        assertNotNull(newRecord.getRecordDate());
    }

    @Test
    @DisplayName("Should create MedicalRecord with parameterized constructor")
    void testParameterizedConstructor() {
        MedicalRecord newRecord = new MedicalRecord(
            1L, 2L, 3L, 
            "Hypertension", 
            "Medication and lifestyle changes",
            "Lisinopril 10mg daily",
            "Patient advised to reduce salt intake"
        );
        
        assertEquals(1L, newRecord.getPatientId());
        assertEquals(2L, newRecord.getDoctorId());
        assertEquals(3L, newRecord.getAppointmentId());
        assertEquals("Hypertension", newRecord.getDiagnosis());
        assertEquals("Medication and lifestyle changes", newRecord.getTreatment());
        assertEquals("Lisinopril 10mg daily", newRecord.getPrescription());
        assertEquals("Patient advised to reduce salt intake", newRecord.getNotes());
    }

    @Test
    @DisplayName("Should set and get all MedicalRecord fields correctly")
    void testGettersAndSetters() {
        LocalDateTime recordDate = LocalDateTime.of(2025, 10, 23, 15, 30);

        medicalRecord.setId(1L);
        medicalRecord.setPatientId(10L);
        medicalRecord.setDoctorId(20L);
        medicalRecord.setAppointmentId(30L);
        medicalRecord.setDiagnosis("Type 2 Diabetes");
        medicalRecord.setTreatment("Insulin therapy");
        medicalRecord.setPrescription("Metformin 500mg twice daily");
        medicalRecord.setNotes("Monitor blood sugar levels");
        medicalRecord.setRecordDate(recordDate);

        assertEquals(1L, medicalRecord.getId());
        assertEquals(10L, medicalRecord.getPatientId());
        assertEquals(20L, medicalRecord.getDoctorId());
        assertEquals(30L, medicalRecord.getAppointmentId());
        assertEquals("Type 2 Diabetes", medicalRecord.getDiagnosis());
        assertEquals("Insulin therapy", medicalRecord.getTreatment());
        assertEquals("Metformin 500mg twice daily", medicalRecord.getPrescription());
        assertEquals("Monitor blood sugar levels", medicalRecord.getNotes());
        assertEquals(recordDate, medicalRecord.getRecordDate());
    }

    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullableFields() {
        medicalRecord.setAppointmentId(null);
        medicalRecord.setDiagnosis(null);
        medicalRecord.setTreatment(null);
        medicalRecord.setPrescription(null);
        medicalRecord.setNotes(null);

        assertNull(medicalRecord.getAppointmentId());
        assertNull(medicalRecord.getDiagnosis());
        assertNull(medicalRecord.getTreatment());
        assertNull(medicalRecord.getPrescription());
        assertNull(medicalRecord.getNotes());
    }

    @Test
    @DisplayName("Should handle PrePersist onCreate callback")
    void testOnCreate() {
        MedicalRecord newRecord = new MedicalRecord();
        newRecord.setRecordDate(null);
        newRecord.onCreate();

        assertNotNull(newRecord.getRecordDate());
    }

    @Test
    @DisplayName("Should not override existing recordDate in onCreate")
    void testOnCreateWithExistingDate() {
        LocalDateTime existingDate = LocalDateTime.of(2023, 5, 15, 10, 0);
        medicalRecord.setRecordDate(existingDate);
        medicalRecord.onCreate();

        assertEquals(existingDate, medicalRecord.getRecordDate());
    }

    @Test
    @DisplayName("Should handle long text in diagnosis field")
    void testLongDiagnosis() {
        String longDiagnosis = "Patient presents with multiple symptoms including " +
            "fatigue, shortness of breath, and chest pain. ".repeat(20);
        medicalRecord.setDiagnosis(longDiagnosis);
        assertEquals(longDiagnosis, medicalRecord.getDiagnosis());
    }

    @Test
    @DisplayName("Should handle long text in treatment field")
    void testLongTreatment() {
        String longTreatment = "Comprehensive treatment plan including medication, " +
            "physical therapy, and lifestyle modifications. ".repeat(15);
        medicalRecord.setTreatment(longTreatment);
        assertEquals(longTreatment, medicalRecord.getTreatment());
    }

    @Test
    @DisplayName("Should handle long text in prescription field")
    void testLongPrescription() {
        String longPrescription = "1. Medication A 100mg daily\n2. Medication B 50mg twice daily\n".repeat(10);
        medicalRecord.setPrescription(longPrescription);
        assertEquals(longPrescription, medicalRecord.getPrescription());
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void testSpecialCharacters() {
        medicalRecord.setDiagnosis("Patient has COVID-19 (δ-variant)");
        medicalRecord.setTreatment("Take 2×500mg paracetamol & rest");
        medicalRecord.setNotes("Follow-up in 7–10 days");

        assertEquals("Patient has COVID-19 (δ-variant)", medicalRecord.getDiagnosis());
        assertEquals("Take 2×500mg paracetamol & rest", medicalRecord.getTreatment());
        assertEquals("Follow-up in 7–10 days", medicalRecord.getNotes());
    }
}
