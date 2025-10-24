package com.mediway.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.dto.request.MedicalRecordRequest;
import com.mediway.backend.dto.response.MedicalRecordResponse;
import com.mediway.backend.service.MedicalRecordService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "*")
public class MedicalRecordController {
    
    @Autowired
    private MedicalRecordService medicalRecordService;
    
    @PostMapping
    public ResponseEntity<MedicalRecordResponse> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecordResponse response = medicalRecordService.createMedicalRecord(request);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> updateMedicalRecord(
            @PathVariable UUID recordId,
            @Valid @RequestBody MedicalRecordRequest request) {
        MedicalRecordResponse response = medicalRecordService.updateMedicalRecord(recordId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{recordId}")
    public ResponseEntity<MedicalRecordResponse> getMedicalRecordById(@PathVariable UUID recordId) {
        MedicalRecordResponse response = medicalRecordService.getMedicalRecordById(recordId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByPatientId(
            @PathVariable UUID patientId) {
        List<MedicalRecordResponse> responses = medicalRecordService.getMedicalRecordsByPatientId(patientId);
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecordResponse>> getMedicalRecordsByDoctorId(
            @PathVariable UUID doctorId) {
        List<MedicalRecordResponse> responses = medicalRecordService.getMedicalRecordsByDoctorId(doctorId);
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deleteMedicalRecord(@PathVariable UUID recordId) {
        medicalRecordService.deleteMedicalRecord(recordId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    public ResponseEntity<List<MedicalRecordResponse>> getAllMedicalRecords() {
        List<MedicalRecordResponse> responses = medicalRecordService.getAllMedicalRecords();
        return ResponseEntity.ok(responses);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<MedicalRecordResponse>> searchMedicalRecords(
            @RequestParam String query) {
        List<MedicalRecordResponse> responses = medicalRecordService.searchMedicalRecords(query);
        return ResponseEntity.ok(responses);
    }
}