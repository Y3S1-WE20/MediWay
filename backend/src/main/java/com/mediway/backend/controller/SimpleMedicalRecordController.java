package com.mediway.backend.controller;

import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.repository.MedicalRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@CrossOrigin(origins = "http://localhost:5174")
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
    public ResponseEntity<MedicalRecord> createMedicalRecord(@RequestBody MedicalRecord record) {
        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        return ResponseEntity.ok(savedRecord);
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