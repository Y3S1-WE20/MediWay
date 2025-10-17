package com.mediway.backend.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;

/**
 * Unit tests for Admin Management (Doctors CRUD)
 * Tests: Manage users, doctors
 */
@DisplayName("Doctor Service Tests - Admin Management")
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MultipartFile photoFile;

    @InjectMocks
    private DoctorService doctorService;

    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup test data
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setEmail("smith@hospital.com");
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setPhone("+1234567890");
        testDoctor.setAvailable(true);
        testDoctor.setPassword("password123");
    }

    @Test
    @DisplayName("Test 1: Successfully create a new doctor")
    void testCreateDoctor_Success() throws Exception {
        // Given
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        Doctor created = doctorService.create("Dr. Smith", "smith@hospital.com", "Cardiology", null);

        // Then
        assertNotNull(created);
        assertEquals("Dr. Smith", created.getName());
        assertEquals("smith@hospital.com", created.getEmail());
        assertEquals("Cardiology", created.getSpecialization());
        assertTrue(created.getAvailable());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test 2: Create doctor with photo upload")
    void testCreateDoctor_WithPhoto() throws Exception {
        // Given
        byte[] photoBytes = "fake-photo-data".getBytes();
        when(photoFile.isEmpty()).thenReturn(false);
        when(photoFile.getBytes()).thenReturn(photoBytes);
        when(photoFile.getContentType()).thenReturn("image/jpeg");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        Doctor created = doctorService.create("Dr. Smith", "smith@hospital.com", "Cardiology", photoFile);

        // Then
        assertNotNull(created);
        verify(photoFile, times(1)).getBytes();
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test 3: List all doctors")
    void testListAllDoctors() {
        // Given
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setName("Dr. Johnson");
        doctor2.setEmail("johnson@hospital.com");
        doctor2.setSpecialization("Neurology");

        List<Doctor> doctors = Arrays.asList(testDoctor, doctor2);
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        List<Doctor> result = doctorService.list();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Dr. Smith", result.get(0).getName());
        assertEquals("Dr. Johnson", result.get(1).getName());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test 4: Get doctor by ID successfully")
    void testGetDoctorById_Success() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When
        Doctor found = doctorService.get(1L);

        // Then
        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Dr. Smith", found.getName());
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Test 5: Get doctor by ID - not found")
    void testGetDoctorById_NotFound() {
        // Given
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, () -> doctorService.get(99L));
        verify(doctorRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Test 6: Update doctor details")
    void testUpdateDoctor() throws Exception {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        testDoctor.setName("Dr. Smith Updated");
        testDoctor.setSpecialization("Advanced Cardiology");
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        Doctor updated = doctorService.update(1L, "Dr. Smith Updated", "smith@hospital.com", 
                                              "Advanced Cardiology", null);

        // Then
        assertNotNull(updated);
        assertEquals("Dr. Smith Updated", updated.getName());
        assertEquals("Advanced Cardiology", updated.getSpecialization());
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test 7: Delete doctor by ID")
    void testDeleteDoctor() {
        // Given
        doNothing().when(doctorRepository).deleteById(1L);

        // When
        doctorService.delete(1L);

        // Then
        verify(doctorRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Test 8: Set doctor password")
    void testSetDoctorPassword() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        doctorService.setPassword(1L, "newPassword123");

        // Then
        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test 9: Doctor login successful")
    void testDoctorLogin_Success() {
        // Given
        when(doctorRepository.findByEmail("smith@hospital.com")).thenReturn(Optional.of(testDoctor));

        // When
        Doctor loggedIn = doctorService.login("smith@hospital.com", "password123");

        // Then
        assertNotNull(loggedIn);
        assertEquals("smith@hospital.com", loggedIn.getEmail());
        verify(doctorRepository, times(1)).findByEmail("smith@hospital.com");
    }

    @Test
    @DisplayName("Test 10: Doctor login fails with wrong password")
    void testDoctorLogin_WrongPassword() {
        // Given
        when(doctorRepository.findByEmail("smith@hospital.com")).thenReturn(Optional.of(testDoctor));

        // When/Then
        assertThrows(RuntimeException.class, 
                () -> doctorService.login("smith@hospital.com", "wrongPassword"));
        verify(doctorRepository, times(1)).findByEmail("smith@hospital.com");
    }

    @Test
    @DisplayName("Test 11: Doctor login fails with non-existent email")
    void testDoctorLogin_EmailNotFound() {
        // Given
        when(doctorRepository.findByEmail("nonexistent@hospital.com")).thenReturn(Optional.empty());

        // When/Then
        assertThrows(RuntimeException.class, 
                () -> doctorService.login("nonexistent@hospital.com", "password123"));
        verify(doctorRepository, times(1)).findByEmail("nonexistent@hospital.com");
    }

    @Test
    @DisplayName("Test 12: Get all appointments for a doctor")
    void testGetDoctorAppointments() {
        // Given
        Appointment apt1 = new Appointment();
        apt1.setId(1L);
        apt1.setDoctorId(1L);
        apt1.setPatientId(1L);
        apt1.setAppointmentDate(LocalDateTime.now().plusDays(1));
        apt1.setStatus(Appointment.Status.SCHEDULED);

        Appointment apt2 = new Appointment();
        apt2.setId(2L);
        apt2.setDoctorId(1L);
        apt2.setPatientId(2L);
        apt2.setAppointmentDate(LocalDateTime.now().plusDays(2));
        apt2.setStatus(Appointment.Status.SCHEDULED);

        List<Appointment> appointments = Arrays.asList(apt1, apt2);
        when(appointmentRepository.findByDoctorIdOrderByAppointmentDateDesc(1L)).thenReturn(appointments);

        // When
        List<Appointment> result = doctorService.appointmentsByDoctor(1L);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getDoctorId());
        assertEquals(1L, result.get(1).getDoctorId());
        verify(appointmentRepository, times(1)).findByDoctorIdOrderByAppointmentDateDesc(1L);
    }

    @Test
    @DisplayName("Test 13: Update doctor with empty strings (should not update)")
    void testUpdateDoctor_EmptyStrings() throws Exception {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        Doctor updated = doctorService.update(1L, "", "", "", null);

        // Then
        assertNotNull(updated);
        assertEquals("Dr. Smith", updated.getName()); // Should remain unchanged
        assertEquals("Cardiology", updated.getSpecialization()); // Should remain unchanged
        verify(doctorRepository, times(1)).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Test 14: Verify doctor availability status")
    void testDoctorAvailability() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When
        Doctor doctor = doctorService.get(1L);

        // Then
        assertNotNull(doctor);
        assertTrue(doctor.getAvailable());
    }

    @Test
    @DisplayName("Test 15: Edge case - Empty doctor list")
    void testListDoctors_Empty() {
        // Given
        when(doctorRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Doctor> result = doctorService.list();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(doctorRepository, times(1)).findAll();
    }
}
