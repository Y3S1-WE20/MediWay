package com.mediway.backend.controller;

import com.mediway.backend.dto.request.AppointmentRequest;
import com.mediway.backend.dto.response.AppointmentResponse;
import com.mediway.backend.dto.response.DoctorResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        
        log.info("Creating appointment for user: {}", authentication.getName());
        UUID patientId = getUserIdFromAuthentication(authentication);
        
        AppointmentResponse response = appointmentService.createAppointment(patientId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication authentication) {
        log.info("Fetching appointments for user: {}", authentication.getName());
        UUID patientId = getUserIdFromAuthentication(authentication);
        
        List<AppointmentResponse> appointments = appointmentService.getMyAppointments(patientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        log.info("Fetching all appointments");
        List<AppointmentResponse> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable UUID appointmentId) {
        log.info("Fetching appointment with ID: {}", appointmentId);
        AppointmentResponse appointment = appointmentService.getAppointmentById(appointmentId);
        return ResponseEntity.ok(appointment);
    }

    @PatchMapping("/{appointmentId}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentResponse> updateAppointmentStatus(
            @PathVariable UUID appointmentId,
            @RequestBody Map<String, String> request) {
        
        log.info("Updating appointment {} status", appointmentId);
        String status = request.get("status");
        String notes = request.get("notes");
        
        AppointmentResponse response = appointmentService.updateAppointmentStatus(appointmentId, status, notes);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{appointmentId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable UUID appointmentId,
            Authentication authentication) {
        
        log.info("Cancelling appointment: {}", appointmentId);
        UUID patientId = getUserIdFromAuthentication(authentication);
        
        appointmentService.cancelAppointment(appointmentId, patientId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        log.info("Fetching all available doctors");
        List<DoctorResponse> doctors = appointmentService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable UUID doctorId) {
        log.info("Fetching doctor with ID: {}", doctorId);
        DoctorResponse doctor = appointmentService.getDoctorById(doctorId);
        return ResponseEntity.ok(doctor);
    }

    /**
     * Helper method to extract user ID from authentication
     */
    private UUID getUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return user.getUserId();
    }
}
