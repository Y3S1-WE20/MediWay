package com.mediway.backend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public ResponseEntity<List<Map<String, Object>>> getPatientMedicalRecords(@PathVariable Long patientId) {
        List<?> rawList = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
        // Convert to List<Map<String, Object>>
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : rawList) {
            if (obj instanceof Map) {
                result.add((Map<String, Object>) obj);
            }
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Map<String, Object>>> getDoctorMedicalRecords(@PathVariable Long doctorId) {
        List<?> rawList = medicalRecordRepository.findByDoctorIdOrderByRecordDateDesc(doctorId);
        // Convert to List<Map<String, Object>>
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object obj : rawList) {
            if (obj instanceof Map) {
                result.add((Map<String, Object>) obj);
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<?> createMedicalRecord(
            @RequestBody MedicalRecord record,
            @org.springframework.web.bind.annotation.RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId != null) {
            record.setDoctorId(userId);
        }
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return ResponseEntity.ok(java.util.Map.of(
            "success", true,
            "message", "Medical record created successfully!",
            "id", savedRecord.getId(),
            "record", savedRecord
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMedicalRecord(@PathVariable Long id, @RequestBody MedicalRecord recordDetails) {
        return medicalRecordRepository.findById(id)
                .map(record -> {
                    record.setDiagnosis(recordDetails.getDiagnosis());
                    record.setTreatment(recordDetails.getTreatment());
                    record.setPrescription(recordDetails.getPrescription());
                    record.setNotes(recordDetails.getNotes());
                    MedicalRecord savedRecord = medicalRecordRepository.save(record);
                    return ResponseEntity.ok(java.util.Map.of(
                        "success", true,
                        "message", "Medical record updated successfully",
                        "id", savedRecord.getId(),
                        "record", savedRecord
                    ));
                })
                .orElse(ResponseEntity.status(404).body(java.util.Map.of(
                    "success", false,
                    "message", "Medical record not found"
                )));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long id) {
        if (!medicalRecordRepository.existsById(id)) {
            return ResponseEntity.status(404).body(java.util.Map.of(
                "success", false,
                "message", "Medical record not found"
            ));
        }
        medicalRecordRepository.deleteById(id);
        return ResponseEntity.ok(java.util.Map.of(
            "success", true,
            "message", "Medical record deleted successfully"
        ));
    }
}