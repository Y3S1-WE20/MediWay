package com.mediway.backend.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Appointment Entity Tests")
class AppointmentEntityTest {

    private Appointment appointment;

    @BeforeEach
    void setUp() {
        appointment = new Appointment();
    }

    @Test
    @DisplayName("Should create Appointment with default constructor and default values")
    void testDefaultConstructor() {
        Appointment newAppointment = new Appointment();
        assertNotNull(newAppointment);
        assertNull(newAppointment.getId());
        assertEquals(Appointment.Status.SCHEDULED, newAppointment.getStatus());
        assertNotNull(newAppointment.getCreatedAt());
    }

    @Test
    @DisplayName("Should create Appointment with parameterized constructor")
    void testParameterizedConstructor() {
        LocalDateTime appointmentDate = LocalDateTime.of(2025, 10, 25, 10, 0);
        Appointment newAppointment = new Appointment(1L, 2L, appointmentDate, "Regular checkup");
        
        assertEquals(1L, newAppointment.getPatientId());
        assertEquals(2L, newAppointment.getDoctorId());
        assertEquals(appointmentDate, newAppointment.getAppointmentDate());
        assertEquals("Regular checkup", newAppointment.getNotes());
        assertEquals(Appointment.Status.SCHEDULED, newAppointment.getStatus());
    }

    @Test
    @DisplayName("Should set and get all Appointment fields correctly")
    void testGettersAndSetters() {
        LocalDateTime appointmentDate = LocalDateTime.of(2025, 10, 25, 14, 30);
        LocalDateTime createdAt = LocalDateTime.now();

        appointment.setId(1L);
        appointment.setPatientId(10L);
        appointment.setDoctorId(20L);
        appointment.setAppointmentDate(appointmentDate);
        appointment.setStatus(Appointment.Status.COMPLETED);
        appointment.setNotes("Follow-up consultation");
        appointment.setCreatedAt(createdAt);

        assertEquals(1L, appointment.getId());
        assertEquals(10L, appointment.getPatientId());
        assertEquals(20L, appointment.getDoctorId());
        assertEquals(appointmentDate, appointment.getAppointmentDate());
        assertEquals(Appointment.Status.COMPLETED, appointment.getStatus());
        assertEquals("Follow-up consultation", appointment.getNotes());
        assertEquals(createdAt, appointment.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null notes")
    void testNullNotes() {
        appointment.setNotes(null);
        assertNull(appointment.getNotes());
    }

    @Test
    @DisplayName("Should handle all status values")
    void testAllStatusValues() {
        appointment.setStatus(Appointment.Status.SCHEDULED);
        assertEquals(Appointment.Status.SCHEDULED, appointment.getStatus());

        appointment.setStatus(Appointment.Status.COMPLETED);
        assertEquals(Appointment.Status.COMPLETED, appointment.getStatus());

        appointment.setStatus(Appointment.Status.CANCELLED);
        assertEquals(Appointment.Status.CANCELLED, appointment.getStatus());
    }

    @Test
    @DisplayName("Should handle PrePersist onCreate callback")
    void testOnCreate() {
        Appointment newAppointment = new Appointment();
        newAppointment.setCreatedAt(null);
        newAppointment.setStatus(null);
        newAppointment.onCreate();

        assertNotNull(newAppointment.getCreatedAt());
        assertEquals(Appointment.Status.SCHEDULED, newAppointment.getStatus());
    }

    @Test
    @DisplayName("Should not override existing values in onCreate")
    void testOnCreateWithExistingValues() {
        LocalDateTime existingTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        appointment.setCreatedAt(existingTime);
        appointment.setStatus(Appointment.Status.COMPLETED);
        appointment.onCreate();

        assertEquals(existingTime, appointment.getCreatedAt());
        assertEquals(Appointment.Status.COMPLETED, appointment.getStatus());
    }

    @Test
    @DisplayName("Should handle long notes text")
    void testLongNotes() {
        String longNotes = "This is a very long note. ".repeat(50);
        appointment.setNotes(longNotes);
        assertEquals(longNotes, appointment.getNotes());
    }

    @Test
    @DisplayName("Should handle future appointment dates")
    void testFutureAppointmentDates() {
        LocalDateTime futureDate = LocalDateTime.now().plusDays(30);
        appointment.setAppointmentDate(futureDate);
        assertEquals(futureDate, appointment.getAppointmentDate());
    }

    @Test
    @DisplayName("Should handle past appointment dates")
    void testPastAppointmentDates() {
        LocalDateTime pastDate = LocalDateTime.now().minusDays(30);
        appointment.setAppointmentDate(pastDate);
        assertEquals(pastDate, appointment.getAppointmentDate());
    }
}
