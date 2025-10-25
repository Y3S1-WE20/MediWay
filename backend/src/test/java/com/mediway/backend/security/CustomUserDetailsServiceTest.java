package com.mediway.backend.security;

/*
 * TESTS SUMMARY (CustomUserDetailsServiceTest):
 * - Load patient user by email                           : Positive
 * - Load doctor user by email                            : Positive
 * - Load admin user by email                             : Positive
 * - Throw UsernameNotFoundException when missing         : Negative
 * - Handle edge cases: null/empty emails and role priority: Edge/Negative
 */

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.mediway.backend.entity.Admin;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AdminRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomUserDetailsService Tests")
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User testUser;
    private Doctor testDoctor;
    private Admin testAdmin;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setEmail("patient@example.com");
        testUser.setPassword("encodedPassword123");
        testUser.setRole(User.Role.PATIENT);

        testDoctor = new Doctor();
        testDoctor.setEmail("doctor@example.com");
        testDoctor.setPassword("encodedPassword456");

        testAdmin = new Admin();
        testAdmin.setEmail("admin@example.com");
        testAdmin.setPassword("encodedPassword789");
    }

    // Positive: Load patient user by email
    @Test
    @DisplayName("Should load patient user by email")
    void testLoadUserByUsername_Patient() {
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("patient@example.com");

        assertNotNull(userDetails);
        assertEquals("patient@example.com", userDetails.getUsername());
        assertEquals("encodedPassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT")));
        
        verify(userRepository).findByEmail("patient@example.com");
        verifyNoInteractions(doctorRepository, adminRepository);
    }

    // Positive: Load doctor user by email
    @Test
    @DisplayName("Should load doctor user by email")
    void testLoadUserByUsername_Doctor() {
        when(userRepository.findByEmail("doctor@example.com")).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail("doctor@example.com")).thenReturn(Optional.of(testDoctor));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("doctor@example.com");

        assertNotNull(userDetails);
        assertEquals("doctor@example.com", userDetails.getUsername());
        assertEquals("encodedPassword456", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")));
        
        verify(userRepository).findByEmail("doctor@example.com");
        verify(doctorRepository).findByEmail("doctor@example.com");
        verifyNoInteractions(adminRepository);
    }

    // Positive: Load admin user by email
    @Test
    @DisplayName("Should load admin user by email")
    void testLoadUserByUsername_Admin() {
        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail("admin@example.com")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(testAdmin));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin@example.com");

        assertNotNull(userDetails);
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals("encodedPassword789", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        
        verify(userRepository).findByEmail("admin@example.com");
        verify(doctorRepository).findByEmail("admin@example.com");
        verify(adminRepository).findByEmail("admin@example.com");
    }

    // Negative: Throw UsernameNotFoundException when user not found
    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void testLoadUserByUsername_NotFound() {
        String email = "notfound@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(email)
        );

        assertEquals("User not found: " + email, exception.getMessage());
        verify(userRepository).findByEmail(email);
        verify(doctorRepository).findByEmail(email);
        verify(adminRepository).findByEmail(email);
    }

    // Edge: Handle patient with ADMIN role
    @Test
    @DisplayName("Should handle patient with ADMIN role")
    void testLoadUserByUsername_PatientWithAdminRole() {
        testUser.setRole(User.Role.ADMIN);
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("patient@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    // Edge: Handle patient with DOCTOR role
    @Test
    @DisplayName("Should handle patient with DOCTOR role")
    void testLoadUserByUsername_PatientWithDoctorRole() {
        testUser.setRole(User.Role.DOCTOR);
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("patient@example.com");

        assertNotNull(userDetails);
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR")));
    }

    // Edge: Prioritize user repository over doctor and admin
    @Test
    @DisplayName("Should prioritize user repository over doctor and admin")
    void testLoadUserByUsername_PriorityOrder() {
        // Even if doctor exists, should return user if found first
        when(userRepository.findByEmail("patient@example.com")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("patient@example.com");

        assertNotNull(userDetails);
        assertEquals("ROLE_PATIENT", userDetails.getAuthorities().iterator().next().getAuthority());
        verify(userRepository).findByEmail("patient@example.com");
        verifyNoInteractions(doctorRepository, adminRepository);
    }

    // Edge: Handle null email gracefully
    @Test
    @DisplayName("Should handle null email gracefully")
    void testLoadUserByUsername_NullEmail() {
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(adminRepository.findByEmail(null)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(null));
    }

    // Edge: Handle empty email gracefully
    @Test
    @DisplayName("Should handle empty email gracefully")
    void testLoadUserByUsername_EmptyEmail() {
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(doctorRepository.findByEmail("")).thenReturn(Optional.empty());
        when(adminRepository.findByEmail("")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(""));
    }
}
