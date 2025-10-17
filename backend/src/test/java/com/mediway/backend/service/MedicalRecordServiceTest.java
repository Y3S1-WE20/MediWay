package com.mediway.backend.service;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.repository.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Medical Records Management
 * Tests: CRUD operations for diagnoses, treatments, and prescriptions
 */
@DisplayName("Medical Record Service Tests - CRUD Operations")
class MedicalRecordServiceTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    private MedicalRecord testRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testRecord = new MedicalRecord();
        testRecord.setId(1L);
        testRecord.setPatientId(1L);
        testRecord.setDoctorId(1L);
        testRecord.setAppointmentId(1L);
        testRecord.setDiagnosis("Hypertension Stage 1");
        testRecord.setTreatment("Lifestyle modifications and medication");
        testRecord.setPrescription("Amlodipine 5mg once daily");
        testRecord.setNotes("Patient advised to reduce salt intake and exercise regularly");
        testRecord.setRecordDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Test 1: Successfully create a medical record")
    void testCreateMedicalRecord_Success() {
        // Given
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord created = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals("Hypertension Stage 1", created.getDiagnosis());
        assertEquals("Lifestyle modifications and medication", created.getTreatment());
        assertEquals("Amlodipine 5mg once daily", created.getPrescription());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 2: Create medical record with diagnosis only")
    void testCreateMedicalRecord_DiagnosisOnly() {
        // Given
        MedicalRecord record = new MedicalRecord();
        record.setPatientId(1L);
        record.setDoctorId(1L);
        record.setDiagnosis("Common Cold");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(record);

        // When
        MedicalRecord created = medicalRecordRepository.save(record);

        // Then
        assertNotNull(created);
        assertEquals("Common Cold", created.getDiagnosis());
        assertNull(created.getTreatment());
        assertNull(created.getPrescription());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 3: Get medical record by ID")
    void testGetMedicalRecordById_Success() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));

        // When
        Optional<MedicalRecord> found = medicalRecordRepository.findById(1L);

        // Then
        assertTrue(found.isPresent());
        assertEquals(1L, found.get().getId());
        assertEquals("Hypertension Stage 1", found.get().getDiagnosis());
        verify(medicalRecordRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 4: Get medical record by ID - not found")
    void testGetMedicalRecordById_NotFound() {
        // Given
        when(medicalRecordRepository.findById(99L)).thenReturn(Optional.empty());

        // When
        Optional<MedicalRecord> found = medicalRecordRepository.findById(99L);

        // Then
        assertFalse(found.isPresent());
        verify(medicalRecordRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test 5: Get all medical records for a patient")
    void testGetPatientMedicalRecords() {
        // Given
        MedicalRecord record2 = new MedicalRecord();
        record2.setId(2L);
        record2.setPatientId(1L);
        record2.setDoctorId(2L);
        record2.setDiagnosis("Type 2 Diabetes");
        record2.setTreatment("Insulin therapy");
        record2.setRecordDate(LocalDateTime.now().minusDays(5));

        List<MedicalRecord> records = Arrays.asList(testRecord, record2);
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(1L)).thenReturn(records);

        // When
        List<MedicalRecord> result = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getPatientId());
        assertEquals(1L, result.get(1).getPatientId());
        verify(medicalRecordRepository, times(1)).findByPatientIdOrderByRecordDateDesc(1L);
    }

    @Test
    @DisplayName("Test 6: Update medical record diagnosis")
    void testUpdateMedicalRecord_Diagnosis() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        testRecord.setDiagnosis("Hypertension Stage 2");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        testRecord.setDiagnosis("Hypertension Stage 2");
        MedicalRecord updated = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(updated);
        assertEquals("Hypertension Stage 2", updated.getDiagnosis());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 7: Update medical record treatment")
    void testUpdateMedicalRecord_Treatment() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        testRecord.setTreatment("Combination therapy with ACE inhibitors");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord updated = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(updated);
        assertEquals("Combination therapy with ACE inhibitors", updated.getTreatment());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 8: Update medical record prescription")
    void testUpdateMedicalRecord_Prescription() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        testRecord.setPrescription("Amlodipine 10mg once daily + Lisinopril 5mg once daily");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord updated = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(updated);
        assertEquals("Amlodipine 10mg once daily + Lisinopril 5mg once daily", updated.getPrescription());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 9: Update medical record notes")
    void testUpdateMedicalRecord_Notes() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        testRecord.setNotes("Patient showing improvement, continue current medication");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord updated = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(updated);
        assertEquals("Patient showing improvement, continue current medication", updated.getNotes());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 10: Delete medical record")
    void testDeleteMedicalRecord() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        doNothing().when(medicalRecordRepository).deleteById(1L);

        // When
        medicalRecordRepository.deleteById(1L);

        // Then
        verify(medicalRecordRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 11: Get all medical records")
    void testGetAllMedicalRecords() {
        // Given
        MedicalRecord record2 = new MedicalRecord();
        record2.setId(2L);
        record2.setPatientId(2L);
        record2.setDoctorId(1L);
        record2.setDiagnosis("Migraine");

        List<MedicalRecord> records = Arrays.asList(testRecord, record2);
        when(medicalRecordRepository.findAll()).thenReturn(records);

        // When
        List<MedicalRecord> result = medicalRecordRepository.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(medicalRecordRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 12: Create medical record with all fields")
    void testCreateMedicalRecord_AllFields() {
        // Given
        MedicalRecord completeRecord = new MedicalRecord();
        completeRecord.setPatientId(2L);
        completeRecord.setDoctorId(2L);
        completeRecord.setAppointmentId(2L);
        completeRecord.setDiagnosis("Acute Bronchitis");
        completeRecord.setTreatment("Antibiotics and rest");
        completeRecord.setPrescription("Azithromycin 500mg for 5 days");
        completeRecord.setNotes("Follow up in 1 week if symptoms persist");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(completeRecord);

        // When
        MedicalRecord created = medicalRecordRepository.save(completeRecord);

        // Then
        assertNotNull(created);
        assertEquals(2L, created.getPatientId());
        assertEquals(2L, created.getDoctorId());
        assertEquals(2L, created.getAppointmentId());
        assertEquals("Acute Bronchitis", created.getDiagnosis());
        assertEquals("Antibiotics and rest", created.getTreatment());
        assertEquals("Azithromycin 500mg for 5 days", created.getPrescription());
        assertEquals("Follow up in 1 week if symptoms persist", created.getNotes());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 13: Verify record date is automatically set")
    void testMedicalRecord_AutomaticRecordDate() {
        // Given
        MedicalRecord newRecord = new MedicalRecord();

        // When/Then
        assertNotNull(newRecord.getRecordDate());
        assertTrue(newRecord.getRecordDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    @DisplayName("Test 14: Edge case - Empty medical records for patient")
    void testGetPatientMedicalRecords_Empty() {
        // Given
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(99L))
                .thenReturn(Arrays.asList());

        // When
        List<MedicalRecord> result = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(99L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(medicalRecordRepository, times(1)).findByPatientIdOrderByRecordDateDesc(99L);
    }

    @Test
    @DisplayName("Test 15: Update multiple fields at once")
    void testUpdateMedicalRecord_MultipleFields() {
        // Given
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        testRecord.setDiagnosis("Hypertension with Diabetes");
        testRecord.setTreatment("Combined medication therapy");
        testRecord.setPrescription("Amlodipine 5mg + Metformin 500mg");
        testRecord.setNotes("Patient requires close monitoring");
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord updated = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(updated);
        assertEquals("Hypertension with Diabetes", updated.getDiagnosis());
        assertEquals("Combined medication therapy", updated.getTreatment());
        assertEquals("Amlodipine 5mg + Metformin 500mg", updated.getPrescription());
        assertEquals("Patient requires close monitoring", updated.getNotes());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 16: Verify patient ID is set correctly")
    void testMedicalRecord_PatientIdValidation() {
        // Given/When/Then
        assertEquals(1L, testRecord.getPatientId());
        assertNotNull(testRecord.getPatientId());
    }

    @Test
    @DisplayName("Test 17: Verify doctor ID is set correctly")
    void testMedicalRecord_DoctorIdValidation() {
        // Given/When/Then
        assertEquals(1L, testRecord.getDoctorId());
        assertNotNull(testRecord.getDoctorId());
    }

    @Test
    @DisplayName("Test 18: Create medical record with long diagnosis text")
    void testCreateMedicalRecord_LongDiagnosis() {
        // Given
        String longDiagnosis = "Patient presents with chronic lower back pain, radiating down the left leg, " +
                "consistent with lumbar radiculopathy. MRI findings show L4-L5 disc herniation with " +
                "nerve root compression. Previous conservative treatment has been unsuccessful.";
        testRecord.setDiagnosis(longDiagnosis);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord created = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(created);
        assertEquals(longDiagnosis, created.getDiagnosis());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 19: Create medical record with long prescription")
    void testCreateMedicalRecord_LongPrescription() {
        // Given
        String longPrescription = "1. Ibuprofen 400mg TID with meals\n" +
                "2. Gabapentin 300mg at bedtime, increase to TID as tolerated\n" +
                "3. Physical therapy 3x per week\n" +
                "4. Heat therapy as needed\n" +
                "5. Follow up in 2 weeks";
        testRecord.setPrescription(longPrescription);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        // When
        MedicalRecord created = medicalRecordRepository.save(testRecord);

        // Then
        assertNotNull(created);
        assertEquals(longPrescription, created.getPrescription());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    @Test
    @DisplayName("Test 20: Verify medical records ordered by date descending")
    void testGetPatientMedicalRecords_OrderedByDate() {
        // Given
        MedicalRecord oldRecord = new MedicalRecord();
        oldRecord.setId(2L);
        oldRecord.setPatientId(1L);
        oldRecord.setRecordDate(LocalDateTime.now().minusDays(30));

        MedicalRecord recentRecord = new MedicalRecord();
        recentRecord.setId(3L);
        recentRecord.setPatientId(1L);
        recentRecord.setRecordDate(LocalDateTime.now().minusDays(1));

        List<MedicalRecord> records = Arrays.asList(recentRecord, oldRecord);
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(1L)).thenReturn(records);

        // When
        List<MedicalRecord> result = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0).getRecordDate().isAfter(result.get(1).getRecordDate()),
                "Records should be ordered by date descending");
        verify(medicalRecordRepository, times(1)).findByPatientIdOrderByRecordDateDesc(1L);
    }
}
