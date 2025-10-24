package com.mediway.backend.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mediway.backend.dto.request.MedicalRecordRequest;
import com.mediway.backend.dto.response.MedicalRecordResponse;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.UserRepository;

@Service
@Transactional
public class MedicalRecordServiceImpl implements MedicalRecordService {
    
    @Autowired
    private MedicalRecordRepository medicalRecordRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    private Doctor resolveOrCreateDoctor(UUID providedId) {
        // If a Doctor exists with this UUID, use it
        return doctorRepository.findById(providedId).orElseGet(() -> {
            // Otherwise, try to find a User with this UUID and create a Doctor from it
            User u = userRepository.findById(providedId)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + providedId));
            Doctor d = new Doctor();
            d.setDoctorId(providedId);
            d.setName(u.getFullName());
            d.setEmail(u.getEmail());
            d.setSpecialization("General");
            d.setAvailable(true);
            return doctorRepository.save(d);
        });
    }

    @Override
    public MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request) {
        User patient = userRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + request.getPatientId()));
        
        Doctor doctor = resolveOrCreateDoctor(request.getDoctorId());
        
        MedicalRecord medicalRecord = new MedicalRecord(
                patient, 
                doctor, 
                request.getDiagnosis(), 
                request.getMedications(), 
                request.getNotes()
        );
        
        MedicalRecord savedRecord = medicalRecordRepository.save(medicalRecord);
        return convertToResponse(savedRecord);
    }
    
    @Override
    public MedicalRecordResponse updateMedicalRecord(UUID recordId, MedicalRecordRequest request) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + recordId));
        
        // Resolve provided doctor id (doctorId or userId) and verify ownership
        Doctor providedDoctor = resolveOrCreateDoctor(request.getDoctorId());
        if (!medicalRecord.getDoctor().getDoctorId().equals(providedDoctor.getDoctorId())) {
            throw new ResourceNotFoundException("Medical record not found or access denied");
        }
        
        medicalRecord.setDiagnosis(request.getDiagnosis());
        medicalRecord.setMedications(request.getMedications());
        medicalRecord.setNotes(request.getNotes());
        
        MedicalRecord updatedRecord = medicalRecordRepository.save(medicalRecord);
        return convertToResponse(updatedRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecordById(UUID recordId) {
        MedicalRecord medicalRecord = medicalRecordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical record not found with id: " + recordId));
        return convertToResponse(medicalRecord);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecordsByPatientId(UUID patientId) {
        if (!userRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found with id: " + patientId);
        }
        
        return medicalRecordRepository.findByPatientUserIdOrderByCreatedAtDesc(patientId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getMedicalRecordsByDoctorId(UUID doctorId) {
        if (!doctorRepository.existsById(doctorId)) {
            // Try to resolve by user table: find a user with same UUID, auto-link create a Doctor
            userRepository.findById(doctorId).ifPresentOrElse(u -> {
                // Create minimal Doctor record if missing
                com.mediway.backend.entity.Doctor d = new com.mediway.backend.entity.Doctor();
                d.setDoctorId(doctorId);
                d.setName(u.getFullName());
                d.setEmail(u.getEmail());
                d.setSpecialization("General");
                d.setAvailable(true);
                doctorRepository.save(d);
            }, () -> {
                throw new ResourceNotFoundException("Doctor not found with id: " + doctorId);
            });
        }
        
        return medicalRecordRepository.findByDoctorDoctorIdOrderByCreatedAtDesc(doctorId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deleteMedicalRecord(UUID recordId) {
        if (!medicalRecordRepository.existsById(recordId)) {
            throw new ResourceNotFoundException("Medical record not found with id: " + recordId);
        }
        medicalRecordRepository.deleteById(recordId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> getAllMedicalRecords() {
        return medicalRecordRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalRecordResponse> searchMedicalRecords(String query) {
        // This would typically involve a more complex search logic
        // For now, searching by patient name or diagnosis
        // You can enhance this with proper search implementation
        return medicalRecordRepository.findAll()
                .stream()
                .filter(record -> record.getPatient().getFullName().toLowerCase().contains(query.toLowerCase()) ||
                                 record.getDiagnosis().toLowerCase().contains(query.toLowerCase()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    private MedicalRecordResponse convertToResponse(MedicalRecord medicalRecord) {
        MedicalRecordResponse response = new MedicalRecordResponse();
        response.setRecordId(medicalRecord.getRecordId());
        response.setPatientId(medicalRecord.getPatient().getUserId());
        response.setPatientName(medicalRecord.getPatient().getFullName());
        response.setDoctorId(medicalRecord.getDoctor().getDoctorId());
        response.setDoctorName(medicalRecord.getDoctor().getName());
        response.setDiagnosis(medicalRecord.getDiagnosis());
        response.setMedications(medicalRecord.getMedications());
        response.setNotes(medicalRecord.getNotes());
        response.setCreatedAt(medicalRecord.getCreatedAt());
        response.setUpdatedAt(medicalRecord.getUpdatedAt());
        return response;
    }
}