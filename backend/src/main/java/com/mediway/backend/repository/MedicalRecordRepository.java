package com.mediway.backend.repository;

import com.mediway.backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {
    
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(UUID patientId);
    
    List<MedicalRecord> findByDoctorIdOrderByRecordDateDesc(UUID doctorId);
    
    List<MedicalRecord> findByPatientIdAndDoctorIdOrderByRecordDateDesc(UUID patientId, UUID doctorId);
    
    List<MedicalRecord> findByPatientIdAndRecordDateBetween(UUID patientId, LocalDate startDate, LocalDate endDate);
    
    List<MedicalRecord> findByAppointmentId(UUID appointmentId);
}
