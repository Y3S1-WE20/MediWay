package com.mediway.backend.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("User Entity Tests")
class UserEntityTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
    }

    @Test
    @DisplayName("Should create User with default constructor and default values")
    void testDefaultConstructor() {
        User newUser = new User();
        assertNotNull(newUser);
        assertNull(newUser.getId());
        assertEquals(User.Role.PATIENT, newUser.getRole());
        assertNotNull(newUser.getCreatedAt());
    }

    @Test
    @DisplayName("Should create User with parameterized constructor")
    void testParameterizedConstructor() {
        User newUser = new User("John Doe", "john@example.com", "password", "1234567890", User.Role.PATIENT);
        assertEquals("John Doe", newUser.getName());
        assertEquals("john@example.com", newUser.getEmail());
        assertEquals("password", newUser.getPassword());
        assertEquals("1234567890", newUser.getPhone());
        assertEquals(User.Role.PATIENT, newUser.getRole());
    }

    @Test
    @DisplayName("Should set and get all User fields correctly")
    void testGettersAndSetters() {
        // Set values
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setPhone("1234567890");
        user.setGender("Male");
        user.setBloodType("O+");
        user.setAddress("123 Main St");
        LocalDate dob = LocalDate.of(1994, 1, 1);
        user.setDateOfBirth(dob);
        user.setRole(User.Role.PATIENT);
        user.setProfilePicture("profile.jpg");
        user.setEmergencyContact("Jane Doe");
        user.setEmergencyPhone("0987654321");
        user.setAllergies("Peanuts");
        user.setMedications("Aspirin");
        user.setQrCode("QR123");
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);

        // Assert values
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("password123", user.getPassword());
        assertEquals("1234567890", user.getPhone());
        assertEquals("Male", user.getGender());
        assertEquals("O+", user.getBloodType());
        assertEquals("123 Main St", user.getAddress());
        assertEquals(dob, user.getDateOfBirth());
        assertEquals(User.Role.PATIENT, user.getRole());
        assertEquals("profile.jpg", user.getProfilePicture());
        assertEquals("Jane Doe", user.getEmergencyContact());
        assertEquals("0987654321", user.getEmergencyPhone());
        assertEquals("Peanuts", user.getAllergies());
        assertEquals("Aspirin", user.getMedications());
        assertEquals("QR123", user.getQrCode());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle null values for optional fields")
    void testNullableFields() {
        user.setAddress(null);
        user.setDateOfBirth(null);
        user.setPhone(null);
        user.setBloodType(null);
        user.setProfilePicture(null);
        user.setEmergencyContact(null);
        user.setAllergies(null);
        user.setMedications(null);
        user.setQrCode(null);

        assertNull(user.getAddress());
        assertNull(user.getDateOfBirth());
        assertNull(user.getPhone());
        assertNull(user.getBloodType());
        assertNull(user.getProfilePicture());
        assertNull(user.getEmergencyContact());
        assertNull(user.getAllergies());
        assertNull(user.getMedications());
        assertNull(user.getQrCode());
    }

    @Test
    @DisplayName("Should handle special characters in text fields")
    void testSpecialCharacters() {
        user.setName("O'Brien-José María");
        user.setAddress("123 Main St., Apt #5");
        user.setAllergies("Pollen, dust & mold");

        assertEquals("O'Brien-José María", user.getName());
        assertEquals("123 Main St., Apt #5", user.getAddress());
        assertEquals("Pollen, dust & mold", user.getAllergies());
    }

    @Test
    @DisplayName("Should handle different role values")
    void testDifferentRoles() {
        user.setRole(User.Role.PATIENT);
        assertEquals(User.Role.PATIENT, user.getRole());

        user.setRole(User.Role.DOCTOR);
        assertEquals(User.Role.DOCTOR, user.getRole());

        user.setRole(User.Role.ADMIN);
        assertEquals(User.Role.ADMIN, user.getRole());
    }

    @Test
    @DisplayName("Should handle email format variations")
    void testEmailVariations() {
        user.setEmail("simple@example.com");
        assertEquals("simple@example.com", user.getEmail());

        user.setEmail("name+tag@example.co.uk");
        assertEquals("name+tag@example.co.uk", user.getEmail());

        user.setEmail("user.name@sub.example.com");
        assertEquals("user.name@sub.example.com", user.getEmail());
    }

    @Test
    @DisplayName("Should handle PrePersist onCreate callback")
    void testOnCreate() {
        User newUser = new User();
        newUser.setCreatedAt(null);
        newUser.setRole(null);
        newUser.onCreate();

        assertNotNull(newUser.getCreatedAt());
        assertEquals(User.Role.PATIENT, newUser.getRole());
    }

    @Test
    @DisplayName("Should not override existing createdAt in onCreate")
    void testOnCreateWithExistingTimestamp() {
        LocalDateTime existingTime = LocalDateTime.of(2023, 1, 1, 10, 0);
        user.setCreatedAt(existingTime);
        user.onCreate();

        assertEquals(existingTime, user.getCreatedAt());
    }

    @Test
    @DisplayName("Should handle blood type variations")
    void testBloodTypeVariations() {
        String[] bloodTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        for (String bloodType : bloodTypes) {
            user.setBloodType(bloodType);
            assertEquals(bloodType, user.getBloodType());
        }
    }

    @Test
    @DisplayName("Should handle long text in medications and allergies")
    void testLongTextFields() {
        String longMedications = "Aspirin 100mg daily, Metformin 500mg twice daily, ".repeat(10);
        String longAllergies = "Pollen, dust, mold, peanuts, shellfish, ".repeat(10);

        user.setMedications(longMedications);
        user.setAllergies(longAllergies);

        assertEquals(longMedications, user.getMedications());
        assertEquals(longAllergies, user.getAllergies());
    }
}
