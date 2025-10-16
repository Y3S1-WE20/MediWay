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
 * Lab Result entity for storing patient laboratory test results
 */
@Entity
@Table(name = "lab_results", indexes = {
    @Index(name = "idx_lab_result_patient", columnList = "patient_id"),
    @Index(name = "idx_lab_result_doctor", columnList = "doctor_id"),
    @Index(name = "idx_lab_result_date", columnList = "test_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabResult {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "result_id", updatable = false, nullable = false)
    private UUID resultId;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "doctor_id", nullable = false)
    private UUID doctorId;

    @Column(name = "appointment_id")
    private UUID appointmentId;

    @Column(name = "medical_record_id")
    private UUID medicalRecordId;

    @Column(name = "test_name", nullable = false, length = 200)
    private String testName;

    @Column(name = "test_type", nullable = false, length = 100)
    private String testType; // e.g., "Blood Test", "X-Ray", "MRI", "CT Scan"

    @Column(name = "test_date", nullable = false)
    private LocalDate testDate;

    @Column(name = "result_date", nullable = false)
    private LocalDate resultDate;

    @Column(name = "result_value", columnDefinition = "TEXT")
    private String resultValue;

    @Column(name = "result_unit", length = 50)
    private String resultUnit; // e.g., "mg/dL", "mmol/L"

    @Column(name = "reference_range", length = 100)
    private String referenceRange; // e.g., "70-100"

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private LabResultStatus status = LabResultStatus.NORMAL;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "file_url", length = 500)
    private String fileUrl; // URL to uploaded result file/image

    @Column(name = "lab_technician", length = 100)
    private String labTechnician;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum LabResultStatus {
        NORMAL,
        ABNORMAL,
        CRITICAL,
        PENDING
    }
}
