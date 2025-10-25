package com.mediway.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Admin;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AdminRepository;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;

@Service
public class AdminService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    // --- USER MANAGEMENT ---
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setPhone(userDetails.getPhone());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setGender(userDetails.getGender());
        user.setBloodType(userDetails.getBloodType());
        user.setProfilePicture(userDetails.getProfilePicture());
        user.setAddress(userDetails.getAddress());
        user.setEmergencyContact(userDetails.getEmergencyContact());
        user.setEmergencyPhone(userDetails.getEmergencyPhone());
        user.setAllergies(userDetails.getAllergies());
        user.setMedications(userDetails.getMedications());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // --- DOCTOR MANAGEMENT ---
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElseThrow(() -> new RuntimeException("Doctor not found"));
    }

    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = getDoctorById(id);
        doctor.setName(doctorDetails.getName());
        doctor.setEmail(doctorDetails.getEmail());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setPhone(doctorDetails.getPhone());
        doctor.setPhoto(doctorDetails.getPhoto());
        doctor.setPhotoContentType(doctorDetails.getPhotoContentType());
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    // --- APPOINTMENT MANAGEMENT ---
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
    }

    public Appointment updateAppointmentStatus(Long id, String status, String reason) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(Appointment.Status.valueOf(status.toUpperCase()));
        // If status is CANCELLED or REJECTED and reason is provided, set notes
        if ((status.equalsIgnoreCase("CANCELLED") || status.equalsIgnoreCase("REJECTED")) && reason != null && !reason.isEmpty()) {
            appointment.setNotes(reason);
        } else if (status.equalsIgnoreCase("COMPLETED")) {
            appointment.setNotes(null); // Clear notes on completion
        }
        return appointmentRepository.save(appointment);
    }

    // --- ADMIN LOGIN ---
    public LoginResponse login(LoginRequest request) {
        Admin admin = adminRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid admin credentials"));
        if (admin.getPassword().equals(request.getPassword())) {
            LoginResponse response = new LoginResponse();
            response.setSuccess(true);
            response.setUserId(admin.getId());
            response.setName(admin.getName());
            response.setRole("ADMIN");
            return response;
        }
        throw new RuntimeException("Invalid admin credentials");
    }

    // --- REPORT GENERATION ---
    public byte[] generateCsvReport() {
        // TODO: Implement real CSV generation
        String csv = "Type,Count\nDoctors," + getAllDoctors().size() + "\nUsers," + getAllUsers().size() + "\nAppointments," + getAllAppointments().size();
        return csv.getBytes();
    }

    public byte[] generatePdfReport() {
        // TODO: Implement real PDF generation
        String text = "Hospital Report\nDoctors: " + getAllDoctors().size() + "\nUsers: " + getAllUsers().size() + "\nAppointments: " + getAllAppointments().size();
        return text.getBytes(); // Placeholder: not a real PDF
    }
}
