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
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleAuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        try {
            // Handle both "name" and "fullName" from frontend
            String name = request.get("name");
            if (name == null || name.trim().isEmpty()) {
                name = request.get("fullName");
            }
            String email = request.get("email");
            String password = request.get("password");
            String phone = request.get("phone");

            // Check if user already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "User with this email already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password); // In production, you should hash this!
            user.setPhone(phone);
            user.setRole(User.Role.PATIENT);

            User savedUser = userRepository.save(user);

            // Return success response with fields frontend expects
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Registration successful");
            response.put("userId", savedUser.getId());
            response.put("fullName", savedUser.getName()); // Frontend expects "fullName"
            response.put("email", savedUser.getEmail());
            response.put("role", savedUser.getRole().toString());
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "name", savedUser.getName(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole().toString()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(error);
            }

            User user = userOpt.get();

            // Simple password check (in production, use proper password hashing!)
            if (!user.getPassword().equals(password)) {
                Map<String, Object> error = new HashMap<>();
                error.put("success", false);
                error.put("message", "Invalid email or password");
                return ResponseEntity.status(401).body(error);
            }

            // Return success response with user data
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("token", "simple-token-" + user.getId()); // Simple token for prototype
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "role", user.getRole().toString()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("message", "Auth service is running");
        return ResponseEntity.ok(response);
    }
}
