package com.mediway.backend.dto.clinical;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class DiagnosisDto {
    private UUID diagnosisId;
    private UUID recordId;

    @NotBlank
    @Size(max = 64)
    private String code;

    @NotBlank
    private String description;

    private LocalDate onsetDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getDiagnosisId() { return diagnosisId; }
    public void setDiagnosisId(UUID diagnosisId) { this.diagnosisId = diagnosisId; }
    public UUID getRecordId() { return recordId; }
    public void setRecordId(UUID recordId) { this.recordId = recordId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getOnsetDate() { return onsetDate; }
    public void setOnsetDate(LocalDate onsetDate) { this.onsetDate = onsetDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}



