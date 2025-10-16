package com.mediway.backend.controller;

import com.google.zxing.WriterException;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Controller for managing user profiles and QR codes
 */
@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ProfileController {

    private final UserRepository userRepository;
    private final QRCodeService qrCodeService;

    /**
     * Get user profile information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProfile(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", user.getUserId());
        profile.put("fullName", user.getFullName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("role", user.getRole());
        profile.put("isActive", user.getIsActive());
        profile.put("createdAt", user.getCreatedAt());

        // Include QR code data if user is a patient
        if (user.getRole() == User.Role.PATIENT && user.getQrCode() != null) {
            profile.put("qrCodeData", user.getQrCode());
        }

        return ResponseEntity.ok(profile);
    }

    /**
     * Get QR code image for patient
     */
    @GetMapping("/qrcode")
    public ResponseEntity<Map<String, String>> getQRCode(Authentication authentication) {
        String email = authentication.getName();
        log.info("Fetching QR code for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getRole() != User.Role.PATIENT) {
            throw new IllegalArgumentException("QR codes are only available for patients");
        }

        try {
            // Generate QR code data if not exists
            String qrCodeData = user.getQrCode();
            if (qrCodeData == null || qrCodeData.isEmpty()) {
                qrCodeData = qrCodeService.generateQRCodeData(user.getUserId(), user.getEmail());
                user.setQrCode(qrCodeData);
                userRepository.save(user);
                log.info("Generated new QR code for patient: {}", email);
            }

            // Generate QR code image
            String qrCodeImage = qrCodeService.generateQRCodeImage(qrCodeData);

            Map<String, String> response = new HashMap<>();
            response.put("qrCodeData", qrCodeData);
            response.put("qrCodeImage", "data:image/png;base64," + qrCodeImage);
            
            return ResponseEntity.ok(response);
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code image for user: {}", email, e);
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }

    /**
     * Verify QR code and get patient information
     */
    @PostMapping("/verify-qr")
    public ResponseEntity<Map<String, Object>> verifyQRCode(@RequestBody Map<String, String> request) {
        String qrCodeData = request.get("qrCodeData");
        log.info("Verifying QR code");

        if (!qrCodeService.validateQRCodeData(qrCodeData)) {
            throw new IllegalArgumentException("Invalid QR code format");
        }

        UUID userId = qrCodeService.extractUserIdFromQRCode(qrCodeData);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if (user.getRole() != User.Role.PATIENT) {
            throw new IllegalArgumentException("QR code does not belong to a patient");
        }

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getUserId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("isActive", user.getIsActive());
        response.put("verified", true);

        return ResponseEntity.ok(response);
    }

    /**
     * Update user profile
     */
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody Map<String, String> updates,
            Authentication authentication) {
        String email = authentication.getName();
        log.info("Updating profile for user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Update allowed fields
        if (updates.containsKey("fullName")) {
            user.setFullName(updates.get("fullName"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone"));
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user: {}", email);

        Map<String, Object> response = new HashMap<>();
        response.put("userId", updatedUser.getUserId());
        response.put("fullName", updatedUser.getFullName());
        response.put("email", updatedUser.getEmail());
        response.put("phone", updatedUser.getPhone());
        response.put("role", updatedUser.getRole());
        response.put("message", "Profile updated successfully");

        return ResponseEntity.ok(response);
    }
}
