package com.mediway.backend.service;

/*
 * TESTS SUMMARY (PatientServiceTest):
 * - Register patient successfully                           : Positive
 * - Register with all fields                                 : Positive
 * - Login success / non-existent / incorrect password         : Positive / Negative
 * - Find by health ID (valid/invalid/null)                   : Positive / Edge
 */

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.request.RegisterRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.dto.response.RegisterResponse;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Patient Service Tests - User Management")
class PatientServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PatientService patientService;

    private RegisterRequest testRegisterRequest;
    private LoginRequest testLoginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRegisterRequest = new RegisterRequest();
        testRegisterRequest.setFullName("Test Patient");
        testRegisterRequest.setEmail("patient@test.com");
        testRegisterRequest.setPassword("password123");
        testRegisterRequest.setPhone("1234567890");
        testRegisterRequest.setRole(User.Role.PATIENT);

        testLoginRequest = new LoginRequest();
        testLoginRequest.setEmail("patient@test.com");
        testLoginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test Patient");
        testUser.setEmail("patient@test.com");
        testUser.setPassword("password123");
        testUser.setPhone("1234567890");
        testUser.setRole(User.Role.PATIENT);
    }

    // Positive: Register patient successfully
    @Test
    @DisplayName("Should register patient successfully")
    void register_ValidRequest_ReturnsSuccessResponse() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        RegisterResponse result = patientService.register(testRegisterRequest);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("Registration successful", result.getMessage());
        verify(userRepository).save(any(User.class));
    }

    // Positive: Register with all fields
    @Test
    @DisplayName("Should register patient with all fields")
    void register_WithAllFields_SavesUserCorrectly() {
        // Given
        testRegisterRequest.setFullName("John Doe");
        testRegisterRequest.setEmail("john.doe@test.com");
        testRegisterRequest.setPassword("securePassword");
        testRegisterRequest.setPhone("9876543210");
        testRegisterRequest.setRole(User.Role.PATIENT);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        RegisterResponse result = patientService.register(testRegisterRequest);

        // Then
        assertTrue(result.isSuccess());
        verify(userRepository).save(argThat(user -> 
            user.getName().equals("John Doe") &&
            user.getEmail().equals("john.doe@test.com") &&
            user.getPassword().equals("securePassword") &&
            user.getPhone().equals("9876543210") &&
            user.getRole() == User.Role.PATIENT
        ));
    }

    // Positive: Login success
    @Test
    @DisplayName("Should login patient successfully with valid credentials")
    void login_ValidCredentials_ReturnsSuccessResponse() {
        // Given
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(testUser));

        // When
        LoginResponse result = patientService.login(testLoginRequest);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(testUser.getId(), result.getUserId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals("PATIENT", result.getRole());
        verify(userRepository).findByEmail("patient@test.com");
    }

    // Negative: Login with non-existent email
    @Test
    @DisplayName("Should throw exception when patient not found during login")
    void login_NonExistentEmail_ThrowsException() {
        // Given
        when(userRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());
        testLoginRequest.setEmail("nonexistent@test.com");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.login(testLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("nonexistent@test.com");
    }

    // Negative: Login with incorrect password
    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void login_IncorrectPassword_ThrowsException() {
        // Given
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(testUser));
        testLoginRequest.setPassword("wrongpassword");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> patientService.login(testLoginRequest));
        assertEquals("Invalid credentials", exception.getMessage());
        verify(userRepository).findByEmail("patient@test.com");
    }

    // Positive: Find by valid health ID
    @Test
    @DisplayName("Should find user by health ID successfully")
    void findByHealthId_ValidId_ReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = patientService.findByHealthId("1");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getEmail(), result.get().getEmail());
        verify(userRepository).findById(1L);
    }

    // Edge: Find by non-existent health ID
    @Test
    @DisplayName("Should return empty when user not found by health ID")
    void findByHealthId_NonExistentId_ReturnsEmpty() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = patientService.findByHealthId("999");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(999L);
    }

    // Edge: Find by invalid health ID format
    @Test
    @DisplayName("Should return empty for invalid health ID format")
    void findByHealthId_InvalidFormat_ReturnsEmpty() {
        // When
        Optional<User> result = patientService.findByHealthId("invalid-id");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, never()).findById(anyLong());
    }

    // Edge: Find by null health ID
    @Test
    @DisplayName("Should handle null health ID")
    void findByHealthId_NullId_ReturnsEmpty() {
        // When
        Optional<User> result = patientService.findByHealthId(null);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, never()).findById(anyLong());
    }

    // Edge: Find by empty health ID
    @Test
    @DisplayName("Should handle empty health ID")
    void findByHealthId_EmptyId_ReturnsEmpty() {
        // When
        Optional<User> result = patientService.findByHealthId("");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository, never()).findById(anyLong());
    }

    // Edge: Find by negative health ID
    @Test
    @DisplayName("Should return empty for negative health ID")
    void findByHealthId_NegativeId_ReturnsEmpty() {
        // Given - negative IDs are actually parsed as valid Long values in the current implementation
        when(userRepository.findById(-1L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = patientService.findByHealthId("-1");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(-1L);
    }

    // Edge: Find by zero health ID
    @Test
    @DisplayName("Should handle zero health ID")
    void findByHealthId_ZeroId_CallsRepository() {
        // Given
        when(userRepository.findById(0L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = patientService.findByHealthId("0");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(0L);
    }

    // Edge: Find by large health ID
    @Test
    @DisplayName("Should handle large health ID")
    void findByHealthId_LargeId_CallsRepository() {
        // Given
        Long largeId = Long.MAX_VALUE;
        when(userRepository.findById(largeId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = patientService.findByHealthId(String.valueOf(largeId));

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(largeId);
    }

    // Positive: Register patient with DOCTOR role
    @Test
    @DisplayName("Should register patient with DOCTOR role")
    void register_DoctorRole_SavesCorrectly() {
        // Given
        testRegisterRequest.setRole(User.Role.DOCTOR);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        RegisterResponse result = patientService.register(testRegisterRequest);

        // Then
        assertTrue(result.isSuccess());
        verify(userRepository).save(argThat(user -> user.getRole() == User.Role.DOCTOR));
    }

    // Positive: Login with different user roles
    @Test
    @DisplayName("Should login with different user roles")
    void login_DifferentRoles_ReturnsCorrectRole() {
        // Given
        testUser.setRole(User.Role.DOCTOR);
        when(userRepository.findByEmail("patient@test.com")).thenReturn(Optional.of(testUser));

        // When
        LoginResponse result = patientService.login(testLoginRequest);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("DOCTOR", result.getRole());
    }
}