package com.mediway.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Medical Record entity for storing patient medical history
 */
@Entity
@Table(name = "medical_records", indexes = {
    @Index(name = "idx_medical_record_patient", columnList = "patient_id"),
    @Index(name = "idx_medical_record_doctor", columnList = "doctor_id"),
    @Index(name = "idx_medical_record_date", columnList = "record_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "record_id", updatable = false, nullable = false)
    private UUID recordId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "diagnosis", columnDefinition = "TEXT")
    private String diagnosis;

    @Column(name = "symptoms", columnDefinition = "TEXT")
    private String symptoms;

    @Column(name = "treatment", columnDefinition = "TEXT")
    private String treatment;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "vital_signs", columnDefinition = "TEXT")
    private String vitalSigns; // JSON format: {"bloodPressure": "120/80", "temperature": "98.6", ...}

    @Column(name = "follow_up_required")
    @Builder.Default
    private Boolean followUpRequired = false;

    @Column(name = "follow_up_date")
    private LocalDate followUpDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
