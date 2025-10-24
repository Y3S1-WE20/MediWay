package com.mediway.backend.service;

import java.util.List;
import java.util.UUID;

import com.mediway.backend.dto.request.MedicalRecordRequest;
import com.mediway.backend.dto.response.MedicalRecordResponse;

public interface MedicalRecordService {
    
    MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request);
    
    MedicalRecordResponse updateMedicalRecord(UUID recordId, MedicalRecordRequest request);
    
    MedicalRecordResponse getMedicalRecordById(UUID recordId);
    
    List<MedicalRecordResponse> getMedicalRecordsByPatientId(UUID patientId);
    
    List<MedicalRecordResponse> getMedicalRecordsByDoctorId(UUID doctorId);
    
    void deleteMedicalRecord(UUID recordId);
    
    List<MedicalRecordResponse> getAllMedicalRecords();
    
    List<MedicalRecordResponse> searchMedicalRecords(String query);
}