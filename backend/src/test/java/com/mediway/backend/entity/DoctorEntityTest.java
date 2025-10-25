package com.mediway.backend.entity;

/*
 * TESTS SUMMARY (DoctorEntityTest):
 * - Default constructor and defaults                    : Positive
 * - Parameterized constructor                           : Positive
 * - Getters/Setters and nullable fields                 : Positive / Edge
 * - Availability toggle                                  : Edge
 * - PrePersist onCreate behavior                         : Edge
 * - Photo byte array handling                            : Edge
 */

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Doctor Entity Tests")
class DoctorEntityTest {

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        doctor = new Doctor();
    }

    // Positive: Create Doctor with default constructor and default values
    @Test
    @DisplayName("Should create Doctor with default constructor and default values")
    void testDefaultConstructor() {
        Doctor newDoctor = new Doctor();
        assertNotNull(newDoctor);
        assertNull(newDoctor.getId());
        assertTrue(newDoctor.getAvailable());
        assertNotNull(newDoctor.getCreatedAt());
    }

    // Positive: Create Doctor with parameterized constructor
    @Test
    @DisplayName("Should create Doctor with parameterized constructor")
    void testParameterizedConstructor() {
        Doctor newDoctor = new Doctor("Dr. Smith", "Cardiology", "smith@hospital.com", "1234567890", "password");
        assertEquals("Dr. Smith", newDoctor.getName());
        assertEquals("Cardiology", newDoctor.getSpecialization());
        assertEquals("smith@hospital.com", newDoctor.getEmail());
        assertEquals("1234567890", newDoctor.getPhone());
        assertEquals("password", newDoctor.getPassword());
        assertTrue(newDoctor.getAvailable());
    }

    // Positive: Set and get all Doctor fields correctly
    @Test
    @DisplayName("Should set and get all Doctor fields correctly")
    void testGettersAndSetters() {
        doctor.setId(1L);
        doctor.setName("Dr. Jane Smith");
        doctor.setSpecialization("Neurology");
        doctor.setEmail("jane.smith@hospital.com");
        doctor.setPhone("9876543210");
        doctor.setAvailable(false);
        doctor.setPassword("securePassword");
        byte[] photoBytes = new byte[]{1, 2, 3, 4, 5};
        doctor.setPhoto(photoBytes);
        doctor.setPhotoContentType("image/jpeg");
        LocalDateTime now = LocalDateTime.now();
        doctor.setCreatedAt(now);

        assertEquals(1L, doctor.getId());
        assertEquals("Dr. Jane Smith", doctor.getName());
        assertEquals("Neurology", doctor.getSpecialization());
        assertEquals("jane.smith@hospital.com", doctor.getEmail());
        assertEquals("9876543210", doctor.getPhone());
        assertFalse(doctor.getAvailable());
        assertEquals("securePassword", doctor.getPassword());
        assertArrayEquals(photoBytes, doctor.getPhoto());
        assertEquals("image/jpeg", doctor.getPhotoContentType());
        assertEquals(now, doctor.getCreatedAt());
    }

    // Edge: Handle null values for optional fields
    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullableFields() {
        doctor.setPhone(null);
        doctor.setPhoto(null);
        doctor.setPhotoContentType(null);

        assertNull(doctor.getPhone());
        assertNull(doctor.getPhoto());
        assertNull(doctor.getPhotoContentType());
    }

    // Edge: Handle availability toggle
    @Test
    @DisplayName("Should handle availability toggle")
    void testAvailabilityToggle() {
        doctor.setAvailable(true);
        assertTrue(doctor.getAvailable());

        doctor.setAvailable(false);
        assertFalse(doctor.getAvailable());
    }

    // Edge: Handle different specializations
    @Test
    @DisplayName("Should handle different specializations")
    void testDifferentSpecializations() {
        String[] specializations = {
            "Cardiology", "Neurology", "Pediatrics", "Orthopedics",
            "Dermatology", "Psychiatry", "General Medicine"
        };

        for (String spec : specializations) {
            doctor.setSpecialization(spec);
            assertEquals(spec, doctor.getSpecialization());
        }
    }

    // Edge: Handle PrePersist onCreate callback
    @Test
    @DisplayName("Should handle PrePersist onCreate callback")
    void testOnCreate() {
        Doctor newDoctor = new Doctor();
        newDoctor.setCreatedAt(null);
        newDoctor.setAvailable(null);
        newDoctor.onCreate();

        assertNotNull(newDoctor.getCreatedAt());
        assertTrue(newDoctor.getAvailable());
    }

    // Edge: Not override existing createdAt in onCreate
    @Test
    @DisplayName("Should not override existing createdAt in onCreate")
    void testOnCreateWithExistingTimestamp() {
        LocalDateTime existingTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        doctor.setCreatedAt(existingTime);
        doctor.setAvailable(false);
        doctor.onCreate();

        assertEquals(existingTime, doctor.getCreatedAt());
        assertFalse(doctor.getAvailable());
    }

    // Edge: Handle photo byte array
    @Test
    @DisplayName("Should handle photo byte array")
    void testPhotoByteArray() {
        byte[] largePhoto = new byte[10000];
        for (int i = 0; i < largePhoto.length; i++) {
            largePhoto[i] = (byte) (i % 256);
        }

        doctor.setPhoto(largePhoto);
        doctor.setPhotoContentType("image/png");

        assertArrayEquals(largePhoto, doctor.getPhoto());
        assertEquals("image/png", doctor.getPhotoContentType());
    }

    // Edge: Handle different photo content types
    @Test
    @DisplayName("Should handle different photo content types")
    void testPhotoContentTypes() {
        String[] contentTypes = {"image/jpeg", "image/png", "image/gif", "image/webp"};

        for (String contentType : contentTypes) {
            doctor.setPhotoContentType(contentType);
            assertEquals(contentType, doctor.getPhotoContentType());
        }
    }
}
