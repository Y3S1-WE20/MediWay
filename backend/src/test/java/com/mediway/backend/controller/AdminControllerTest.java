package com.mediway.backend.controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.service.AdminService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Controller Tests")
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private User testUser;
    private Doctor testDoctor;
    private Appointment testAppointment;
    private LoginRequest loginRequest;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setRole(User.Role.PATIENT);

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Smith");
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setEmail("doctor@example.com");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setPatientId(1L);
        testAppointment.setDoctorId(1L);
        testAppointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        testAppointment.setStatus(Appointment.Status.SCHEDULED);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@example.com");
        loginRequest.setPassword("password");

        loginResponse = new LoginResponse();
        loginResponse.setSuccess(true);
        loginResponse.setUserId(1L);
        loginResponse.setRole("ADMIN");
        loginResponse.setName("Admin User");
    }

    @Test
    @DisplayName("Get all users - Success")
    void testGetAllUsers() {
        List<User> users = Arrays.asList(testUser);
        when(adminService.getAllUsers()).thenReturn(users);

        ResponseEntity<List<User>> response = adminController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getAllUsers();
    }

    @Test
    @DisplayName("Get user by ID - Success")
    void testGetUserById() {
        when(adminService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<User> response = adminController.getUserById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(adminService).getUserById(1L);
    }

    @Test
    @DisplayName("Create user - Success")
    void testCreateUser() {
        when(adminService.createUser(any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = adminController.createUser(testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test@example.com", response.getBody().getEmail());
        verify(adminService).createUser(any(User.class));
    }

    @Test
    @DisplayName("Update user - Success")
    void testUpdateUser() {
        when(adminService.updateUser(eq(1L), any(User.class))).thenReturn(testUser);

        ResponseEntity<User> response = adminController.updateUser(1L, testUser);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(adminService).updateUser(eq(1L), any(User.class));
    }

    @Test
    @DisplayName("Delete user - Success")
    void testDeleteUser() {
        doNothing().when(adminService).deleteUser(1L);

        ResponseEntity<Void> response = adminController.deleteUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminService).deleteUser(1L);
    }

    @Test
    @DisplayName("Admin login - Success")
    void testAdminLogin() {
        when(adminService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = adminController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals(1L, response.getBody().getUserId());
        verify(adminService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Get all doctors - Success")
    void testGetAllDoctors() {
        List<Doctor> doctors = Arrays.asList(testDoctor);
        when(adminService.getAllDoctors()).thenReturn(doctors);

        ResponseEntity<List<Doctor>> response = adminController.getAllDoctors();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getAllDoctors();
    }

    @Test
    @DisplayName("Get doctor by ID - Success")
    void testGetDoctorById() {
        when(adminService.getDoctorById(1L)).thenReturn(testDoctor);

        ResponseEntity<Doctor> response = adminController.getDoctorById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dr. Smith", response.getBody().getName());
        verify(adminService).getDoctorById(1L);
    }

    @Test
    @DisplayName("Create doctor - Success")
    void testCreateDoctor() {
        when(adminService.createDoctor(any(Doctor.class))).thenReturn(testDoctor);

        ResponseEntity<Doctor> response = adminController.createDoctor(testDoctor);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Dr. Smith", response.getBody().getName());
        verify(adminService).createDoctor(any(Doctor.class));
    }

    @Test
    @DisplayName("Update doctor - Success")
    void testUpdateDoctor() {
        when(adminService.updateDoctor(eq(1L), any(Doctor.class))).thenReturn(testDoctor);

        ResponseEntity<Doctor> response = adminController.updateDoctor(1L, testDoctor);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(adminService).updateDoctor(eq(1L), any(Doctor.class));
    }

    @Test
    @DisplayName("Delete doctor - Success")
    void testDeleteDoctor() {
        doNothing().when(adminService).deleteDoctor(1L);

        ResponseEntity<Void> response = adminController.deleteDoctor(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(adminService).deleteDoctor(1L);
    }

    @Test
    @DisplayName("Get all appointments - Success")
    void testGetAllAppointments() {
        List<Appointment> appointments = Arrays.asList(testAppointment);
        when(adminService.getAllAppointments()).thenReturn(appointments);

        ResponseEntity<List<Appointment>> response = adminController.getAllAppointments();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(adminService).getAllAppointments();
    }

    @Test
    @DisplayName("Get appointment by ID - Success")
    void testGetAppointmentById() {
        when(adminService.getAppointmentById(1L)).thenReturn(testAppointment);

        ResponseEntity<Appointment> response = adminController.getAppointmentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(Appointment.Status.SCHEDULED, response.getBody().getStatus());
        verify(adminService).getAppointmentById(1L);
    }

    @Test
    @DisplayName("Update appointment status - Success")
    void testUpdateAppointmentStatus() {
        testAppointment.setStatus(Appointment.Status.COMPLETED);
    when(adminService.updateAppointmentStatus(1L, "COMPLETED", null)).thenReturn(testAppointment);

    ResponseEntity<Appointment> response = adminController.updateAppointmentStatus(1L, "COMPLETED", null);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(Appointment.Status.COMPLETED, response.getBody().getStatus());
    verify(adminService).updateAppointmentStatus(1L, "COMPLETED", null);
    }
}
