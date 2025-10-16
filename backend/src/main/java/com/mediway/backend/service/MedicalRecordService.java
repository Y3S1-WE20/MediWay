package com.mediway.backend.service;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing patient medical records
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final UserRepository userRepository;

    /**
     * Get all medical records for a patient
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> getPatientMedicalRecords(UUID patientId) {
        log.info("Fetching medical records for patient: {}", patientId);
        return medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
    }

    /**
     * Get all medical records created by a doctor
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> getDoctorMedicalRecords(UUID doctorId) {
        log.info("Fetching medical records for doctor: {}", doctorId);
        return medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(doctorId);
    }

    /**
     * Get medical records for a specific patient-doctor relationship
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> getPatientDoctorMedicalRecords(UUID patientId, UUID doctorId) {
        log.info("Fetching medical records for patient: {} and doctor: {}", patientId, doctorId);
        return medicalRecordRepository.findByPatientIdAndDoctorIdOrderByRecordDateDesc(patientId, doctorId);
    }

    /**
     * Get medical record by ID
     */
    @Transactional(readOnly = true)
    public MedicalRecord getMedicalRecordById(UUID recordId) {
        log.info("Fetching medical record: {}", recordId);
        return medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + recordId));
    }

    /**
     * Create a new medical record
     */
    @Transactional
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        log.info("Creating medical record for patient: {}", medicalRecord.getPatientId());

        // Validate patient exists
        userRepository.findById(medicalRecord.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Validate doctor exists
        userRepository.findById(medicalRecord.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        log.info("Medical record created with ID: {}", savedRecord.getRecordId());
        
        return savedRecord;
    }

    /**
     * Update an existing medical record
     */
    @Transactional
    public MedicalRecord updateMedicalRecord(UUID recordId, MedicalRecord updatedRecord) {
        log.info("Updating medical record: {}", recordId);

        MedicalRecord existingRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with ID: " + recordId));

        // Update fields
        if (updatedRecord.getDiagnosis() != null) {
            existingRecord.setDiagnosis(updatedRecord.getDiagnosis());
        }
        if (updatedRecord.getSymptoms() != null) {
            existingRecord.setSymptoms(updatedRecord.getSymptoms());
        }
        if (updatedRecord.getTreatment() != null) {
            existingRecord.setTreatment(updatedRecord.getTreatment());
        }
        if (updatedRecord.getNotes() != null) {
            existingRecord.setNotes(updatedRecord.getNotes());
        }
        if (updatedRecord.getVitalSigns() != null) {
            existingRecord.setVitalSigns(updatedRecord.getVitalSigns());
        }
        if (updatedRecord.getFollowUpRequired() != null) {
            existingRecord.setFollowUpRequired(updatedRecord.getFollowUpRequired());
        }
        if (updatedRecord.getFollowUpDate() != null) {
            existingRecord.setFollowUpDate(updatedRecord.getFollowUpDate());
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(existingRecord);
        log.info("Medical record updated: {}", recordId);
        
        return savedRecord;
    }

    /**
     * Delete a medical record
     */
    @Transactional
    public void deleteMedicalRecord(UUID recordId) {
        log.info("Deleting medical record: {}", recordId);
        
        if (!medicalRecordRepository.existsById(recordId)) {
            throw new ResourceNotFoundException("Medical record not found with ID: " + recordId);
        }
        
        medicalRecordRepository.deleteById(recordId);
        log.info("Medical record deleted: {}", recordId);
    }

    /**
     * Get medical records within a date range
     */
    @Transactional(readOnly = true)
    public List<MedicalRecord> getMedicalRecordsByDateRange(UUID patientId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching medical records for patient: {} between {} and {}", patientId, startDate, endDate);
        return medicalRecordRepository.findByPatientIdAndRecordDateBetween(patientId, startDate, endDate);
    }
}
