package com.mediway.backend.dto.clinical;

import com.mediway.backend.entity.Diagnosis;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Prescription;
import com.mediway.backend.entity.Treatment;

public final class ClinicalMappers {
    private ClinicalMappers() {}

    // Diagnosis
    public static DiagnosisDto toDto(Diagnosis e) {
        DiagnosisDto d = new DiagnosisDto();
        d.setDiagnosisId(e.getDiagnosisId());
        d.setRecordId(e.getMedicalRecord() != null ? e.getMedicalRecord().getRecordId() : null);
        d.setCode(e.getCode());
        d.setDescription(e.getDescription());
        d.setOnsetDate(e.getOnsetDate());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public static Diagnosis toEntity(DiagnosisDto d, MedicalRecord record) {
        Diagnosis e = new Diagnosis();
        e.setDiagnosisId(d.getDiagnosisId());
        e.setMedicalRecord(record);
        e.setCode(d.getCode());
        e.setDescription(d.getDescription());
        e.setOnsetDate(d.getOnsetDate());
        return e;
    }

    // Treatment
    public static TreatmentDto toDto(Treatment e) {
        TreatmentDto d = new TreatmentDto();
        d.setTreatmentId(e.getTreatmentId());
        d.setRecordId(e.getMedicalRecord() != null ? e.getMedicalRecord().getRecordId() : null);
        d.setType(e.getType());
        d.setDetails(e.getDetails());
        d.setStartDate(e.getStartDate());
        d.setEndDate(e.getEndDate());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public static Treatment toEntity(TreatmentDto d, MedicalRecord record) {
        Treatment e = new Treatment();
        e.setTreatmentId(d.getTreatmentId());
        e.setMedicalRecord(record);
        e.setType(d.getType());
        e.setDetails(d.getDetails());
        e.setStartDate(d.getStartDate());
        e.setEndDate(d.getEndDate());
        return e;
    }

    // Prescription
    public static PrescriptionDto toDto(Prescription e) {
        PrescriptionDto d = new PrescriptionDto();
        d.setPrescriptionId(e.getPrescriptionId());
        d.setRecordId(e.getMedicalRecord() != null ? e.getMedicalRecord().getRecordId() : null);
        d.setDrugName(e.getDrugName());
        d.setDosage(e.getDosage());
        d.setFrequency(e.getFrequency());
        d.setDurationDays(e.getDurationDays());
        d.setStartDate(e.getStartDate());
        d.setCreatedAt(e.getCreatedAt());
        d.setUpdatedAt(e.getUpdatedAt());
        return d;
    }

    public static Prescription toEntity(PrescriptionDto d, MedicalRecord record) {
        Prescription e = new Prescription();
        e.setPrescriptionId(d.getPrescriptionId());
        e.setMedicalRecord(record);
        e.setDrugName(d.getDrugName());
        e.setDosage(d.getDosage());
        e.setFrequency(d.getFrequency());
        e.setDurationDays(d.getDurationDays());
        e.setStartDate(d.getStartDate());
        return e;
    }
}



