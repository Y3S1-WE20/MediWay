package com.mediway.backend.controller;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // Frontend expects appointments at /api/appointments/my
    @GetMapping("/my")
    public ResponseEntity<List<Appointment>> getMyAppointments() {
        // For now, return all appointments (since we don't have authentication)
        List<Appointment> appointments = appointmentRepository.findAll();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .map(appointment -> ResponseEntity.ok(appointment))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody Map<String, Object> request) {
        try {
            // Extract data from request - frontend sends doctorId, appointmentDate, appointmentTime, reason
            Long doctorId = Long.parseLong(request.get("doctorId").toString());
            String dateStr = request.get("appointmentDate").toString();
            String timeStr = request.get("appointmentTime").toString();
            String reason = request.get("reason") != null ? request.get("reason").toString() : "";
            
            // Combine date and time into LocalDateTime
            String dateTimeStr = dateStr + " " + timeStr;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime appointmentDateTime = LocalDateTime.parse(dateTimeStr, formatter);
            
            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPatientId(1L); // For prototype, use patient ID 1
            appointment.setDoctorId(doctorId);
            appointment.setAppointmentDate(appointmentDateTime);
            appointment.setNotes(reason);
            appointment.setStatus(Appointment.Status.SCHEDULED);
            
            Appointment savedAppointment = appointmentRepository.save(appointment);
            return ResponseEntity.ok(savedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to create appointment: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
                    appointment.setStatus(appointmentDetails.getStatus());
                    appointment.setNotes(appointmentDetails.getNotes());
                    return ResponseEntity.ok(appointmentRepository.save(appointment));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointmentRepository.delete(appointment);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}