package com.mediway.backend.repository;

import com.mediway.backend.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    
    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(Long patientId);
    
    List<MedicalRecord> findByDoctorIdOrderByRecordDateDesc(Long doctorId);
    
    List<MedicalRecord> findByPatientIdAndDoctorIdOrderByRecordDateDesc(Long patientId, Long doctorId);
    
    List<MedicalRecord> findByAppointmentId(Long appointmentId);
}
