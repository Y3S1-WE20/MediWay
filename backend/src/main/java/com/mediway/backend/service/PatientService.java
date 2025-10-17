package com.mediway.backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.request.RegisterRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.dto.response.RegisterResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;

@Service
public class PatientService {

    @Autowired
    private UserRepository userRepository;

    public RegisterResponse register(RegisterRequest request) {
        User user = new User();
        user.setName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setPhone(request.getPhone());
        user.setRole(request.getRole());
        userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setSuccess(true);
        response.setMessage("Registration successful");
        return response;
    }

    public LoginResponse login(LoginRequest request) {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        if (user.isPresent() && user.get().getPassword().equals(request.getPassword())) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setUserId(user.get().getId());
            response.setName(user.get().getName());
            response.setRole(user.get().getRole().name());
            return response;
        }
        throw new RuntimeException("Invalid credentials");
    }

    public Optional<User> findByHealthId(String healthId) {
        // Assuming healthId is the id
        try {
            Long id = Long.parseLong(healthId);
            return userRepository.findById(id);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
}