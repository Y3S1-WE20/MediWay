package com.mediway.backend.controller;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@CrossOrigin(origins = "http://localhost:5174")
public class SimpleReportsController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalRecordRepository medicalRecordRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalAppointments", appointmentRepository.count());
        stats.put("totalMedicalRecords", medicalRecordRepository.count());
        stats.put("totalPayments", paymentRepository.count());
        stats.put("totalPatients", userRepository.count());
        stats.put("totalDoctors", doctorRepository.count());

        // Appointment stats by status
        Map<String, Long> appointmentsByStatus = new HashMap<>();
        appointmentsByStatus.put("scheduled", (long) appointmentRepository.findByStatus(Appointment.Status.SCHEDULED).size());
        appointmentsByStatus.put("completed", (long) appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size());
        appointmentsByStatus.put("cancelled", (long) appointmentRepository.findByStatus(Appointment.Status.CANCELLED).size());
        stats.put("appointmentsByStatus", appointmentsByStatus);

        // Payment stats by status
        Map<String, Long> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("pending", (long) paymentRepository.findByStatus(Payment.Status.PENDING).size());
        paymentsByStatus.put("completed", (long) paymentRepository.findByStatus(Payment.Status.COMPLETED).size());
        paymentsByStatus.put("failed", (long) paymentRepository.findByStatus(Payment.Status.FAILED).size());
        stats.put("paymentsByStatus", paymentsByStatus);

        return ResponseEntity.ok(stats);
    }

    // Frontend expects reports at /api/reports/summary
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getReportsSummary() {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalAppointments", appointmentRepository.count());
        stats.put("totalMedicalRecords", medicalRecordRepository.count());
        stats.put("totalPayments", paymentRepository.count());
        stats.put("totalPatients", userRepository.count());
        stats.put("totalDoctors", doctorRepository.count());

        // Appointment stats by status
        Map<String, Long> appointmentsByStatus = new HashMap<>();
        appointmentsByStatus.put("scheduled", (long) appointmentRepository.findByStatus(Appointment.Status.SCHEDULED).size());
        appointmentsByStatus.put("completed", (long) appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size());
        appointmentsByStatus.put("cancelled", (long) appointmentRepository.findByStatus(Appointment.Status.CANCELLED).size());
        stats.put("appointmentsByStatus", appointmentsByStatus);

        // Payment stats by status
        Map<String, Long> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("pending", (long) paymentRepository.findByStatus(Payment.Status.PENDING).size());
        paymentsByStatus.put("completed", (long) paymentRepository.findByStatus(Payment.Status.COMPLETED).size());
        paymentsByStatus.put("failed", (long) paymentRepository.findByStatus(Payment.Status.FAILED).size());
        stats.put("paymentsByStatus", paymentsByStatus);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/medical-records")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findAll();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/medical-records/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getPatientMedicalRecords(@PathVariable Long patientId) {
        List<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByRecordDateDesc(patientId);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return ResponseEntity.ok(payments);
    }
}