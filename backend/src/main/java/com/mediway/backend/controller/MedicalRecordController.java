package com.mediway.backend.controller;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private UserRepository userRepository;

    // Create medical record (Doctor/Admin only)
    @PostMapping
    public ResponseEntity<?> createMedicalRecord(
            @RequestBody MedicalRecord medicalRecord,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check if user is doctor or admin
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            if (!user.getRole().equals(User.Role.DOCTOR) && !user.getRole().equals(User.Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Only doctors and admins can create medical records."
                ));
            }

            // Set doctor ID if user is doctor
            if (user.getRole().equals(User.Role.DOCTOR)) {
                medicalRecord.setDoctorId(userId);
            }

            // Validate required fields
            if (medicalRecord.getPatientId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Patient ID is required"
                ));
            }

            if (medicalRecord.getDoctorId() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Doctor ID is required"
                ));
            }

            medicalRecord.setRecordDate(LocalDateTime.now());
            MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record created successfully",
                "record", savedRecord
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error creating medical record: " + e.getMessage()
            ));
        }
    }

    // Get medical records for a patient (Doctor/Admin/Patient can view their own)
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientMedicalRecords(
            @PathVariable Long patientId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check access permissions
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            
            // Admin can view all, Doctor can view all, Patient can view only their own
            if (user.getRole().equals(User.Role.PATIENT) && !userId.equals(patientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Patients can only view their own medical records."
                ));
            }

            List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
            return ResponseEntity.ok(records);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching medical records: " + e.getMessage()
            ));
        }
    }

    // Get medical records created by a doctor (Doctor/Admin only)
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorMedicalRecords(
            @PathVariable Long doctorId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check access permissions
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            
            // Only admins and the doctor themselves can view doctor's records
            if (user.getRole().equals(User.Role.PATIENT) || 
                (user.getRole().equals(User.Role.DOCTOR) && !userId.equals(doctorId))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied."
                ));
            }

            List<MedicalRecord> records = medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(doctorId);
            return ResponseEntity.ok(records);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching medical records: " + e.getMessage()
            ));
        }
    }

    // Get all medical records (Admin only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllMedicalRecords(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check if user is admin
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            if (!user.getRole().equals(User.Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Only admins can view all medical records."
                ));
            }

            List<MedicalRecord> records = medicalRecordRepository.findAll();
            return ResponseEntity.ok(records);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching medical records: " + e.getMessage()
            ));
        }
    }

    // Get medical record by ID
    @GetMapping("/{recordId}")
    public ResponseEntity<?> getMedicalRecordById(
            @PathVariable Long recordId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
            if (recordOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MedicalRecord record = recordOpt.get();

            // Check access permissions
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            
            // Admin can view all, Doctor can view all, Patient can view only their own
            if (user.getRole().equals(User.Role.PATIENT) && !userId.equals(record.getPatientId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied."
                ));
            }

            return ResponseEntity.ok(record);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error fetching medical record: " + e.getMessage()
            ));
        }
    }

    // Update medical record (Doctor/Admin only - doctors can only update their own records)
    @PutMapping("/{recordId}")
    public ResponseEntity<?> updateMedicalRecord(
            @PathVariable Long recordId,
            @RequestBody MedicalRecord medicalRecord,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            Optional<MedicalRecord> existingRecordOpt = medicalRecordRepository.findById(recordId);
            if (existingRecordOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            MedicalRecord existingRecord = existingRecordOpt.get();

            // Check access permissions
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            
            if (user.getRole().equals(User.Role.PATIENT)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Patients cannot update medical records."
                ));
            }

            // Doctors can only update their own records, admins can update any
            if (user.getRole().equals(User.Role.DOCTOR) && !userId.equals(existingRecord.getDoctorId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Doctors can only update their own medical records."
                ));
            }

            // Update fields
            existingRecord.setDiagnosis(medicalRecord.getDiagnosis());
            existingRecord.setTreatment(medicalRecord.getTreatment());
            existingRecord.setPrescription(medicalRecord.getPrescription());
            existingRecord.setNotes(medicalRecord.getNotes());

            MedicalRecord updatedRecord = medicalRecordRepository.save(existingRecord);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record updated successfully",
                "record", updatedRecord
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error updating medical record: " + e.getMessage()
            ));
        }
    }

    // Delete medical record (Admin only)
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteMedicalRecord(
            @PathVariable Long recordId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }

            // Check if user is admin
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "User not found"
                ));
            }

            User user = userOpt.get();
            if (!user.getRole().equals(User.Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "Access denied. Only admins can delete medical records."
                ));
            }

            Optional<MedicalRecord> recordOpt = medicalRecordRepository.findById(recordId);
            if (recordOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            medicalRecordRepository.deleteById(recordId);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Medical record deleted successfully"
            ));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Error deleting medical record: " + e.getMessage()
            ));
        }
    }
}