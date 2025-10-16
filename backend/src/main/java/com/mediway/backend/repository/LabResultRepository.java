package com.mediway.backend.repository;

import com.mediway.backend.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LabResultRepository extends JpaRepository<LabResult, UUID> {
    
    List<LabResult> findByPatientIdOrderByResultDateDesc(UUID patientId);
    
    List<LabResult> findByDoctorIdOrderByResultDateDesc(UUID doctorId);
    
    List<LabResult> findByPatientIdAndStatus(UUID patientId, LabResult.LabResultStatus status);
    
    List<LabResult> findByPatientIdAndResultDateBetween(UUID patientId, LocalDate startDate, LocalDate endDate);
    
    List<LabResult> findByAppointmentId(UUID appointmentId);
    
    List<LabResult> findByMedicalRecordId(UUID medicalRecordId);
}
