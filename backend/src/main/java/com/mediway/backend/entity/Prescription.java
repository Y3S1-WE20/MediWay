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
 * Prescription entity for managing patient prescriptions
 */
@Entity
@Table(name = "prescriptions", indexes = {
    @Index(name = "idx_prescription_patient", columnList = "patient_id"),
    @Index(name = "idx_prescription_doctor", columnList = "doctor_id"),
    @Index(name = "idx_prescription_date", columnList = "prescription_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "prescription_id", updatable = false, nullable = false)
    private UUID prescriptionId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "medical_record_id")
    private UUID medicalRecordId;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(name = "medication_name", nullable = false, length = 200)
    private String medicationName;

    @Column(name = "dosage", nullable = false, length = 100)
    private String dosage;

    @Column(name = "frequency", nullable = false, length = 100)
    private String frequency; // e.g., "Twice daily", "Every 8 hours"

    @Column(name = "duration", nullable = false, length = 100)
    private String duration; // e.g., "7 days", "2 weeks"

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PrescriptionStatus status = PrescriptionStatus.ACTIVE;

    @Column(name = "refills_allowed")
    @Builder.Default
    private Integer refillsAllowed = 0;

    @Column(name = "refills_remaining")
    @Builder.Default
    private Integer refillsRemaining = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum PrescriptionStatus {
        ACTIVE,
        COMPLETED,
        CANCELLED,
        EXPIRED
    }
}
