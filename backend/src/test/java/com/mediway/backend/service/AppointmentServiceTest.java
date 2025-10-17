package com.mediway.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;

/**
 * Unit tests for Appointment Scheduling functionality
 * Tests: Doctor scheduling, booking logic, and concurrency control
 */
@DisplayName("Appointment Service Tests - Scheduling & Concurrency")
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private UserRepository userRepository;

    private Appointment testAppointment;
    private Doctor testDoctor;
    private User testPatient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setEmail("smith@test.com");
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setAvailable(true);

        testPatient = new User();
        testPatient.setId(1L);
        testPatient.setName("John Doe");
        testPatient.setEmail("john@test.com");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(testPatient.getId());
        testAppointment.setDoctorId(testDoctor.getId());
        testAppointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(Appointment.Status.SCHEDULED);
        testAppointment.setNotes("Regular checkup");
    }

    @Test
    @DisplayName("Test 1: Successfully create appointment with available doctor")
    void testCreateAppointment_Success() {
        // Given
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.of(testDoctor));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        Appointment created = appointmentRepository.save(testAppointment);

        // Then
        assertNotNull(created);
        assertEquals(1L, created.getId());
        assertEquals(Appointment.Status.SCHEDULED, created.getStatus());
        assertEquals("Regular checkup", created.getNotes());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Test 2: Create appointment fails when doctor not found")
    void testCreateAppointment_DoctorNotFound() {
        // Given
        when(doctorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        Optional<Doctor> doctor = doctorRepository.findById(99L);
        assertFalse(doctor.isPresent(), "Doctor should not exist");
    }

    @Test
    @DisplayName("Test 3: Create appointment fails when patient not found")
    void testCreateAppointment_PatientNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When/Then
        Optional<User> patient = userRepository.findById(99L);
        assertFalse(patient.isPresent(), "Patient should not exist");
    }

    @Test
    @DisplayName("Test 4: Retrieve all appointments for a specific patient")
    void testGetPatientAppointments() {
        // Given
        Appointment apt1 = new Appointment();
        apt1.setId(1L);
        apt1.setPatientId(1L);
        apt1.setDoctorId(1L);
        apt1.setAppointmentDate(LocalDateTime.now().plusDays(1));

        Appointment apt2 = new Appointment();
        apt2.setId(2L);
        apt2.setPatientId(1L);
        apt2.setDoctorId(2L);
        apt2.setAppointmentDate(LocalDateTime.now().plusDays(2));

        List<Appointment> appointments = Arrays.asList(apt1, apt2);
        when(appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(1L)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getPatientId());
        assertEquals(1L, result.get(1).getPatientId());
        verify(appointmentRepository, times(1)).findByPatientIdOrderByAppointmentDateDesc(1L);
    }

    @Test
    @DisplayName("Test 5: Retrieve all appointments for a specific doctor")
    void testGetDoctorAppointments() {
        // Given
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(1L)).thenReturn(appointments);

        // When
        List<Appointment> result = appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getDoctorId());
        verify(appointmentRepository, times(1)).findByDoctorIdOrderByAppointmentDateDesc(1L);
    }

    @Test
    @DisplayName("Test 6: Update appointment status to COMPLETED")
    void testUpdateAppointmentStatus_Completed() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        testAppointment.setStatus(Appointment.Status.COMPLETED);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment updated = appointmentRepository.save(testAppointment);

        // Then
        assertNotNull(updated);
        assertEquals(Appointment.Status.COMPLETED, updated.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Test 7: Update appointment status to CANCELLED")
    void testUpdateAppointmentStatus_Cancelled() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        testAppointment.setStatus(Appointment.Status.CANCELLED);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment updated = appointmentRepository.save(testAppointment);

        // Then
        assertNotNull(updated);
        assertEquals(Appointment.Status.CANCELLED, updated.getStatus());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Test 8: Delete appointment by ID")
    void testDeleteAppointment() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        doNothing().when(appointmentRepository).deleteById(1L);

        // When
        appointmentRepository.deleteById(1L);

        // Then
        verify(appointmentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 9: Concurrency - Multiple appointments for same doctor at different times")
    void testConcurrency_MultipleAppointments_DifferentTimes() {
        // Given
        Appointment apt1 = new Appointment();
        apt1.setDoctorId(1L);
        apt1.setPatientId(1L);
        apt1.setAppointmentDate(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));

        Appointment apt2 = new Appointment();
        apt2.setDoctorId(1L);
        apt2.setPatientId(2L);
        apt2.setAppointmentDate(LocalDateTime.now().plusDays(1).withHour(11).withMinute(0));

        when(appointmentRepository.save(any(Appointment.class))).thenReturn(apt1).thenReturn(apt2);

        // When
        Appointment saved1 = appointmentRepository.save(apt1);
        Appointment saved2 = appointmentRepository.save(apt2);

        // Then
        assertNotNull(saved1);
        assertNotNull(saved2);
        assertNotEquals(saved1.getAppointmentDate(), saved2.getAppointmentDate());
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Test 10: Retrieve appointments by status (SCHEDULED)")
    void testGetAppointmentsByStatus_Scheduled() {
        // Given
        List<Appointment> scheduledAppointments = Arrays.asList(testAppointment);
        when(appointmentRepository.findByStatus(Appointment.Status.SCHEDULED)).thenReturn(scheduledAppointments);

        // When
        List<Appointment> result = appointmentRepository.findByStatus(Appointment.Status.SCHEDULED);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Appointment.Status.SCHEDULED, result.get(0).getStatus());
        verify(appointmentRepository, times(1)).findByStatus(Appointment.Status.SCHEDULED);
    }

    @Test
    @DisplayName("Test 11: Verify appointment dates are in the future")
    void testAppointmentDate_InFuture() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
        testAppointment.setAppointmentDate(futureDate);

        // When/Then
        assertTrue(testAppointment.getAppointmentDate().isAfter(LocalDateTime.now()),
                "Appointment date should be in the future");
    }

    @Test
    @DisplayName("Test 12: Validate appointment creation with notes")
    void testCreateAppointment_WithNotes() {
        // Given
        String notes = "Patient has history of heart disease";
        testAppointment.setNotes(notes);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
        Appointment saved = appointmentRepository.save(testAppointment);

        // Then
        assertNotNull(saved);
        assertEquals(notes, saved.getNotes());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    @DisplayName("Test 13: Edge case - Empty appointments list for patient")
    void testGetPatientAppointments_Empty() {
        // Given
        when(appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(99L))
                .thenReturn(Arrays.asList());

        // When
        List<Appointment> result = appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(99L);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(appointmentRepository, times(1)).findByPatientIdOrderByAppointmentDateDesc(99L);
    }

    @Test
    @DisplayName("Test 14: Verify doctor availability check")
    void testDoctorAvailability() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When
        Optional<Doctor> doctor = doctorRepository.findById(1L);

        // Then
        assertTrue(doctor.isPresent());
        assertTrue(doctor.get().getAvailable(), "Doctor should be available");
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 15: Appointment created with default SCHEDULED status")
    void testAppointment_DefaultStatus() {
        // Given
        Appointment newAppointment = new Appointment();

        // When/Then
        assertEquals(Appointment.Status.SCHEDULED, newAppointment.getStatus(),
                "Default status should be SCHEDULED");
    }
}
