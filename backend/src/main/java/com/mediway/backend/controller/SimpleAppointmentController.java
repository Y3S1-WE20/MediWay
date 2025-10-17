package com.mediway.backend.controller;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleAppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Helper method to convert Appointment to detailed response
    private Map<String, Object> appointmentToMap(Appointment appointment) {
        Map<String, Object> map = new HashMap<>();
        map.put("appointmentId", appointment.getId());
        map.put("id", appointment.getId());
        map.put("appointmentDate", appointment.getAppointmentDate().toString());
        map.put("status", appointment.getStatus().toString());
        map.put("notes", appointment.getNotes());
        map.put("reason", appointment.getNotes());
        
        // Add doctor details
        doctorRepository.findById(appointment.getDoctorId()).ifPresent(doctor -> {
            map.put("doctorId", doctor.getId());
            map.put("doctorName", doctor.getName());
            map.put("doctorSpecialization", doctor.getSpecialization());
            map.put("doctorEmail", doctor.getEmail());
            map.put("doctorPhone", doctor.getPhone());
        });
        
        // Add patient details
        userRepository.findById(appointment.getPatientId()).ifPresent(patient -> {
            map.put("patientId", patient.getId());
            map.put("patientName", patient.getName());
            map.put("patientEmail", patient.getEmail());
            map.put("patientPhone", patient.getPhone());
        });
        
        // Add payment info (default for prototype)
        map.put("consultationFee", 500.00);
        map.put("paymentStatus", "PENDING");
        map.put("isPaid", false);
        
        return map;
    }

    // Frontend expects appointments at /api/appointments/my
    @GetMapping("/my")
    public ResponseEntity<?> getMyAppointments() {
        // For prototype, return all appointments with full details
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Map<String, Object>> detailedAppointments = appointments.stream()
                .map(this::appointmentToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(detailedAppointments);
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        List<Map<String, Object>> detailedAppointments = appointments.stream()
                .map(this::appointmentToMap)
                .collect(Collectors.toList());
        return ResponseEntity.ok(detailedAppointments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        return appointmentRepository.findById(id)
                .map(appointment -> ResponseEntity.ok(appointmentToMap(appointment)))
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
            return ResponseEntity.ok(appointmentToMap(savedAppointment));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "message", "Failed to create appointment: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Appointment appointmentDetails) {
        return appointmentRepository.findById(id)
                .map(appointment -> {
                    appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
                    appointment.setStatus(appointmentDetails.getStatus());
                    appointment.setNotes(appointmentDetails.getNotes());
                    Appointment updated = appointmentRepository.save(appointment);
                    return ResponseEntity.ok(appointmentToMap(updated));
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