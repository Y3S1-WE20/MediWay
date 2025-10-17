package com.mediway.backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.MedicalRecord;
import com.mediway.backend.entity.Payment;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.MedicalRecordRepository;
import com.mediway.backend.repository.PaymentRepository;
import com.mediway.backend.repository.UserRepository;

@RestController
@RequestMapping("/reports")
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
        long patientsCount = userRepository.count();
        long doctorsCount = doctorRepository.count();
        long appointmentsCount = appointmentRepository.count();
        stats.put("patientsCount", patientsCount);
        stats.put("doctorsCount", doctorsCount);
        stats.put("appointmentsCount", appointmentsCount);

        // Payment status distribution
        Map<String, Long> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("pending", (long) paymentRepository.findByStatus(Payment.Status.PENDING).size());
        paymentsByStatus.put("completed", (long) paymentRepository.findByStatus(Payment.Status.COMPLETED).size());
        paymentsByStatus.put("failed", (long) paymentRepository.findByStatus(Payment.Status.FAILED).size());
        stats.put("paymentsByStatus", paymentsByStatus);

        // Appointments by department/specialization
        List<Appointment> allAppointments = appointmentRepository.findAll();
        List<Doctor> allDoctors = doctorRepository.findAll();
        Map<Long, String> doctorIdToSpec = new HashMap<>();
        for (Doctor d : allDoctors) doctorIdToSpec.put(d.getId(), d.getSpecialization());
        Map<String, Integer> apptByDept = new HashMap<>();
        for (Appointment appt : allAppointments) {
            String spec = doctorIdToSpec.getOrDefault(appt.getDoctorId(), "Unknown");
            apptByDept.put(spec, apptByDept.getOrDefault(spec, 0) + 1);
        }
        List<Map<String, Object>> apptByDeptList = new java.util.ArrayList<>();
        for (Map.Entry<String, Integer> e : apptByDept.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("department", e.getKey());
            m.put("count", e.getValue());
            apptByDeptList.add(m);
        }
        stats.put("appointmentsByDepartment", apptByDeptList);

        // Monthly revenue (sum of completed payments per month)
        List<Payment> allPayments = paymentRepository.findAll();
        Map<String, BigDecimal> revenueByMonth = new HashMap<>();
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
        for (Payment p : allPayments) {
            if (p.getStatus() == Payment.Status.COMPLETED && p.getPaymentDate() != null) {
                String month = p.getPaymentDate().toLocalDate().format(fmt);
                BigDecimal prev = revenueByMonth.getOrDefault(month, BigDecimal.ZERO);
                BigDecimal amt = p.getAmount() == null ? BigDecimal.ZERO : p.getAmount();
                revenueByMonth.put(month, prev.add(amt));
            }
        }
        List<Map<String, Object>> monthlyRevenue = new java.util.ArrayList<>();
        for (Map.Entry<String, BigDecimal> e : revenueByMonth.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("month", e.getKey());
            m.put("revenue", e.getValue());
            monthlyRevenue.add(m);
        }
        monthlyRevenue.sort(java.util.Comparator.comparing(m -> (String)m.get("month")));
        stats.put("monthlyRevenue", monthlyRevenue);

        // Users by status (active/inactive by recent login or createdAt, here just all active for demo)
        Map<String, Long> usersByStatus = new HashMap<>();
    usersByStatus.put("active", patientsCount + doctorsCount); // All users counted as active for now
        usersByStatus.put("inactive", 0L);
        stats.put("usersByStatus", usersByStatus);

        // Daily appointments trend (count per day for last 30 days)
        Map<String, Integer> apptByDay = new HashMap<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Appointment appt : allAppointments) {
            if (appt.getAppointmentDate() != null) {
                String day = appt.getAppointmentDate().toLocalDate().toString();
                apptByDay.put(day, apptByDay.getOrDefault(day, 0) + 1);
            }
        }
        List<Map<String, Object>> dailyAppointments = new java.util.ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            String day = today.minusDays(i).toString();
            Map<String, Object> m = new HashMap<>();
            m.put("date", day);
            m.put("count", apptByDay.getOrDefault(day, 0));
            dailyAppointments.add(m);
        }
        stats.put("dailyAppointments", dailyAppointments);

        // Top paying patients (by total paid)
        Map<Long, BigDecimal> patientPayments = new HashMap<>();
        for (Payment p : allPayments) {
            if (p.getStatus() == Payment.Status.COMPLETED) {
                BigDecimal prev = patientPayments.getOrDefault(p.getUserId(), BigDecimal.ZERO);
                BigDecimal amt = p.getAmount() == null ? BigDecimal.ZERO : p.getAmount();
                patientPayments.put(p.getUserId(), prev.add(amt));
            }
        }
        List<Map<String, Object>> topPayingPatients = new java.util.ArrayList<>();
        for (Map.Entry<Long, BigDecimal> e : patientPayments.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("patientId", e.getKey());
            m.put("totalPaid", e.getValue() == null ? 0.0 : e.getValue().doubleValue());
            topPayingPatients.add(m);
        }
        topPayingPatients.sort((a, b) -> Double.compare((Double)b.get("totalPaid"), (Double)a.get("totalPaid")));
        stats.put("topPayingPatients", topPayingPatients);

        // Revenue by doctor
        Map<Long, BigDecimal> doctorRevenue = new HashMap<>();
        for (Appointment appt : allAppointments) {
            if (appt.getDoctorId() != null) {
                for (Payment p : allPayments) {
                    if (p.getAppointmentId() != null && p.getAppointmentId().equals(appt.getId()) && p.getStatus() == Payment.Status.COMPLETED) {
                        BigDecimal prev = doctorRevenue.getOrDefault(appt.getDoctorId(), BigDecimal.ZERO);
                        BigDecimal amt = p.getAmount() == null ? BigDecimal.ZERO : p.getAmount();
                        doctorRevenue.put(appt.getDoctorId(), prev.add(amt));
                    }
                }
            }
        }
        List<Map<String, Object>> revenueByDoctor = new java.util.ArrayList<>();
        for (Map.Entry<Long, BigDecimal> e : doctorRevenue.entrySet()) {
            Map<String, Object> m = new HashMap<>();
            m.put("doctorId", e.getKey());
            m.put("revenue", e.getValue() == null ? 0.0 : e.getValue().doubleValue());
            revenueByDoctor.add(m);
        }
        stats.put("revenueByDoctor", revenueByDoctor);

        // Pending payments over time (count of pending payments per day for last 30 days)
        Map<String, Integer> pendingByDay = new HashMap<>();
        for (Payment p : allPayments) {
            if (p.getStatus() == Payment.Status.PENDING && p.getPaymentDate() != null) {
                String day = p.getPaymentDate().toLocalDate().toString();
                pendingByDay.put(day, pendingByDay.getOrDefault(day, 0) + 1);
            }
        }
        List<Map<String, Object>> pendingPaymentsOverTime = new java.util.ArrayList<>();
        for (int i = 29; i >= 0; i--) {
            String day = today.minusDays(i).toString();
            Map<String, Object> m = new HashMap<>();
            m.put("date", day);
            m.put("pendingCount", pendingByDay.getOrDefault(day, 0));
            pendingPaymentsOverTime.add(m);
        }
        stats.put("pendingPaymentsOverTime", pendingPaymentsOverTime);

        return ResponseEntity.ok(stats);
    }

    // Frontend expects reports at /api/reports/summary
    @GetMapping("/summary/csv")
    public ResponseEntity<String> downloadCsvSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Type,Count\n");
        sb.append("Patients,").append(userRepository.count()).append("\n");
        sb.append("Doctors,").append(doctorRepository.count()).append("\n");
        sb.append("Appointments,").append(appointmentRepository.count()).append("\n");
        sb.append("MedicalRecords,").append(medicalRecordRepository.count()).append("\n");
        sb.append("Payments,").append(paymentRepository.count()).append("\n");
        sb.append("AppointmentsScheduled,").append(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED).size()).append("\n");
        sb.append("AppointmentsCompleted,").append(appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size()).append("\n");
        sb.append("AppointmentsCancelled,").append(appointmentRepository.findByStatus(Appointment.Status.CANCELLED).size()).append("\n");
        sb.append("PaymentsPending,").append(paymentRepository.findByStatus(Payment.Status.PENDING).size()).append("\n");
        sb.append("PaymentsCompleted,").append(paymentRepository.findByStatus(Payment.Status.COMPLETED).size()).append("\n");
        sb.append("PaymentsFailed,").append(paymentRepository.findByStatus(Payment.Status.FAILED).size()).append("\n");
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=report_summary.csv")
                .header("Content-Type", "text/csv")
                .body(sb.toString());
    }

    @GetMapping("/summary/pdf")
    public ResponseEntity<byte[]> downloadPdfSummary() {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(baos);
            com.itextpdf.kernel.pdf.PdfDocument pdfDoc = new com.itextpdf.kernel.pdf.PdfDocument(writer);
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdfDoc);
            document.add(new com.itextpdf.layout.element.Paragraph("Hospital Report Summary").setBold().setFontSize(16));
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // Basic counts
            document.add(new com.itextpdf.layout.element.Paragraph("Patients: " + userRepository.count()));
            document.add(new com.itextpdf.layout.element.Paragraph("Doctors: " + doctorRepository.count()));
            document.add(new com.itextpdf.layout.element.Paragraph("Appointments: " + appointmentRepository.count()));
            document.add(new com.itextpdf.layout.element.Paragraph("Medical Records: " + medicalRecordRepository.count()));
            document.add(new com.itextpdf.layout.element.Paragraph("Payments: " + paymentRepository.count()));
            document.add(new com.itextpdf.layout.element.Paragraph(" "));
            document.add(new com.itextpdf.layout.element.Paragraph("Appointments Scheduled: " + appointmentRepository.findByStatus(Appointment.Status.SCHEDULED).size()));
            document.add(new com.itextpdf.layout.element.Paragraph("Appointments Completed: " + appointmentRepository.findByStatus(Appointment.Status.COMPLETED).size()));
            document.add(new com.itextpdf.layout.element.Paragraph("Appointments Cancelled: " + appointmentRepository.findByStatus(Appointment.Status.CANCELLED).size()));
            document.add(new com.itextpdf.layout.element.Paragraph("Payments Pending: " + paymentRepository.findByStatus(Payment.Status.PENDING).size()));
            document.add(new com.itextpdf.layout.element.Paragraph("Payments Completed: " + paymentRepository.findByStatus(Payment.Status.COMPLETED).size()));
            document.add(new com.itextpdf.layout.element.Paragraph("Payments Failed: " + paymentRepository.findByStatus(Payment.Status.FAILED).size()));
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // --- Analytics Data as Tables ---
            // 1. Monthly Revenue
            document.add(new com.itextpdf.layout.element.Paragraph("Monthly Revenue").setBold());
            java.util.List<Payment> allPayments = paymentRepository.findAll();
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM");
            java.util.Map<String, java.math.BigDecimal> revenueByMonth = new java.util.HashMap<>();
            for (Payment p : allPayments) {
                if (p.getStatus() == Payment.Status.COMPLETED && p.getPaymentDate() != null) {
                    String month = p.getPaymentDate().toLocalDate().format(fmt);
                    java.math.BigDecimal prev = revenueByMonth.getOrDefault(month, java.math.BigDecimal.ZERO);
                    java.math.BigDecimal amt = p.getAmount() == null ? java.math.BigDecimal.ZERO : p.getAmount();
                    revenueByMonth.put(month, prev.add(amt));
                }
            }
            com.itextpdf.layout.element.Table revenueTable = new com.itextpdf.layout.element.Table(2);
            revenueTable.addHeaderCell("Month");
            revenueTable.addHeaderCell("Revenue");
            java.util.List<String> sortedMonths = new java.util.ArrayList<>(revenueByMonth.keySet());
            java.util.Collections.sort(sortedMonths);
            for (String month : sortedMonths) {
                revenueTable.addCell(month);
                revenueTable.addCell(revenueByMonth.get(month).toString());
            }
            document.add(revenueTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // 2. Payment Status Distribution
            document.add(new com.itextpdf.layout.element.Paragraph("Payment Status Distribution").setBold());
            java.util.Map<String, Long> paymentsByStatus = new java.util.HashMap<>();
            paymentsByStatus.put("pending", (long) paymentRepository.findByStatus(Payment.Status.PENDING).size());
            paymentsByStatus.put("completed", (long) paymentRepository.findByStatus(Payment.Status.COMPLETED).size());
            paymentsByStatus.put("failed", (long) paymentRepository.findByStatus(Payment.Status.FAILED).size());
            com.itextpdf.layout.element.Table statusTable = new com.itextpdf.layout.element.Table(2);
            statusTable.addHeaderCell("Status");
            statusTable.addHeaderCell("Count");
            for (java.util.Map.Entry<String, Long> entry : paymentsByStatus.entrySet()) {
                statusTable.addCell(entry.getKey());
                statusTable.addCell(entry.getValue().toString());
            }
            document.add(statusTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // 3. Appointments by Department
            document.add(new com.itextpdf.layout.element.Paragraph("Appointments by Department").setBold());
            java.util.List<Appointment> allAppointments = appointmentRepository.findAll();
            java.util.List<Doctor> allDoctors = doctorRepository.findAll();
            java.util.Map<Long, String> doctorIdToSpec = new java.util.HashMap<>();
            for (Doctor d : allDoctors) doctorIdToSpec.put(d.getId(), d.getSpecialization());
            java.util.Map<String, Integer> apptByDept = new java.util.HashMap<>();
            for (Appointment appt : allAppointments) {
                String spec = doctorIdToSpec.getOrDefault(appt.getDoctorId(), "Unknown");
                apptByDept.put(spec, apptByDept.getOrDefault(spec, 0) + 1);
            }
            com.itextpdf.layout.element.Table deptTable = new com.itextpdf.layout.element.Table(2);
            deptTable.addHeaderCell("Department");
            deptTable.addHeaderCell("Appointments");
            for (java.util.Map.Entry<String, Integer> entry : apptByDept.entrySet()) {
                deptTable.addCell(entry.getKey());
                deptTable.addCell(entry.getValue().toString());
            }
            document.add(deptTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // 4. Users by Status
            document.add(new com.itextpdf.layout.element.Paragraph("Users by Status").setBold());
            com.itextpdf.layout.element.Table usersTable = new com.itextpdf.layout.element.Table(2);
            usersTable.addHeaderCell("Status");
            usersTable.addHeaderCell("Count");
            usersTable.addCell("active");
            usersTable.addCell(Long.toString(userRepository.count() + doctorRepository.count()));
            usersTable.addCell("inactive");
            usersTable.addCell("0");
            document.add(usersTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // 5. Daily Appointments Trend (last 7 days)
            document.add(new com.itextpdf.layout.element.Paragraph("Daily Appointments (Last 7 Days)").setBold());
            java.time.LocalDate today = java.time.LocalDate.now();
            java.util.Map<String, Integer> apptByDay = new java.util.HashMap<>();
            for (Appointment appt : allAppointments) {
                if (appt.getAppointmentDate() != null) {
                    String day = appt.getAppointmentDate().toLocalDate().toString();
                    apptByDay.put(day, apptByDay.getOrDefault(day, 0) + 1);
                }
            }
            com.itextpdf.layout.element.Table dailyApptTable = new com.itextpdf.layout.element.Table(2);
            dailyApptTable.addHeaderCell("Date");
            dailyApptTable.addHeaderCell("Appointments");
            for (int i = 6; i >= 0; i--) {
                String day = today.minusDays(i).toString();
                dailyApptTable.addCell(day);
                dailyApptTable.addCell(apptByDay.getOrDefault(day, 0).toString());
            }
            document.add(dailyApptTable);
            document.add(new com.itextpdf.layout.element.Paragraph(" "));

            // 6. Pending Payments Over Time (last 7 days)
            document.add(new com.itextpdf.layout.element.Paragraph("Pending Payments Over Time (Last 7 Days)").setBold());
            java.util.Map<String, Integer> pendingByDay = new java.util.HashMap<>();
            for (Payment p : allPayments) {
                if (p.getStatus() == Payment.Status.PENDING && p.getPaymentDate() != null) {
                    String day = p.getPaymentDate().toLocalDate().toString();
                    pendingByDay.put(day, pendingByDay.getOrDefault(day, 0) + 1);
                }
            }
            com.itextpdf.layout.element.Table pendingTable = new com.itextpdf.layout.element.Table(2);
            pendingTable.addHeaderCell("Date");
            pendingTable.addHeaderCell("Pending Payments");
            for (int i = 6; i >= 0; i--) {
                String day = today.minusDays(i).toString();
                pendingTable.addCell(day);
                pendingTable.addCell(pendingByDay.getOrDefault(day, 0).toString());
            }
            document.add(pendingTable);

            document.close();
            byte[] pdfBytes = baos.toByteArray();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=report_summary.pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
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