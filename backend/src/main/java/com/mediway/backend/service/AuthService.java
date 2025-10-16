package com.mediway.backend.service;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.request.RegisterRequest;
import com.mediway.backend.dto.response.AuthResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final QRCodeService qrCodeService;

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email " + request.getEmail() + " already exists");
        }

        // Create new user entity
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(request.getRole())
                .isActive(true)
                .build();

        // Save user to database first to get the generated userId
        User savedUser = userRepository.save(user);
        
        // Generate QR code for PATIENT role
        if (savedUser.getRole() == User.Role.PATIENT) {
            try {
                String qrCodeData = qrCodeService.generateQRCodeData(savedUser.getUserId(), savedUser.getEmail());
                savedUser.setQrCode(qrCodeData);
                savedUser = userRepository.save(savedUser);
                log.info("QR code generated for patient: {}", savedUser.getEmail());
            } catch (Exception e) {
                log.error("Failed to generate QR code for patient: {}", savedUser.getEmail(), e);
                // Continue without QR code - it can be generated later
            }
        }
        
        log.info("User registered successfully with ID: {}", savedUser.getUserId());

        // Generate JWT token
        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name());
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime() / 1000);

        // Build and return auth response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(savedUser.getUserId())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .expiresAt(expiresAt)
                .build();
    }

    /**
     * Authenticate user and generate JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for user: {}", request.getEmail());

        try {
            // Authenticate user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            log.error("Invalid credentials for user: {}", request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }

        // Fetch user details
        User user = userRepository.findByEmailAndIsActive(request.getEmail(), true)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or inactive"));

        log.info("User authenticated successfully: {}", user.getEmail());

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        
        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime() / 1000);

        // Build and return auth response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .expiresAt(expiresAt)
                .build();
    }
}
