package com.mediway.backend.dto.response;

import com.mediway.backend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    
    @Builder.Default
    private String tokenType = "Bearer";
    
    private UUID userId;
    private String fullName;
    private String email;
    private User.Role role;
    private LocalDateTime expiresAt;
}
