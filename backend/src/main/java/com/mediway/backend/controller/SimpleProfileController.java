package com.mediway.backend.controller;

import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/profile")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<?> getProfile() {
        try {
            // For prototype, return first user or create a dummy response
            Optional<User> userOpt = userRepository.findById(1L);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Map<String, Object> response = new HashMap<>();
                response.put("id", user.getId());
                response.put("name", user.getName());
                response.put("email", user.getEmail());
                response.put("phone", user.getPhone());
                response.put("role", user.getRole().toString());
                response.put("dateOfBirth", user.getDateOfBirth());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("message", "No user profile found");
                return ResponseEntity.status(404).body(error);
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error fetching profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> request) {
        try {
            Long userId = 1L; // For prototype, use first user
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

            User updatedUser = userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Profile updated successfully");
            response.put("user", Map.of(
                "id", updatedUser.getId(),
                "name", updatedUser.getName(),
                "email", updatedUser.getEmail(),
                "phone", updatedUser.getPhone()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/qrcode")
    public ResponseEntity<?> getQRCode() {
        try {
            // For prototype, return a simple response
            Map<String, Object> response = new HashMap<>();
            response.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==");
            response.put("message", "QR code feature coming soon");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Error generating QR code: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
