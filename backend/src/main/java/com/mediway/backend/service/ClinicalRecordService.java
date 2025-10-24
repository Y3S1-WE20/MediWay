package com.mediway.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.mediway.backend.entity.Diagnosis;
import com.mediway.backend.entity.Prescription;
import com.mediway.backend.entity.Treatment;

public interface ClinicalRecordService {
    // Diagnosis
    Diagnosis addDiagnosis(UUID recordId, Diagnosis payload);
    Diagnosis updateDiagnosis(UUID diagnosisId, Diagnosis payload);
    void deleteDiagnosis(UUID diagnosisId);
    List<Diagnosis> getDiagnosesByRecord(UUID recordId);

    // Treatment
    Treatment addTreatment(UUID recordId, Treatment payload);
    Treatment updateTreatment(UUID treatmentId, Treatment payload);
    void deleteTreatment(UUID treatmentId);
    List<Treatment> getTreatmentsByRecord(UUID recordId);
    List<Treatment> getActiveTreatments(LocalDate onDate);

    // Prescription
    Prescription addPrescription(UUID recordId, Prescription payload);
    Prescription updatePrescription(UUID prescriptionId, Prescription payload);
    void deletePrescription(UUID prescriptionId);
    List<Prescription> getPrescriptionsByRecord(UUID recordId);
}



