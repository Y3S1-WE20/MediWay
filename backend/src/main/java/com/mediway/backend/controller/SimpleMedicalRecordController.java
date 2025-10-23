package com.mediway.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.repository.MedicalRecordRepository;

@RestController
@RequestMapping("/medical-records")
public class SimpleMedicalRecordController {

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(@PathVariable Long id) {
        return medicalRecordRepository.findById(id)
                .map(record -> ResponseEntity.ok(record))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<?> createMedicalRecord(
            @RequestBody MedicalRecord record,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-User-Id", required = false) Long userId) {
        System.out.println("[DEBUG] Incoming MedicalRecord: " + record);
        System.out.println("[DEBUG] Incoming userId: " + userId);
        if (userId != null) {
            System.out.println("[DEBUG] Setting doctorId to userId: " + userId);
            record.setDoctorId(userId);
        } else {
            System.out.println("[DEBUG] No userId provided in header!");
        }
        System.out.println("[DEBUG] After doctorId set, MedicalRecord: " + record);
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return ResponseEntity.ok(java.util.Map.of(
            "success", true,
            "message", "Medical record created successfully!",
            "record", savedRecord
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord recordDetails) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setDiagnosis(recordDetails.getDiagnosis());
                    record.setTreatment(recordDetails.getTreatment());
                    record.setPrescription(recordDetails.getPrescription());
                    record.setNotes(recordDetails.getNotes());
                    return ResponseEntity.ok(medicalRecordRepository.save(record));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}