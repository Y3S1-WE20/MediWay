package com.mediway.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorResponse {

    private UUID doctorId;
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String qualification;
    private Integer experienceYears;
    private BigDecimal consultationFee;
    private Boolean available;
}
