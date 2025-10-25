package com.mediway.backend.controller;

/*
 * TESTS SUMMARY (SimpleAuthControllerTest):
 * - register_ValidUser_ReturnsSuccess                 : Positive
 * - register_ExistingEmail_ReturnsBadRequest         : Negative
 * - login_ValidCredentials_ReturnsSuccess            : Positive
 * - login_InvalidCredentials_ReturnsUnauthorized     : Negative
 * - register variations (fullName/empty name)        : Edge / Positive
 * - register exception handling                      : Negative
 * - login wrong password / exception handling        : Negative
 * - doctor/admin login variations (success/invalid)  : Mix (Positive/Negative)
 * - health endpoint                                  : Positive (health check)
 */

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.mediway.backend.entity.Admin;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AdminRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;
import com.mediway.backend.security.JwtUtil;

class SimpleAuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SimpleAuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_ValidUser_ReturnsSuccess() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\",\"phone\":\"1234567890\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    void register_ExistingEmail_ReturnsBadRequest() throws Exception {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"email\":\"john@example.com\",\"password\":\"password123\",\"phone\":\"1234567890\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    void login_ValidCredentials_ReturnsSuccess() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(User.Role.PATIENT);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"john@example.com\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("simple-token-1"));
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"john@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Register with fullName field instead of name")
    void register_WithFullName_ReturnsSuccess() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setName("Jane Doe");
        user.setEmail("jane@example.com");
        user.setRole(User.Role.PATIENT);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"fullName\":\"Jane Doe\",\"email\":\"jane@example.com\",\"password\":\"password123\",\"phone\":\"9876543210\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.fullName").value("Jane Doe"))
                .andExpect(jsonPath("$.role").value("PATIENT"));
    }

    @Test
    @DisplayName("Register with empty name falls back to fullName")
    void register_EmptyNameUsesFullName_ReturnsSuccess() throws Exception {
        // Given
        User user = new User();
        user.setId(2L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(User.Role.PATIENT);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"fullName\":\"Test User\",\"email\":\"test@example.com\",\"password\":\"pass123\",\"phone\":\"5555555555\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Register error handling - exception thrown")
    void register_ExceptionThrown_ReturnsInternalServerError() throws Exception {
        // Given
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Error User\",\"email\":\"error@example.com\",\"password\":\"pass\",\"phone\":\"1111111111\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Registration failed: Database error"));
    }

    @Test
    @DisplayName("Login with wrong password")
    void login_WrongPassword_ReturnsUnauthorized() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setEmail("john@example.com");
        user.setPassword("correctpassword");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"john@example.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Login error handling - exception thrown")
    void login_ExceptionThrown_ReturnsInternalServerError() throws Exception {
        // Given
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database connection lost"));

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"test@example.com\",\"password\":\"password\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Login failed: Database connection lost"));
    }

    @Test
    @DisplayName("Doctor login - success")
    void doctorLogin_ValidCredentials_ReturnsSuccess() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(10L);
        doctor.setName("Dr. Smith");
        doctor.setEmail("dr.smith@hospital.com");
        doctor.setPassword("doctor123");

        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(doctor));

        // When & Then
        mockMvc.perform(post("/auth/doctor-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dr.smith@hospital.com\",\"password\":\"doctor123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("simple-token-doctor-10"))
                .andExpect(jsonPath("$.role").value("DOCTOR"))
                .andExpect(jsonPath("$.fullName").value("Dr. Smith"));
    }

    @Test
    @DisplayName("Doctor login - invalid email")
    void doctorLogin_InvalidEmail_ReturnsUnauthorized() throws Exception {
        // Given
        when(doctorRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/auth/doctor-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"unknown@hospital.com\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Doctor login - wrong password")
    void doctorLogin_WrongPassword_ReturnsUnauthorized() throws Exception {
        // Given
        Doctor doctor = new Doctor();
        doctor.setId(10L);
        doctor.setEmail("dr.smith@hospital.com");
        doctor.setPassword("correctpassword");

        when(doctorRepository.findAll()).thenReturn(Collections.singletonList(doctor));

        // When & Then
        mockMvc.perform(post("/auth/doctor-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"dr.smith@hospital.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Doctor login - exception handling")
    void doctorLogin_ExceptionThrown_ReturnsInternalServerError() throws Exception {
        // Given
        when(doctorRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(post("/auth/doctor-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"doctor@example.com\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Login failed: Database error"));
    }

    @Test
    @DisplayName("Admin login - success")
    void adminLogin_ValidCredentials_ReturnsSuccess() throws Exception {
        // Given
        Admin admin = new Admin();
        admin.setId(100L);
        admin.setName("Admin User");
        admin.setEmail("admin@hospital.com");
        admin.setPassword("admin123");

        when(adminRepository.findByEmail("admin@hospital.com")).thenReturn(Optional.of(admin));

        // When & Then
        mockMvc.perform(post("/auth/admin-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@hospital.com\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.token").value("simple-token-admin-100"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.fullName").value("Admin User"));
    }

    @Test
    @DisplayName("Admin login - invalid email")
    void adminLogin_InvalidEmail_ReturnsUnauthorized() throws Exception {
        // Given
        when(adminRepository.findByEmail("unknown@hospital.com")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/auth/admin-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"unknown@hospital.com\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("Admin login - wrong password")
    void adminLogin_WrongPassword_ReturnsUnauthorized() throws Exception {
        // Given
        Admin admin = new Admin();
        admin.setId(100L);
        admin.setEmail("admin@hospital.com");
        admin.setPassword("correctpassword");

        when(adminRepository.findByEmail("admin@hospital.com")).thenReturn(Optional.of(admin));

        // When & Then
        mockMvc.perform(post("/auth/admin-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@hospital.com\",\"password\":\"wrongpassword\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Admin login - exception handling")
    void adminLogin_ExceptionThrown_ReturnsInternalServerError() throws Exception {
        // Given
        when(adminRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Connection timeout"));

        // When & Then
        mockMvc.perform(post("/auth/admin-login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"admin@example.com\",\"password\":\"pass\"}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Login failed: Connection timeout"));
    }

    @Test
    @DisplayName("Health check endpoint")
    void health_ReturnsHealthy() throws Exception {
        // When & Then
        mockMvc.perform(get("/auth/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.message").value("Auth service is running"));
    }
}