package com.mediway.backend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mediway.backend.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {
    List<Prescription> findByMedicalRecord_RecordIdOrderByCreatedAtDesc(UUID recordId);

    @Query("select p from Prescription p where p.medicalRecord.patient.userId = :patientId order by p.createdAt desc")
    List<Prescription> findByPatient(UUID patientId);
}



