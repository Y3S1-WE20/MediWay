package com.mediway.backend.controller;

import com.mediway.backend.entity.LabResult;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Prescription;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller for managing medical reports, records, prescriptions, and lab results
 */
@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ReportsController {

    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final LabResultRepository labResultRepository;
    private final AppointmentRepository appointmentRepository;

    private UUID getUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return user.getUserId();
    }

    /**
     * Get comprehensive patient report (all medical data)
     */
    @GetMapping("/patient/comprehensive")
    public ResponseEntity<Map<String, Object>> getComprehensivePatientReport(Authentication authentication) {
        UUID patientId = getUserIdFromAuthentication(authentication);
        log.info("Fetching comprehensive report for patient: {}", patientId);

        Map<String, Object> report = new HashMap<>();
        
        // Get patient info
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        Map<String, Object> patientInfo = new HashMap<>();
        patientInfo.put("userId", patient.getUserId());
        patientInfo.put("fullName", patient.getFullName());
        patientInfo.put("email", patient.getEmail());
        patientInfo.put("phone", patient.getPhone());
        
        report.put("patient", patientInfo);
        report.put("medicalRecords", medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId));
        report.put("prescriptions", prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId));
        report.put("labResults", labResultRepository.findByPatientIdOrderByResultDateDesc(patientId));
        report.put("appointments", appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId));
        report.put("generatedAt", LocalDate.now());

        return ResponseEntity.ok(report);
    }

    /**
     * Get medical records for patient
     */
    @GetMapping("/medical-records")
    public ResponseEntity<List<MedicalRecord>> getMyMedicalRecords(Authentication authentication) {
        UUID patientId = getUserIdFromAuthentication(authentication);
        log.info("Fetching medical records for patient: {}", patientId);
        
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
        return ResponseEntity.ok(records);
    }

    /**
     * Get specific medical record
     */
    @GetMapping("/medical-records/{recordId}")
    public ResponseEntity<MedicalRecord> getMedicalRecord(
            @PathVariable UUID recordId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        log.info("Fetching medical record: {} for user: {}", recordId, userId);
        
        MedicalRecord record = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found"));
        
        // Verify user has access to this record
        if (!record.getPatientId().equals(userId) && !record.getDoctorId().equals(userId)) {
            throw new IllegalArgumentException("Access denied to this medical record");
        }
        
        return ResponseEntity.ok(record);
    }

    /**
     * Create medical record (doctor only)
     */
    @PostMapping("/medical-records")
    public ResponseEntity<MedicalRecord> createMedicalRecord(
            @RequestBody MedicalRecord medicalRecord,
            Authentication authentication) {
        UUID doctorId = getUserIdFromAuthentication(authentication);
        log.info("Creating medical record by doctor: {}", doctorId);
        
        medicalRecord.setDoctorId(doctorId);
        medicalRecord.setRecordDate(LocalDate.now());
        
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created with ID: {}", savedRecord.getRecordId());
        
        return ResponseEntity.ok(savedRecord);
    }

    /**
     * Get prescriptions for patient
     */
    @GetMapping("/prescriptions")
    public ResponseEntity<List<Prescription>> getMyPrescriptions(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        UUID patientId = getUserIdFromAuthentication(authentication);
        log.info("Fetching prescriptions for patient: {}", patientId);
        
        List<Prescription> prescriptions;
        if (status != null && !status.isEmpty()) {
            Prescription.PrescriptionStatus prescriptionStatus = Prescription.PrescriptionStatus.valueOf(status.toUpperCase());
            prescriptions = prescriptionRepository.findByPatientIdAndStatus(patientId, prescriptionStatus);
        } else {
            prescriptions = prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId);
        }
        
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * Get specific prescription
     */
    @GetMapping("/prescriptions/{prescriptionId}")
    public ResponseEntity<Prescription> getPrescription(
            @PathVariable UUID prescriptionId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        log.info("Fetching prescription: {} for user: {}", prescriptionId, userId);
        
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        
        // Verify user has access
        if (!prescription.getPatientId().equals(userId) && !prescription.getDoctorId().equals(userId)) {
            throw new IllegalArgumentException("Access denied to this prescription");
        }
        
        return ResponseEntity.ok(prescription);
    }

    /**
     * Create prescription (doctor only)
     */
    @PostMapping("/prescriptions")
    public ResponseEntity<Prescription> createPrescription(
            @RequestBody Prescription prescription,
            Authentication authentication) {
        UUID doctorId = getUserIdFromAuthentication(authentication);
        log.info("Creating prescription by doctor: {}", doctorId);
        
        prescription.setDoctorId(doctorId);
        prescription.setPrescriptionDate(LocalDate.now());
        
        if (prescription.getStartDate() == null) {
            prescription.setStartDate(LocalDate.now());
        }
        
        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Prescription created with ID: {}", savedPrescription.getPrescriptionId());
        
        return ResponseEntity.ok(savedPrescription);
    }

    /**
     * Update prescription status
     */
    @PatchMapping("/prescriptions/{prescriptionId}/status")
    public ResponseEntity<Prescription> updatePrescriptionStatus(
            @PathVariable UUID prescriptionId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        UUID doctorId = getUserIdFromAuthentication(authentication);
        log.info("Updating prescription status: {} by doctor: {}", prescriptionId, doctorId);
        
        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Prescription not found"));
        
        if (!prescription.getDoctorId().equals(doctorId)) {
            throw new IllegalArgumentException("Only the prescribing doctor can update prescription status");
        }
        
        String statusStr = request.get("status");
        Prescription.PrescriptionStatus status = Prescription.PrescriptionStatus.valueOf(statusStr.toUpperCase());
        prescription.setStatus(status);
        
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        return ResponseEntity.ok(updatedPrescription);
    }

    /**
     * Get lab results for patient
     */
    @GetMapping("/lab-results")
    public ResponseEntity<List<LabResult>> getMyLabResults(
            @RequestParam(required = false) String status,
            Authentication authentication) {
        UUID patientId = getUserIdFromAuthentication(authentication);
        log.info("Fetching lab results for patient: {}", patientId);
        
        List<LabResult> labResults;
        if (status != null && !status.isEmpty()) {
            LabResult.LabResultStatus labStatus = LabResult.LabResultStatus.valueOf(status.toUpperCase());
            labResults = labResultRepository.findByPatientIdAndStatus(patientId, labStatus);
        } else {
            labResults = labResultRepository.findByPatientIdOrderByResultDateDesc(patientId);
        }
        
        return ResponseEntity.ok(labResults);
    }

    /**
     * Get specific lab result
     */
    @GetMapping("/lab-results/{resultId}")
    public ResponseEntity<LabResult> getLabResult(
            @PathVariable UUID resultId,
            Authentication authentication) {
        UUID userId = getUserIdFromAuthentication(authentication);
        log.info("Fetching lab result: {} for user: {}", resultId, userId);
        
        LabResult labResult = labResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab result not found"));
        
        // Verify user has access
        if (!labResult.getPatientId().equals(userId) && !labResult.getDoctorId().equals(userId)) {
            throw new IllegalArgumentException("Access denied to this lab result");
        }
        
        return ResponseEntity.ok(labResult);
    }

    /**
     * Create lab result (doctor only)
     */
    @PostMapping("/lab-results")
    public ResponseEntity<LabResult> createLabResult(
            @RequestBody LabResult labResult,
            Authentication authentication) {
        UUID doctorId = getUserIdFromAuthentication(authentication);
        log.info("Creating lab result by doctor: {}", doctorId);
        
        labResult.setDoctorId(doctorId);
        labResult.setResultDate(LocalDate.now());
        
        if (labResult.getTestDate() == null) {
            labResult.setTestDate(LocalDate.now());
        }
        
        LabResult savedLabResult = labResultRepository.save(labResult);
        log.info("Lab result created with ID: {}", savedLabResult.getResultId());
        
        return ResponseEntity.ok(savedLabResult);
    }

    /**
     * Update lab result status
     */
    @PatchMapping("/lab-results/{resultId}/status")
    public ResponseEntity<LabResult> updateLabResultStatus(
            @PathVariable UUID resultId,
            @RequestBody Map<String, String> request,
            Authentication authentication) {
        UUID doctorId = getUserIdFromAuthentication(authentication);
        log.info("Updating lab result status: {} by doctor: {}", resultId, doctorId);
        
        LabResult labResult = labResultRepository.findById(resultId)
                .orElseThrow(() -> new ResourceNotFoundException("Lab result not found"));
        
        if (!labResult.getDoctorId().equals(doctorId)) {
            throw new IllegalArgumentException("Only the ordering doctor can update lab result status");
        }
        
        String statusStr = request.get("status");
        LabResult.LabResultStatus status = LabResult.LabResultStatus.valueOf(statusStr.toUpperCase());
        labResult.setStatus(status);
        
        LabResult updatedLabResult = labResultRepository.save(labResult);
        return ResponseEntity.ok(updatedLabResult);
    }

    /**
     * Get report summary (statistics)
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportSummary(Authentication authentication) {
        UUID patientId = getUserIdFromAuthentication(authentication);
        log.info("Fetching report summary for patient: {}", patientId);

        Map<String, Object> summary = new HashMap<>();
        
        // Count totals
        summary.put("totalMedicalRecords", medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId).size());
        summary.put("totalPrescriptions", prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId).size());
        summary.put("totalLabResults", labResultRepository.findByPatientIdOrderByResultDateDesc(patientId).size());
        summary.put("totalAppointments", appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId).size());
        
        // Active prescriptions
        summary.put("activePrescriptions", 
            prescriptionRepository.findByPatientIdAndStatus(patientId, Prescription.PrescriptionStatus.ACTIVE).size());
        
        // Abnormal lab results
        summary.put("abnormalLabResults", 
            labResultRepository.findByPatientIdAndStatus(patientId, LabResult.LabResultStatus.ABNORMAL).size());
        
        summary.put("criticalLabResults", 
            labResultRepository.findByPatientIdAndStatus(patientId, LabResult.LabResultStatus.CRITICAL).size());

        return ResponseEntity.ok(summary);
    }
}
