package com.mediway.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.dto.clinical.ClinicalMappers;
import com.mediway.backend.dto.clinical.DiagnosisDto;
import com.mediway.backend.dto.clinical.PrescriptionDto;
import com.mediway.backend.dto.clinical.TreatmentDto;
import com.mediway.backend.entity.Diagnosis;
import com.mediway.backend.entity.Prescription;
import com.mediway.backend.entity.Treatment;
import com.mediway.backend.service.ClinicalRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/clinical")
public class ClinicalRecordController {

    private final ClinicalRecordService service;

    public ClinicalRecordController(ClinicalRecordService service) {
        this.service = service;
    }

    // Diagnosis endpoints
    @PostMapping("/records/{recordId}/diagnoses")
    public ResponseEntity<DiagnosisDto> addDiagnosis(
            @PathVariable UUID recordId,
            @Valid @RequestBody DiagnosisDto payload) {
        Diagnosis saved = service.addDiagnosis(recordId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(saved));
    }

    @PutMapping("/diagnoses/{diagnosisId}")
    public ResponseEntity<DiagnosisDto> updateDiagnosis(
            @PathVariable UUID diagnosisId,
            @Valid @RequestBody DiagnosisDto payload) {
        Diagnosis updated = service.updateDiagnosis(diagnosisId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(updated));
    }

    @DeleteMapping("/diagnoses/{diagnosisId}")
    public ResponseEntity<Void> deleteDiagnosis(@PathVariable UUID diagnosisId) {
        service.deleteDiagnosis(diagnosisId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/records/{recordId}/diagnoses")
    public ResponseEntity<List<DiagnosisDto>> getDiagnoses(@PathVariable UUID recordId) {
        return ResponseEntity.ok(service.getDiagnosesByRecord(recordId).stream().map(ClinicalMappers::toDto).toList());
    }

    // Treatment endpoints
    @PostMapping("/records/{recordId}/treatments")
    public ResponseEntity<TreatmentDto> addTreatment(
            @PathVariable UUID recordId,
            @Valid @RequestBody TreatmentDto payload) {
        Treatment saved = service.addTreatment(recordId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(saved));
    }

    @PutMapping("/treatments/{treatmentId}")
    public ResponseEntity<TreatmentDto> updateTreatment(
            @PathVariable UUID treatmentId,
            @Valid @RequestBody TreatmentDto payload) {
        Treatment updated = service.updateTreatment(treatmentId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(updated));
    }

    @DeleteMapping("/treatments/{treatmentId}")
    public ResponseEntity<Void> deleteTreatment(@PathVariable UUID treatmentId) {
        service.deleteTreatment(treatmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/records/{recordId}/treatments")
    public ResponseEntity<List<TreatmentDto>> getTreatments(@PathVariable UUID recordId) {
        return ResponseEntity.ok(service.getTreatmentsByRecord(recordId).stream().map(ClinicalMappers::toDto).toList());
    }

    @GetMapping("/treatments/active")
    public ResponseEntity<List<TreatmentDto>> getActiveTreatments(@RequestParam("date") String date) {
        return ResponseEntity.ok(service.getActiveTreatments(LocalDate.parse(date)).stream().map(ClinicalMappers::toDto).toList());
    }

    // Prescription endpoints
    @PostMapping("/records/{recordId}/prescriptions")
    public ResponseEntity<PrescriptionDto> addPrescription(
            @PathVariable UUID recordId,
            @Valid @RequestBody PrescriptionDto payload) {
        Prescription saved = service.addPrescription(recordId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(saved));
    }

    @PutMapping("/prescriptions/{prescriptionId}")
    public ResponseEntity<PrescriptionDto> updatePrescription(
            @PathVariable UUID prescriptionId,
            @Valid @RequestBody PrescriptionDto payload) {
        Prescription updated = service.updatePrescription(prescriptionId, ClinicalMappers.toEntity(payload, null));
        return ResponseEntity.ok(ClinicalMappers.toDto(updated));
    }

    @DeleteMapping("/prescriptions/{prescriptionId}")
    public ResponseEntity<Void> deletePrescription(@PathVariable UUID prescriptionId) {
        service.deletePrescription(prescriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/records/{recordId}/prescriptions")
    public ResponseEntity<List<PrescriptionDto>> getPrescriptions(@PathVariable UUID recordId) {
        return ResponseEntity.ok(service.getPrescriptionsByRecord(recordId).stream().map(ClinicalMappers::toDto).toList());
    }
}


