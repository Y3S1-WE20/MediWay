package com.mediway.backend.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    public Doctor create(String name, String email, String specialization, MultipartFile photo) throws Exception {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setEmail(email);
        doctor.setSpecialization(specialization);
        doctor.setAvailable(true);

        if (photo != null && !photo.isEmpty()) {
            doctor.setPhoto(photo.getBytes());
            doctor.setPhotoContentType(photo.getContentType());
        }

        return doctorRepository.save(doctor);
    }

    public List<Doctor> list() {
        return doctorRepository.findAll();
    }

    public Doctor get(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor update(Long id, String name, String email, String specialization, MultipartFile photo) throws Exception {
        Doctor doctor = get(id);

        if (name != null && !name.trim().isEmpty()) doctor.setName(name);
        if (email != null && !email.trim().isEmpty()) doctor.setEmail(email);
        if (specialization != null && !specialization.trim().isEmpty()) doctor.setSpecialization(specialization);

        if (photo != null && !photo.isEmpty()) {
            doctor.setPhoto(photo.getBytes());
            doctor.setPhotoContentType(photo.getContentType());
        }

        return doctorRepository.save(doctor);
    }

    public void delete(Long id) {
        doctorRepository.deleteById(id);
    }

    public void setPassword(Long id, String password) {
        Doctor doctor = get(id);
        doctor.setPassword(password); // Assuming Doctor has password field
        doctorRepository.save(doctor);
    }

    public Doctor login(String email, String password) {
        Optional<Doctor> doctor = doctorRepository.findByEmail(email);
        if (doctor.isPresent() && password.equals(doctor.get().getPassword())) {
            return doctor.get();
        }
        throw new RuntimeException("Invalid credentials");
    }

    public List<Appointment> appointmentsByDoctor(Long doctorId) {
        // Assuming Appointment has doctorId field
        return appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Doctor> doctor = doctorRepository.findByEmail(request.getEmail());
        if (doctor.isPresent() && request.getPassword().equals(doctor.get().getPassword())) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setUserId(doctor.get().getId());
            response.setName(doctor.get().getName());
            response.setRole("DOCTOR");
            return response;
        }
        throw new RuntimeException("Invalid credentials");
    }

    public List<Map<String, Object>> getAppointmentsByDoctor(Long doctorId) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(doctorId);
        return appointments.stream().map(appointment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", appointment.getId());
            map.put("patientId", appointment.getPatientId());
            map.put("date", appointment.getAppointmentDate().toLocalDate());
            map.put("time", appointment.getAppointmentDate().toLocalTime());
            map.put("status", appointment.getStatus().name());
            map.put("notes", appointment.getNotes());
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}