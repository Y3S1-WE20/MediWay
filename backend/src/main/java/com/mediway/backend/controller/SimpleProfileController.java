package com.mediway.backend.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;

@RestController
@RequestMapping("/profile")
public class SimpleProfileController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<?> getProfile(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            // Use userId from header, or default to 1 for prototype
            if (userId == null) {
                userId = 1L;
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("phone", user.getPhone());
                response.put("role", user.getRole().toString());
                response.put("dateOfBirth", user.getDateOfBirth());
                response.put("gender", user.getGender());
                response.put("bloodType", user.getBloodType());
                response.put("profilePicture", user.getProfilePicture());
                response.put("address", user.getAddress());
                response.put("emergencyContact", user.getEmergencyContact());
                response.put("emergencyPhone", user.getEmergencyPhone());
                response.put("allergies", user.getAllergies());
                response.put("medications", user.getMedications());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "User not found with ID: " + userId);
                return ResponseEntity.status(404).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, Object> request) {
        try {
            // Use userId from header, or default to 1 for prototype
            if (userId == null) {
                userId = 1L;
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "User not found");
                return ResponseEntity.status(404).body(error);
            }

            User user = userOpt.get();
            
            // Update fields if provided
            if (request.containsKey("name")) {
                user.setName((String) request.get("name"));
            }
            if (request.containsKey("phone")) {
                user.setPhone((String) request.get("phone"));
            }
            if (request.containsKey("dateOfBirth")) {
                user.setDateOfBirth(LocalDate.parse((String) request.get("dateOfBirth")));
            }
            if (request.containsKey("gender")) {
                user.setGender((String) request.get("gender"));
            }
            if (request.containsKey("bloodType")) {
                user.setBloodType((String) request.get("bloodType"));
            }
            if (request.containsKey("address")) {
                user.setAddress((String) request.get("address"));
            }
            if (request.containsKey("emergencyContact")) {
                user.setEmergencyContact((String) request.get("emergencyContact"));
            }
            if (request.containsKey("emergencyPhone")) {
                user.setEmergencyPhone((String) request.get("emergencyPhone"));
            }
            if (request.containsKey("allergies")) {
                user.setAllergies((String) request.get("allergies"));
            }
            if (request.containsKey("medications")) {
                user.setMedications((String) request.get("medications"));
            }
            if (request.containsKey("profilePicture")) {
                user.setProfilePicture((String) request.get("profilePicture"));
            }

            User updatedUser = userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("id", updatedUser.getId());
            response.put("name", updatedUser.getName());
            response.put("email", updatedUser.getEmail());
            response.put("phone", updatedUser.getPhone());
            response.put("dateOfBirth", updatedUser.getDateOfBirth());
            response.put("gender", updatedUser.getGender());
            response.put("bloodType", updatedUser.getBloodType());
            response.put("profilePicture", updatedUser.getProfilePicture());
            response.put("address", updatedUser.getAddress());
            response.put("emergencyContact", updatedUser.getEmergencyContact());
            response.put("emergencyPhone", updatedUser.getEmergencyPhone());
            response.put("allergies", updatedUser.getAllergies());
            response.put("medications", updatedUser.getMedications());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody Map<String, String> request) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Current and new passwords are required"));
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }
            
            User user = userOpt.get();
            
            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                return ResponseEntity.status(400).body(Map.of("message", "Current password is incorrect"));
            }
            
            // Update password
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password changed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Error changing password: " + e.getMessage()));
        }
    }

    @GetMapping("/qrcode")
    public ResponseEntity<?> getQRCode(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "User not found"));
            }
            
            User user = userOpt.get();
            
            // Generate or retrieve QR code
            String qrCodeData;
            if (user.getQrCode() != null && !user.getQrCode().isEmpty()) {
                qrCodeData = user.getQrCode();
            } else {
                // Generate QR code with patient information
                String qrContent = String.format("MEDIWAY-PATIENT:%d:%s:%s", 
                    user.getId(), 
                    user.getName(), 
                    user.getEmail()
                );
                qrCodeData = generateQRCodeImage(qrContent, 300, 300);
                
                // Save QR code to database
                user.setQrCode(qrCodeData);
                userRepository.save(user);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("qrCodeImage", qrCodeData);
            response.put("patientId", "PAT-" + user.getId());
            response.put("patientName", user.getName());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error generating QR code: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    private String generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "PNG", outputStream);
        
        byte[] imageBytes = outputStream.toByteArray();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        
        return "data:image/png;base64," + base64Image;
    }
}
