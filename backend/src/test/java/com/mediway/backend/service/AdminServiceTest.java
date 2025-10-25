package com.mediway.backend.service;

/*
 * TESTS SUMMARY (AdminServiceTest):
 * - Get all users / Get user by ID                        : Positive
 * - Throw exception when user not found                   : Negative
 * - Create / Update / Delete user                         : Positive
 * - Get all doctors / doctor by ID                        : Positive
 * - Admin-specific flows and error handling               : Mix (Positive/Negative)
 */

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
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Admin;
import com.mediway.backend.entity.Appointment;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.entity.User;
import com.mediway.backend.repository.AdminRepository;
import com.mediway.backend.repository.AppointmentRepository;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Tests - System Administration")
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private Admin testAdmin;
    private Doctor testDoctor;
    private User testUser;
    private Appointment testAppointment;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setEmail("admin@test.com");
        testAdmin.setPassword("password123");
        testAdmin.setName("Test Admin");

        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setName("Dr. Test");
        testDoctor.setSpecialization("General");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setName("Test User");

        testAppointment = new Appointment();
        testAppointment.setId(1L);
        testAppointment.setStatus(Appointment.Status.SCHEDULED);
    }

    // Positive: Get all users
    @Test
    @DisplayName("Should get all users")
    void getAllUsers_ReturnsAllUsers() {
        // Given
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@test.com");
        List<User> userList = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> result = adminService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals(testUser.getEmail(), result.get(0).getEmail());
        assertEquals(user2.getEmail(), result.get(1).getEmail());
        verify(userRepository).findAll();
    }

    // Positive: Get user by ID
    @Test
    @DisplayName("Should get user by ID")
    void getUserById_ExistingId_ReturnsUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        User result = adminService.getUserById(1L);

        // Then
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    // Negative: Throw exception when user not found
    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserById_NonExistingId_ThrowsException() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> adminService.getUserById(999L));
        verify(userRepository).findById(999L);
    }

    // Positive: Create user successfully
    @Test
    @DisplayName("Should create user successfully")
    void createUser_ValidUser_ReturnsCreatedUser() {
        // Given
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User result = adminService.createUser(testUser);

        // Then
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).save(testUser);
    }

    // Positive: Update user successfully
    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ValidUser_ReturnsUpdatedUser() {
        // Given
        User updatedUser = new User();
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@test.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = adminService.updateUser(1L, updatedUser);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    // Positive: Delete user by ID
    @Test
    @DisplayName("Should delete user by ID")
    void deleteUser_ExistingId_DeletesUser() {
        // Given
        doNothing().when(userRepository).deleteById(1L);

        // When
        adminService.deleteUser(1L);

        // Then
        verify(userRepository).deleteById(1L);
    }

    // Positive: Get all doctors
    @Test
    @DisplayName("Should get all doctors")
    void getAllDoctors_ReturnsAllDoctors() {
        // Given
        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setEmail("doctor2@test.com");
        List<Doctor> doctorList = Arrays.asList(testDoctor, doctor2);
        when(doctorRepository.findAll()).thenReturn(doctorList);

        // When
        List<Doctor> result = adminService.getAllDoctors();

        // Then
        assertEquals(2, result.size());
        assertEquals(testDoctor.getEmail(), result.get(0).getEmail());
        assertEquals(doctor2.getEmail(), result.get(1).getEmail());
        verify(doctorRepository).findAll();
    }

    // Positive: Get doctor by ID
    @Test
    @DisplayName("Should get doctor by ID")
    void getDoctorById_ExistingId_ReturnsDoctor() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When
        Doctor result = adminService.getDoctorById(1L);

        // Then
        assertEquals(testDoctor.getId(), result.getId());
        assertEquals(testDoctor.getEmail(), result.getEmail());
        verify(doctorRepository).findById(1L);
    }

    // Negative: Throw exception when doctor not found
    @Test
    @DisplayName("Should throw exception when doctor not found")
    void getDoctorById_NonExistingId_ThrowsException() {
        // Given
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> adminService.getDoctorById(999L));
        verify(doctorRepository).findById(999L);
    }

    // Positive: Create doctor successfully
    @Test
    @DisplayName("Should create doctor successfully")
    void createDoctor_ValidDoctor_ReturnsCreatedDoctor() {
        // Given
        when(doctorRepository.save(testDoctor)).thenReturn(testDoctor);

        // When
        Doctor result = adminService.createDoctor(testDoctor);

        // Then
        assertEquals(testDoctor.getId(), result.getId());
        assertEquals(testDoctor.getEmail(), result.getEmail());
        verify(doctorRepository).save(testDoctor);
    }

    // Positive: Update doctor successfully
    @Test
    @DisplayName("Should update doctor successfully")
    void updateDoctor_ValidDoctor_ReturnsUpdatedDoctor() {
        // Given
        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setName("Updated Doctor");
        updatedDoctor.setEmail("updated@test.com");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When
        Doctor result = adminService.updateDoctor(1L, updatedDoctor);

        // Then
        assertNotNull(result);
        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(testDoctor);
    }

    // Positive: Delete doctor by ID
    @Test
    @DisplayName("Should delete doctor by ID")
    void deleteDoctor_ExistingId_DeletesDoctor() {
        // Given
        doNothing().when(doctorRepository).deleteById(1L);

        // When
        adminService.deleteDoctor(1L);

        // Then
        verify(doctorRepository).deleteById(1L);
    }

    // Positive: Get all appointments
    @Test
    @DisplayName("Should get all appointments")
    void getAllAppointments_ReturnsAllAppointments() {
        // Given
        Appointment appointment2 = new Appointment();
        appointment2.setId(2L);
        appointment2.setStatus(Appointment.Status.COMPLETED);
        List<Appointment> appointmentList = Arrays.asList(testAppointment, appointment2);
        when(appointmentRepository.findAll()).thenReturn(appointmentList);

        // When
        List<Appointment> result = adminService.getAllAppointments();

        // Then
        assertEquals(2, result.size());
        assertEquals(testAppointment.getId(), result.get(0).getId());
        assertEquals(appointment2.getId(), result.get(1).getId());
        verify(appointmentRepository).findAll();
    }

    // Positive: Get appointment by ID
    @Test
    @DisplayName("Should get appointment by ID")
    void getAppointmentById_ExistingId_ReturnsAppointment() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When
        Appointment result = adminService.getAppointmentById(1L);

        // Then
        assertEquals(testAppointment.getId(), result.getId());
        assertEquals(testAppointment.getStatus(), result.getStatus());
        verify(appointmentRepository).findById(1L);
    }

    // Negative: Throw exception when appointment not found
    @Test
    @DisplayName("Should throw exception when appointment not found")
    void getAppointmentById_NonExistingId_ThrowsException() {
        // Given
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> adminService.getAppointmentById(999L));
        verify(appointmentRepository).findById(999L);
    }

    // Positive: Update appointment status
    @Test
    @DisplayName("Should update appointment status")
    void updateAppointmentStatus_ValidStatus_ReturnsUpdatedAppointment() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);

        // When
    Appointment result = adminService.updateAppointmentStatus(1L, "COMPLETED", null);

        // Then
        assertEquals(Appointment.Status.COMPLETED, result.getStatus());
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(testAppointment);
    }

    // Positive: Login admin successfully
    @Test
    @DisplayName("Should login admin successfully")
    void login_ValidCredentials_ReturnsLoginResponse() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("password123");
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));

        // When
        LoginResponse result = adminService.login(loginRequest);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(testAdmin.getId(), result.getUserId());
        assertEquals(testAdmin.getName(), result.getName());
        assertEquals("ADMIN", result.getRole());
        verify(adminRepository).findByEmail("admin@test.com");
    }

    // Negative: Throw exception when admin not found during login
    @Test
    @DisplayName("Should throw exception when admin not found during login")
    void login_NonExistentEmail_ThrowsException() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@test.com");
        loginRequest.setPassword("password123");
        when(adminRepository.findByEmail("nonexistent@test.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> adminService.login(loginRequest));
        verify(adminRepository).findByEmail("nonexistent@test.com");
    }

    // Negative: Throw exception when password is incorrect
    @Test
    @DisplayName("Should throw exception when password is incorrect")
    void login_IncorrectPassword_ThrowsException() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("wrongpassword");
        when(adminRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(testAdmin));

        // When & Then
        assertThrows(RuntimeException.class, () -> adminService.login(loginRequest));
        verify(adminRepository).findByEmail("admin@test.com");
    }

    // Positive: Generate CSV report
    @Test
    @DisplayName("Should generate CSV report")
    void generateCsvReport_ReturnsReportData() {
        // Given
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));

        // When
        byte[] result = adminService.generateCsvReport();

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        String csvContent = new String(result);
        assertTrue(csvContent.contains("Type,Count"));
        assertTrue(csvContent.contains("Doctors,1"));
        assertTrue(csvContent.contains("Users,1"));
        assertTrue(csvContent.contains("Appointments,1"));
        verify(doctorRepository).findAll();
        verify(userRepository).findAll();
        verify(appointmentRepository).findAll();
    }

    // Positive: Generate PDF report
    @Test
    @DisplayName("Should generate PDF report")
    void generatePdfReport_ReturnsReportData() {
        // Given
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));
        when(appointmentRepository.findAll()).thenReturn(Arrays.asList(testAppointment));

        // When
        byte[] result = adminService.generatePdfReport();

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0);
        String pdfContent = new String(result);
        assertTrue(pdfContent.contains("Hospital Report"));
        assertTrue(pdfContent.contains("Doctors: 1"));
        assertTrue(pdfContent.contains("Users: 1"));
        assertTrue(pdfContent.contains("Appointments: 1"));
        verify(doctorRepository).findAll();
        verify(userRepository).findAll();
        verify(appointmentRepository).findAll();
    }
}