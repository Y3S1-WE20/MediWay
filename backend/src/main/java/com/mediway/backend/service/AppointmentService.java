package com.mediway.backend.service;

import com.mediway.backend.dto.request.AppointmentRequest;
import com.mediway.backend.dto.response.AppointmentResponse;
import com.mediway.backend.dto.response.DoctorResponse;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.exception.ResourceNotFoundException;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Transactional
    public AppointmentResponse createAppointment(UUID patientId, AppointmentRequest request) {
        log.info("Creating appointment for patient: {} with doctor: {}", patientId, request.getDoctorId());

        // Verify doctor exists
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + request.getDoctorId()));

        if (!doctor.getAvailable()) {
            throw new IllegalStateException("Doctor is not available for appointments");
        }

        Appointment appointment = Appointment.builder()
                .patientId(patientId)
                .doctorId(request.getDoctorId())
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .consultationFee(request.getConsultationFee() != null ? request.getConsultationFee() : doctor.getConsultationFee())
                .status(Appointment.AppointmentStatus.PENDING)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Appointment created successfully with ID: {}", savedAppointment.getAppointmentId());

        return mapToResponse(savedAppointment, doctor);
    }

    public List<AppointmentResponse> getMyAppointments(UUID patientId) {
        log.info("Fetching appointments for patient: {}", patientId);
        List<Appointment> appointments = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patientId);

        return appointments.stream()
                .map(appointment -> {
                    Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
                    return mapToResponse(appointment, doctor);
                })
                .collect(Collectors.toList());
    }

    public List<AppointmentResponse> getAllAppointments() {
        log.info("Fetching all appointments");
        List<Appointment> appointments = appointmentRepository.findAll();

        return appointments.stream()
                .map(appointment -> {
                    Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
                    return mapToResponse(appointment, doctor);
                })
                .collect(Collectors.toList());
    }

    public AppointmentResponse getAppointmentById(UUID appointmentId) {
        log.info("Fetching appointment with ID: {}", appointmentId);
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);
        return mapToResponse(appointment, doctor);
    }

    @Transactional
    public AppointmentResponse updateAppointmentStatus(UUID appointmentId, String status, String notes) {
        log.info("Updating appointment {} status to: {}", appointmentId, status);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        appointment.setStatus(Appointment.AppointmentStatus.valueOf(status.toUpperCase()));
        if (notes != null && !notes.isEmpty()) {
            appointment.setNotes(notes);
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        Doctor doctor = doctorRepository.findById(appointment.getDoctorId()).orElse(null);

        return mapToResponse(updatedAppointment, doctor);
    }

    @Transactional
    public void cancelAppointment(UUID appointmentId, UUID patientId) {
        log.info("Cancelling appointment: {} for patient: {}", appointmentId, patientId);

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + appointmentId));

        if (!appointment.getPatientId().equals(patientId)) {
            throw new IllegalStateException("You can only cancel your own appointments");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    public List<DoctorResponse> getAllDoctors() {
        log.info("Fetching all available doctors");
        List<Doctor> doctors = doctorRepository.findByAvailableTrue();

        return doctors.stream()
                .map(this::mapDoctorToResponse)
                .collect(Collectors.toList());
    }

    public DoctorResponse getDoctorById(UUID doctorId) {
        log.info("Fetching doctor with ID: {}", doctorId);
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));

        return mapDoctorToResponse(doctor);
    }

    private AppointmentResponse mapToResponse(Appointment appointment, Doctor doctor) {
        return AppointmentResponse.builder()
                .appointmentId(appointment.getAppointmentId())
                .patientId(appointment.getPatientId())
                .doctorId(appointment.getDoctorId())
                .doctorName(doctor != null ? doctor.getName() : "Unknown")
                .doctorSpecialization(doctor != null ? doctor.getSpecialization() : "Unknown")
                .appointmentDate(appointment.getAppointmentDate())
                .appointmentTime(appointment.getAppointmentTime())
                .status(appointment.getStatus().name())
                .reason(appointment.getReason())
                .notes(appointment.getNotes())
                .consultationFee(appointment.getConsultationFee())
                .createdAt(appointment.getCreatedAt())
                .build();
    }

    private DoctorResponse mapDoctorToResponse(Doctor doctor) {
        return DoctorResponse.builder()
                .doctorId(doctor.getDoctorId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .email(doctor.getEmail())
                .phone(doctor.getPhone())
                .qualification(doctor.getQualification())
                .experienceYears(doctor.getExperienceYears())
                .consultationFee(doctor.getConsultationFee())
                .available(doctor.getAvailable())
                .build();
    }
}
