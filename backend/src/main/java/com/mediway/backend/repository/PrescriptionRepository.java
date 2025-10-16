package com.mediway.backend.repository;

import com.mediway.backend.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId);
    
    List<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(UUID doctorId);
    
    List<Prescription> findByPatientIdAndStatus(UUID patientId, Prescription.PrescriptionStatus status);
    
    List<Prescription> findByPatientIdAndPrescriptionDateBetween(UUID patientId, LocalDate startDate, LocalDate endDate);
    
    List<Prescription> findByAppointmentId(UUID appointmentId);
    
    List<Prescription> findByMedicalRecordId(UUID medicalRecordId);
}
