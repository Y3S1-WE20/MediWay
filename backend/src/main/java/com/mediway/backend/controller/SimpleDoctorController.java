package com.mediway.backend.controller;

import com.mediway.backend.entity.Doctor;
import com.mediway.backend.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleDoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // Helper method to convert Doctor to frontend format
    private Map<String, Object> doctorToMap(Doctor doctor) {
        Map<String, Object> map = new HashMap<>();
        map.put("doctorId", doctor.getId()); // Frontend expects "doctorId"
        map.put("id", doctor.getId());
        map.put("name", doctor.getName());
        map.put("specialization", doctor.getSpecialization());
        map.put("email", doctor.getEmail());
        map.put("phone", doctor.getPhone());
        map.put("available", doctor.getAvailable());
        map.put("consultationFee", 500.00); // Default fee for prototype
        map.put("experience", 5); // Default experience for prototype
        return map;
    }

    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> doctorMaps = doctors.stream()
                .map(this::doctorToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorMaps);
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        return doctorRepository.findById(id)
                .map(doctor -> ResponseEntity.ok(doctorToMap(doctor)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/doctors/available")
    public ResponseEntity<?> getAvailableDoctors() {
        List<Doctor> doctors = doctorRepository.findByAvailableTrue();
        List<Map<String, Object>> doctorMaps = doctors.stream()
                .map(this::doctorToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorMaps);
    }

    @PostMapping("/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor savedDoctor = doctorRepository.save(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    // Frontend expects doctors at /api/appointments/doctors
    @GetMapping("/appointments/doctors")
    public ResponseEntity<?> getDoctorsForAppointments() {
        List<Doctor> doctors = doctorRepository.findAll();
        List<Map<String, Object>> doctorMaps = doctors.stream()
                .map(this::doctorToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(doctorMaps);
    }
}