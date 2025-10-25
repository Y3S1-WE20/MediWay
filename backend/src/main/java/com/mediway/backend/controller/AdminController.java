
package com.mediway.backend.controller;
import java.util.List;

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
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.service.AdminService;

@RestController
@RequestMapping("")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // --- USER MANAGEMENT ---
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = adminService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/admin/users")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = adminService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = adminService.updateUser(id, userDetails);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = adminService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/doctors")
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = adminService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/admin/doctors/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = adminService.getDoctorById(id);
        return ResponseEntity.ok(doctor);
    }

    @PostMapping("/admin/doctors")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor savedDoctor = adminService.createDoctor(doctor);
        return ResponseEntity.ok(savedDoctor);
    }

    @PutMapping("/admin/doctors/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        Doctor updatedDoctor = adminService.updateDoctor(id, doctorDetails);
        return ResponseEntity.ok(updatedDoctor);
    }

    @DeleteMapping("/admin/doctors/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        adminService.deleteDoctor(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = adminService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/admin/appointments/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = adminService.getAppointmentById(id);
        return ResponseEntity.ok(appointment);
    }

    @PutMapping("/admin/appointments/{id}/status")
    public ResponseEntity<Appointment> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reason) {
        Appointment updatedAppointment = adminService.updateAppointmentStatus(id, status, reason);
        return ResponseEntity.ok(updatedAppointment);
    }
}