package com.mediway.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.request.RegisterRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.dto.response.RegisterResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.service.PatientService;

@RestController
@RequestMapping("")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private UserRepository userRepository;

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

    // CRUD endpoints for patient management
    @GetMapping("/patients")
    public ResponseEntity<List<User>> getAllPatients() {
        List<User> patients = userRepository.findAll();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<User> getPatientById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/patients")
    public ResponseEntity<User> createPatient(@RequestBody User patient) {
        patient.setRole(User.Role.PATIENT);
        User saved = userRepository.save(patient);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<User> updatePatient(@PathVariable Long id, @RequestBody User patientDetails) {
        Optional<User> existingPatient = userRepository.findById(id);
        if (existingPatient.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User patient = existingPatient.get();
        if (patientDetails.getName() != null) patient.setName(patientDetails.getName());
        if (patientDetails.getEmail() != null) patient.setEmail(patientDetails.getEmail());
        if (patientDetails.getPhone() != null) patient.setPhone(patientDetails.getPhone());
        if (patientDetails.getAddress() != null) patient.setAddress(patientDetails.getAddress());
        if (patientDetails.getGender() != null) patient.setGender(patientDetails.getGender());
        if (patientDetails.getDateOfBirth() != null) patient.setDateOfBirth(patientDetails.getDateOfBirth());
        if (patientDetails.getBloodType() != null) patient.setBloodType(patientDetails.getBloodType());
        
        User updated = userRepository.save(patient);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/patients/search")
    public ResponseEntity<List<User>> searchPatientsByName(@RequestParam String name) {
        List<User> allPatients = userRepository.findAll();
        List<User> matchingPatients = allPatients.stream()
                .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(matchingPatients);
    }

    @GetMapping("/patients/gender/{gender}")
    public ResponseEntity<List<User>> getPatientsByGender(@PathVariable String gender) {
        List<User> allPatients = userRepository.findAll();
        List<User> matchingPatients = allPatients.stream()
                .filter(p -> gender.equalsIgnoreCase(p.getGender()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(matchingPatients);
    }

    // Legacy endpoints (keep for backward compatibility)
    @GetMapping("/patients/health/{healthId}")
    public ResponseEntity<User> getPatientByHealthId(@PathVariable String healthId) {
        Optional<User> user = patientService.findByHealthId(healthId);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/patients/batch")
    public ResponseEntity<List<User>> getPatientsByIds(@RequestParam("ids") List<Long> ids) {
        List<User> patients = patientService.findByIds(ids);
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/api/patients/{id}")
    public ResponseEntity<User> getPatientByIdApi(@PathVariable Long id) {
        Optional<User> user = patientService.findById(id);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}