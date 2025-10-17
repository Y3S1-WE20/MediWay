package com.mediway.backend.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.service.DoctorService;

@RestController
@RequestMapping("")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private DoctorService doctorService;

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
        if (doctor.getPhoto() != null) {
            map.put("photo", "data:" + doctor.getPhotoContentType() + ";base64," + java.util.Base64.getEncoder().encodeToString(doctor.getPhoto()));
        }
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

    @PostMapping(value = "/doctors/with-photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Doctor> createDoctorWithPhoto(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("specialization") String specialization,
            @RequestParam("phone") String phone,
            @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setPassword(password);
        doctor.setSpecialization(specialization);
        doctor.setPhone(phone);
        doctor.setAvailable(true);
        if (photo != null && !photo.isEmpty()) {
            doctor.setPhoto(photo.getBytes());
            doctor.setPhotoContentType(photo.getContentType());
        }
        Doctor savedDoctor = doctorRepository.save(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    @PutMapping("/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        return doctorRepository.findById(id)
                .map(doctor -> {
                    doctor.setName(doctorDetails.getName());
                    doctor.setEmail(doctorDetails.getEmail());
                    doctor.setSpecialization(doctorDetails.getSpecialization());
                    doctor.setPhone(doctorDetails.getPhone());
                    doctor.setAvailable(doctorDetails.getAvailable());
                    return ResponseEntity.ok(doctorRepository.save(doctor));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping(value = "/doctors/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Doctor> updateDoctorPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photo) {
        try {
            return doctorRepository.findById(id)
                    .map(doctor -> {
                        try {
                            doctor.setPhoto(photo.getBytes());
                            doctor.setPhotoContentType(photo.getContentType());
                            return ResponseEntity.ok(doctorRepository.save(doctor));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw new RuntimeException("Failed to process photo", e);
            }
            throw e;
        }
    }

    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        if (doctorRepository.existsById(id)) {
            doctorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/doctors/login")
    public ResponseEntity<LoginResponse> loginDoctor(@RequestBody LoginRequest request) {
        LoginResponse response = doctorService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/doctors/{id}/appointments")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long id) {
        List<Map<String, Object>> appointments = doctorService.getAppointmentsByDoctor(id);
        return ResponseEntity.ok(appointments);
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