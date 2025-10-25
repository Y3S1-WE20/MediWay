package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimpleProfileControllerTest):
 * - Get profile with valid user ID                    : Positive
 * - Default user ID when null                         : Edge
 * - Return 404 when user not found                    : Negative
 * - Update profile success and partial updates       : Positive / Edge
 * - Change password success and wrong current pass   : Positive / Negative
 * - Missing fields handling                           : Edge
 */

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Simple Profile Controller Tests")
class SimpleProfileControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private SimpleProfileController simpleProfileController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setPhone("1234567890");
        testUser.setRole(User.Role.PATIENT);
        testUser.setDateOfBirth(LocalDate.of(1990, 1, 1));
        testUser.setGender("Male");
        testUser.setAddress("123 Main St");
        testUser.setPassword("hashedPassword");
    }

    // Positive: Get profile with valid user ID
    @Test
    @DisplayName("Should get profile with valid user ID")
    void testGetProfile_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = simpleProfileController.getProfile(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userRepository, times(1)).findById(1L);
    }

    // Edge: Default user ID when null
    @Test
    @DisplayName("Should use default user ID when null")
    void testGetProfile_DefaultUserId() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = simpleProfileController.getProfile(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
    }

    // Negative: Return 404 when user not found
    @Test
    @DisplayName("Should return 404 when user not found")
    void testGetProfile_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = simpleProfileController.getProfile(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Positive: Update profile successfully
    @Test
    @DisplayName("Should update profile successfully")
    void testUpdateProfile_Success() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Jane Doe");
        updates.put("phone", "9876543210");
        updates.put("address", "456 Oak Ave");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Negative: Return 404 when updating non-existent user
    @Test
    @DisplayName("Should return 404 when updating non-existent user")
    void testUpdateProfile_UserNotFound() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    // Positive: Change password successfully
    @Test
    @DisplayName("Should change password successfully")
    void testChangePassword_Success() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "oldPassword");
        passwordData.put("newPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.changePassword(1L, passwordData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Negative: Reject password change with wrong current password
    @Test
    @DisplayName("Should reject password change with wrong current password")
    void testChangePassword_WrongCurrentPassword() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "wrongPassword");
        passwordData.put("newPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", testUser.getPassword())).thenReturn(false);

        ResponseEntity<?> response = simpleProfileController.changePassword(1L, passwordData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    // Negative: Return 404 when changing password for non-existent user
    @Test
    @DisplayName("Should return 404 when changing password for non-existent user")
    void testChangePassword_UserNotFound() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "oldPassword");
        passwordData.put("newPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = simpleProfileController.changePassword(1L, passwordData);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    // Edge: Handle missing fields in profile update
    @Test
    @DisplayName("Should handle missing fields in profile update")
    void testUpdateProfile_PartialUpdate() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("phone", "5555555555");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // Edge: Handle null updates map
    @Test
    @DisplayName("Should handle null updates map")
    void testUpdateProfile_NullUpdates() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, null);

        // Expect 500 error because containsKey will throw NullPointerException
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle missing password fields")
    void testChangePassword_MissingFields() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "oldPassword");
        // Missing newPassword

        ResponseEntity<?> response = simpleProfileController.changePassword(1L, passwordData);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in getProfile")
    void testGetProfile_Exception() {
        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = simpleProfileController.getProfile(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in updateProfile")
    void testUpdateProfile_Exception() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Jane Doe");

        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should handle exception in changePassword")
    void testChangePassword_Exception() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "oldPassword");
        passwordData.put("newPassword", "newPassword123");

        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = simpleProfileController.changePassword(1L, passwordData);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should update dateOfBirth field")
    void testUpdateProfile_WithDateOfBirth() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("dateOfBirth", "1985-05-15");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update gender field")
    void testUpdateProfile_WithGender() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("gender", "Female");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update bloodType field")
    void testUpdateProfile_WithBloodType() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("bloodType", "O+");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update emergencyContact field")
    void testUpdateProfile_WithEmergencyContact() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("emergencyContact", "Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update emergencyPhone field")
    void testUpdateProfile_WithEmergencyPhone() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("emergencyPhone", "555-1234");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update allergies field")
    void testUpdateProfile_WithAllergies() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("allergies", "Peanuts, Shellfish");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update medications field")
    void testUpdateProfile_WithMedications() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("medications", "Aspirin, Vitamin D");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should update profilePicture field")
    void testUpdateProfile_WithProfilePicture() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("profilePicture", "base64ImageData");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(1L, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should get QR code for user with existing QR code")
    void testGetQRCode_ExistingQRCode() {
        testUser.setQrCode("existingQRCodeData");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = simpleProfileController.getQRCode(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, never()).save(any()); // Should not save when QR code exists
    }

    @Test
    @DisplayName("Should generate new QR code for user without one")
    void testGetQRCode_GenerateNew() {
        testUser.setQrCode(null); // No existing QR code
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.getQRCode(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class)); // Should save new QR code
    }

    @Test
    @DisplayName("Should generate new QR code when empty string")
    void testGetQRCode_EmptyString() {
        testUser.setQrCode(""); // Empty QR code
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.getQRCode(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class)); // Should save new QR code
    }

    @Test
    @DisplayName("Should return 404 when getting QR code for non-existent user")
    void testGetQRCode_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = simpleProfileController.getQRCode(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId for QR code when null")
    void testGetQRCode_DefaultUserId() {
        testUser.setQrCode("existingQRCodeData");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ResponseEntity<?> response = simpleProfileController.getQRCode(null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should handle exception in getQRCode")
    void testGetQRCode_Exception() {
        when(userRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        ResponseEntity<?> response = simpleProfileController.getQRCode(1L);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should use default userId in changePassword when null")
    void testChangePassword_DefaultUserId() {
        Map<String, String> passwordData = new HashMap<>();
        passwordData.put("currentPassword", "oldPassword");
        passwordData.put("newPassword", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", testUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("hashedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.changePassword(null, passwordData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should use default userId in updateProfile when null")
    void testUpdateProfile_DefaultUserId() {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", "Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<?> response = simpleProfileController.updateProfile(null, updates);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).findById(1L);
    }
}
