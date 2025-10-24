package com.mediway.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mediway.backend.entity.Treatment;

public interface TreatmentRepository extends JpaRepository<Treatment, UUID> {
    List<Treatment> findByMedicalRecord_RecordIdOrderByCreatedAtDesc(UUID recordId);

    @Query("select t from Treatment t where t.medicalRecord.patient.userId = :patientId order by t.createdAt desc")
    List<Treatment> findByPatient(UUID patientId);

    List<Treatment> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);
}



