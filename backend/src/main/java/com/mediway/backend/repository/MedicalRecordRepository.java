package com.mediway.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mediway.backend.entity.MedicalRecord;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    
    // Fixed method names - using correct field names
    List<MedicalRecord> findByPatientUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<MedicalRecord> findByDoctorDoctorIdOrderByCreatedAtDesc(UUID doctorId);
    
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.userId = :patientId AND mr.doctor.doctorId = :doctorId ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByPatientAndDoctor(@Param("patientId") UUID patientId, @Param("doctorId") UUID doctorId);
    
    boolean existsByRecordIdAndDoctorDoctorId(UUID recordId, UUID doctorId);
}