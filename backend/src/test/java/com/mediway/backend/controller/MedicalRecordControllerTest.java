package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (MedicalRecordControllerTest):
 * - create medical record as doctor                      : Positive
 * - create medical record as admin                       : Positive
 * - reject create medical record as patient              : Negative (forbidden)
 * - return unauthorized when user not found              : Negative
 * - reject record without patient ID                     : Negative
 * - get medical records for patient                      : Positive
 * - allow doctor to view patient records                 : Positive
 * - get medical record by ID - Success                   : Positive
 * - get medical record by ID - Not Found                 : Negative
 * - update medical record                                 : Positive
 * - delete medical record                                 : Positive
 * - handle exceptions on create/get/update/delete         : Negative (error handling)
 * - default userId when null                              : Edge
 * - set doctor ID automatically for doctor user           : Edge
 * - forbid patient/admin/doctor unauthorized access cases : Negative
 */

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Medical Record Controller Tests")
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordRepository medicalRecordRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MedicalRecordController medicalRecordController;

    private User doctorUser;
    private User patientUser;
    private User adminUser;
    private MedicalRecord testRecord;

    @BeforeEach
    void setUp() {
        // Setup doctor user
        doctorUser = new User();
        doctorUser.setId(1L);
        doctorUser.setRole(User.Role.DOCTOR);
        doctorUser.setName("Dr. Smith");

        // Setup patient user
        patientUser = new User();
        patientUser.setId(2L);
        patientUser.setRole(User.Role.PATIENT);
        patientUser.setName("John Doe");

        // Setup admin user
        adminUser = new User();
        adminUser.setId(3L);
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setName("Admin User");

        // Setup test medical record
        testRecord = new MedicalRecord();
        testRecord.setId(1L);
        testRecord.setPatientId(2L);
        testRecord.setDoctorId(1L);
        testRecord.setDiagnosis("Common Cold");
        testRecord.setTreatment("Rest and fluids");
        testRecord.setPrescription("Paracetamol 500mg");
        testRecord.setNotes("Follow up in 1 week");
        testRecord.setRecordDate(LocalDateTime.now());
    }

    // Positive: Creates a medical record when user is a doctor
    @Test
    @DisplayName("Should create medical record as doctor")
    void testCreateMedicalRecord_AsDoctor() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    // Positive: Creates a medical record when user is an admin
    @Test
    @DisplayName("Should create medical record as admin")
    void testCreateMedicalRecord_AsAdmin() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    // Negative: Rejects creation of medical record when user is a patient (forbidden)
    @Test
    @DisplayName("Should reject create medical record as patient")
    void testCreateMedicalRecord_AsPatient_Forbidden() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).save(any());
    }

    // Negative: Returns unauthorized when user is not found during creation
    @Test
    @DisplayName("Should return unauthorized when user not found")
    void testCreateMedicalRecord_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 1L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Rejects creation when patient ID is missing
    @Test
    @DisplayName("Should reject record without patient ID")
    void testCreateMedicalRecord_MissingPatientId() {
        testRecord.setPatientId(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    // Positive: Retrieves medical records for a patient successfully
    @Test
    @DisplayName("Should get medical records for patient")
    void testGetPatientMedicalRecords_Success() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(2L)).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(2L, 2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).findByPatientIdOrderByRecordDateDesc(2L);
    }

    // Positive: Allows doctor to view patient records
    @Test
    @DisplayName("Should allow doctor to view patient records")
    void testGetPatientMedicalRecords_AsDoctor() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(2L)).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(2L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Positive: Retrieves a medical record by ID successfully
    @Test
    @DisplayName("Should get medical record by ID")
    void testGetMedicalRecordById_Success() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        ResponseEntity<?> response = medicalRecordController.getMedicalRecordById(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Negative: Returns 404 when medical record is not found by ID
    @Test
    @DisplayName("Should return 404 when record not found")
    void testGetMedicalRecordById_NotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.getMedicalRecordById(1L, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Positive: Updates a medical record successfully
    @Test
    @DisplayName("Should update medical record")
    void testUpdateMedicalRecord_Success() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        MedicalRecord updates = new MedicalRecord();
        updates.setDiagnosis("Updated Diagnosis");

        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    // Positive: Deletes a medical record successfully
    @Test
    @DisplayName("Should delete medical record")
    void testDeleteMedicalRecord_Success() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));

        ResponseEntity<?> response = medicalRecordController.deleteMedicalRecord(1L, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).deleteById(1L);
    }

    // Negative: Handles exceptions during medical record creation
    @Test
    @DisplayName("Should handle exception in create")
    void testCreateMedicalRecord_Exception() {
        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Negative: Handles exceptions when getting patient medical records
    @Test
    @DisplayName("Should handle exception in get patient records")
    void testGetPatientMedicalRecords_Exception() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(2L))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(2L, 2L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Negative: Handles exceptions during medical record update
    @Test
    @DisplayName("Should handle exception in update")
    void testUpdateMedicalRecord_Exception() {
        when(medicalRecordRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        MedicalRecord updates = new MedicalRecord();
        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Negative: Handles exceptions during medical record deletion
    @Test
    @DisplayName("Should handle exception in delete")
    void testDeleteMedicalRecord_Exception() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.deleteMedicalRecord(1L, 3L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Edge: Uses default user ID when null is provided
    @Test
    @DisplayName("Should use default userId when null")
    void testCreateMedicalRecord_DefaultUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    // Edge: Automatically sets doctor ID for doctor user when null
    @Test
    @DisplayName("Should set doctor ID automatically for doctor user")
    void testCreateMedicalRecord_AutoSetDoctorId() {
        testRecord.setDoctorId(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    // Negative: Rejects creation when doctor ID is missing for admin user
    @Test
    @DisplayName("Should reject record without doctor ID for admin")
    void testCreateMedicalRecord_MissingDoctorId_AsAdmin() {
        testRecord.setDoctorId(null);
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));

        ResponseEntity<?> response = medicalRecordController.createMedicalRecord(testRecord, 3L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(medicalRecordRepository, never()).save(any());
    }

    // Negative: Forbids patient from viewing other patient's records
    @Test
    @DisplayName("Should forbid patient from viewing other patient's records")
    void testGetPatientMedicalRecords_PatientAccessingOthers() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));

        // Patient 2 trying to access patient 999's records
        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(999L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).findByPatientIdOrderByRecordDateDesc(any());
    }

    // Positive: Allows admin to view patient records
    @Test
    @DisplayName("Should allow admin to view patient records")
    void testGetPatientMedicalRecords_AsAdmin() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(2L)).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(2L, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).findByPatientIdOrderByRecordDateDesc(2L);
    }

    // Negative: Returns unauthorized when user not found for patient records
    @Test
    @DisplayName("Should return unauthorized for patient records when user not found")
    void testGetPatientMedicalRecords_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.getPatientMedicalRecords(2L, 999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Forbids patient from viewing doctor records
    @Test
    @DisplayName("Should forbid patient from viewing doctor records")
    void testGetDoctorMedicalRecords_AsPatient_Forbidden() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));

        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).findByDoctorIdOrderByRecordDateDesc(any());
    }

    // Negative: Forbids doctor from viewing other doctor's records
    @Test
    @DisplayName("Should forbid doctor from viewing other doctor's records")
    void testGetDoctorMedicalRecords_DoctorAccessingOthers() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        // Doctor 1 trying to access doctor 999's records
        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(999L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).findByDoctorIdOrderByRecordDateDesc(any());
    }

    // Positive: Allows doctor to view own records
    @Test
    @DisplayName("Should allow doctor to view own records")
    void testGetDoctorMedicalRecords_OwnRecords() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(1L)).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(1L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).findByDoctorIdOrderByRecordDateDesc(1L);
    }

    // Positive: Allows admin to view doctor records
    @Test
    @DisplayName("Should allow admin to view doctor records")
    void testGetDoctorMedicalRecords_AsAdmin() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(1L)).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(1L, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).findByDoctorIdOrderByRecordDateDesc(1L);
    }

    // Negative: Returns unauthorized when user not found for doctor records
    @Test
    @DisplayName("Should return unauthorized for doctor records when user not found")
    void testGetDoctorMedicalRecords_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(1L, 999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Handles exceptions when getting doctor records
    @Test
    @DisplayName("Should handle exception in get doctor records")
    void testGetDoctorMedicalRecords_Exception() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));
        when(medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(1L))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.getDoctorMedicalRecords(1L, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Positive: Allows admin to get all medical records
    @Test
    @DisplayName("Should allow admin to get all records")
    void testGetAllMedicalRecords_AsAdmin() {
        List<MedicalRecord> records = Arrays.asList(testRecord);
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findAll()).thenReturn(records);

        ResponseEntity<?> response = medicalRecordController.getAllMedicalRecords(3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).findAll();
    }

    // Negative: Forbids non-admin from getting all records
    @Test
    @DisplayName("Should forbid non-admin from getting all records")
    void testGetAllMedicalRecords_AsDoctor_Forbidden() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        ResponseEntity<?> response = medicalRecordController.getAllMedicalRecords(1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).findAll();
    }

    // Negative: Returns unauthorized when user not found for get all records
    @Test
    @DisplayName("Should return unauthorized for get all when user not found")
    void testGetAllMedicalRecords_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.getAllMedicalRecords(999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Handles exceptions when getting all records
    @Test
    @DisplayName("Should handle exception in get all records")
    void testGetAllMedicalRecords_Exception() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.getAllMedicalRecords(3L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Negative: Forbids patient from viewing record by ID for others
    @Test
    @DisplayName("Should forbid patient from viewing record by ID")
    void testGetMedicalRecordById_PatientAccessingOthers() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));

        // Patient 2 trying to access patient 999's record (testRecord has patientId=2L)
        testRecord.setPatientId(999L);

        ResponseEntity<?> response = medicalRecordController.getMedicalRecordById(1L, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    // Negative: Returns unauthorized when user not found for get by ID
    @Test
    @DisplayName("Should return unauthorized for get by ID when user not found")
    void testGetMedicalRecordById_UserNotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.getMedicalRecordById(1L, 999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Handles exceptions when getting record by ID
    @Test
    @DisplayName("Should handle exception in get by ID")
    void testGetMedicalRecordById_Exception() {
        when(medicalRecordRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = medicalRecordController.getMedicalRecordById(1L, 1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // Negative: Forbids patient from updating medical record
    @Test
    @DisplayName("Should forbid patient from updating medical record")
    void testUpdateMedicalRecord_AsPatient_Forbidden() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patientUser));

        MedicalRecord updates = new MedicalRecord();
        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 2L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).save(any());
    }

    // Negative: Forbids doctor from updating other doctor's record
    @Test
    @DisplayName("Should forbid doctor from updating other doctor's record")
    void testUpdateMedicalRecord_DoctorUpdatingOthers() {
        testRecord.setDoctorId(999L); // Different doctor
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        MedicalRecord updates = new MedicalRecord();
        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).save(any());
    }

    // Positive: Allows admin to update any record
    @Test
    @DisplayName("Should allow admin to update any record")
    void testUpdateMedicalRecord_AsAdmin() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(testRecord);

        MedicalRecord updates = new MedicalRecord();
        updates.setDiagnosis("Admin Updated");

        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 3L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordRepository, times(1)).save(any(MedicalRecord.class));
    }

    // Negative: Returns 404 when updating non-existent record
    @Test
    @DisplayName("Should return 404 when updating non-existent record")
    void testUpdateMedicalRecord_NotFound() {
        when(medicalRecordRepository.findById(999L)).thenReturn(Optional.empty());

        MedicalRecord updates = new MedicalRecord();
        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(999L, updates, 1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Negative: Returns unauthorized when user not found for update
    @Test
    @DisplayName("Should return unauthorized for update when user not found")
    void testUpdateMedicalRecord_UserNotFound() {
        when(medicalRecordRepository.findById(1L)).thenReturn(Optional.of(testRecord));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        MedicalRecord updates = new MedicalRecord();
        ResponseEntity<?> response = medicalRecordController.updateMedicalRecord(1L, updates, 999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    // Negative: Forbids non-admin from deleting medical record
    @Test
    @DisplayName("Should forbid non-admin from deleting medical record")
    void testDeleteMedicalRecord_AsDoctor_Forbidden() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(doctorUser));

        ResponseEntity<?> response = medicalRecordController.deleteMedicalRecord(1L, 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(medicalRecordRepository, never()).deleteById(any());
    }

    // Negative: Returns 404 when deleting non-existent record
    @Test
    @DisplayName("Should return 404 when deleting non-existent record")
    void testDeleteMedicalRecord_NotFound() {
        when(userRepository.findById(3L)).thenReturn(Optional.of(adminUser));
        when(medicalRecordRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.deleteMedicalRecord(999L, 3L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(medicalRecordRepository, never()).deleteById(any());
    }

    // Negative: Returns unauthorized when user not found for delete
    @Test
    @DisplayName("Should return unauthorized for delete when user not found")
    void testDeleteMedicalRecord_UserNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = medicalRecordController.deleteMedicalRecord(1L, 999L);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
