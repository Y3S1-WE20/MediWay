package com.mediway.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediway.backend.entity.Diagnosis;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Prescription;
import com.mediway.backend.entity.Treatment;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.DiagnosisRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.PrescriptionRepository;
import com.mediway.backend.repository.TreatmentRepository;

@Service
@Transactional
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final TreatmentRepository treatmentRepository;
    private final PrescriptionRepository prescriptionRepository;

    public ClinicalRecordServiceImpl(
        MedicalRecordRepository medicalRecordRepository,
        DiagnosisRepository diagnosisRepository,
        TreatmentRepository treatmentRepository,
        PrescriptionRepository prescriptionRepository
    ) {
        this.medicalRecordRepository = medicalRecordRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.treatmentRepository = treatmentRepository;
        this.prescriptionRepository = prescriptionRepository;
    }

    private MedicalRecord getRecord(UUID recordId) {
        return medicalRecordRepository.findById(recordId)
            .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + recordId));
    }

    // Diagnosis
    @Override
    public Diagnosis addDiagnosis(UUID recordId, Diagnosis payload) {
        MedicalRecord record = getRecord(recordId);
        payload.setMedicalRecord(record);
        return diagnosisRepository.save(payload);
    }

    @Override
    public Diagnosis updateDiagnosis(UUID diagnosisId, Diagnosis payload) {
        Diagnosis existing = diagnosisRepository.findById(diagnosisId)
            .orElseThrow(() -> new ResourceNotFoundException("Diagnosis not found with id: " + diagnosisId));
        existing.setCode(payload.getCode());
        existing.setDescription(payload.getDescription());
        existing.setOnsetDate(payload.getOnsetDate());
        return diagnosisRepository.save(existing);
    }

    @Override
    public void deleteDiagnosis(UUID diagnosisId) {
        if (!diagnosisRepository.existsById(diagnosisId)) {
            throw new ResourceNotFoundException("Diagnosis not found with id: " + diagnosisId);
        }
        diagnosisRepository.deleteById(diagnosisId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Diagnosis> getDiagnosesByRecord(UUID recordId) {
        return diagnosisRepository.findByMedicalRecord_RecordIdOrderByCreatedAtDesc(recordId);
    }

    // Treatment
    @Override
    public Treatment addTreatment(UUID recordId, Treatment payload) {
        if (payload.getEndDate() != null && payload.getStartDate() != null && payload.getEndDate().isBefore(payload.getStartDate())) {
            throw new IllegalArgumentException("Treatment endDate cannot be before startDate");
        }
        MedicalRecord record = getRecord(recordId);
        payload.setMedicalRecord(record);
        return treatmentRepository.save(payload);
    }

    @Override
    public Treatment updateTreatment(UUID treatmentId, Treatment payload) {
        Treatment existing = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Treatment not found with id: " + treatmentId));
        if (payload.getEndDate() != null && payload.getStartDate() != null && payload.getEndDate().isBefore(payload.getStartDate())) {
            throw new IllegalArgumentException("Treatment endDate cannot be before startDate");
        }
        existing.setType(payload.getType());
        existing.setDetails(payload.getDetails());
        existing.setStartDate(payload.getStartDate());
        existing.setEndDate(payload.getEndDate());
        return treatmentRepository.save(existing);
    }

    @Override
    public void deleteTreatment(UUID treatmentId) {
        if (!treatmentRepository.existsById(treatmentId)) {
            throw new ResourceNotFoundException("Treatment not found with id: " + treatmentId);
        }
        treatmentRepository.deleteById(treatmentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Treatment> getTreatmentsByRecord(UUID recordId) {
        return treatmentRepository.findByMedicalRecord_RecordIdOrderByCreatedAtDesc(recordId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Treatment> getActiveTreatments(LocalDate onDate) {
        return treatmentRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(onDate, onDate);
    }

    // Prescription
    @Override
    public Prescription addPrescription(UUID recordId, Prescription payload) {
        if (payload.getDurationDays() == null || payload.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Prescription durationDays must be positive");
        }
        MedicalRecord record = getRecord(recordId);
        payload.setMedicalRecord(record);
        return prescriptionRepository.save(payload);
    }

    @Override
    public Prescription updatePrescription(UUID prescriptionId, Prescription payload) {
        Prescription existing = prescriptionRepository.findById(prescriptionId)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription not found with id: " + prescriptionId));
        if (payload.getDurationDays() != null && payload.getDurationDays() <= 0) {
            throw new IllegalArgumentException("Prescription durationDays must be positive");
        }
        existing.setDrugName(payload.getDrugName());
        existing.setDosage(payload.getDosage());
        existing.setFrequency(payload.getFrequency());
        existing.setDurationDays(payload.getDurationDays());
        existing.setStartDate(payload.getStartDate());
        return prescriptionRepository.save(existing);
    }

    @Override
    public void deletePrescription(UUID prescriptionId) {
        if (!prescriptionRepository.existsById(prescriptionId)) {
            throw new ResourceNotFoundException("Prescription not found with id: " + prescriptionId);
        }
        prescriptionRepository.deleteById(prescriptionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prescription> getPrescriptionsByRecord(UUID recordId) {
        return prescriptionRepository.findByMedicalRecord_RecordIdOrderByCreatedAtDesc(recordId);
    }
}



