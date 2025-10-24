package com.mediway.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mediway.backend.entity.Diagnosis;

public interface DiagnosisRepository extends JpaRepository<Diagnosis, UUID> {
    List<Diagnosis> findByMedicalRecord_RecordIdOrderByCreatedAtDesc(UUID recordId);

    @Query("select d from Diagnosis d where d.medicalRecord.patient.userId = :patientId order by d.createdAt desc")
    List<Diagnosis> findByPatient(UUID patientId);

    List<Diagnosis> findByOnsetDateBetween(LocalDate start, LocalDate end);
}



