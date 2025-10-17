package com.mediway.backend.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.request.RegisterRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.dto.response.RegisterResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.service.PatientService;

@RestController
@RequestMapping("")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/patients/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = patientService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/patients/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = patientService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/patients/{healthId}")
    public ResponseEntity<User> getPatientByHealthId(@PathVariable String healthId) {
        Optional<User> user = patientService.findByHealthId(healthId);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}