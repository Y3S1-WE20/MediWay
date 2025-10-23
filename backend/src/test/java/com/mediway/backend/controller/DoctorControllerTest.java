package com.mediway.backend.controller;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediway.backend.dto.request.LoginRequest;
import com.mediway.backend.dto.response.LoginResponse;
import com.mediway.backend.entity.Doctor;
import com.mediway.backend.repository.DoctorRepository;
import com.mediway.backend.service.DoctorService;

@ExtendWith(MockitoExtension.class)
@DisplayName("DoctorController Tests")
class DoctorControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private DoctorService doctorService;

    @InjectMocks
    private DoctorController doctorController;

    private ObjectMapper objectMapper;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(doctorController).build();
        objectMapper = new ObjectMapper();
        
        // Setup test doctor
        testDoctor = new Doctor();
        testDoctor.setId(1L);
        testDoctor.setName("Dr. Test Doctor");
        testDoctor.setSpecialization("Cardiology");
        testDoctor.setEmail("doctor@test.com");
        testDoctor.setPhone("1234567890");
    }

    @Test
    @DisplayName("Get all doctors - Success")
    void getAllDoctors_Success() throws Exception {
        // Given
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));

        // When & Then
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Dr. Test Doctor"))
                .andExpect(jsonPath("$[0].specialization").value("Cardiology"));

        verify(doctorRepository).findAll();
    }

    @Test
    @DisplayName("Get doctor by ID - Success")
    void getDoctorById_Success() throws Exception {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When & Then
        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Test Doctor"))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));

        verify(doctorRepository).findById(1L);
    }

    @Test
    @DisplayName("Get doctor by ID - Not Found")
    void getDoctorById_NotFound() throws Exception {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isNotFound());

        verify(doctorRepository).findById(1L);
    }

    @Test
    @DisplayName("Create doctor - Success")
    void createDoctor_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "Dr. Test Doctor",
                "specialization": "Cardiology",
                "email": "doctor@test.com",
                "phone": "1234567890"
            }
            """;

        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When & Then
        mockMvc.perform(post("/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Test Doctor"));

        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Update doctor - Success")
    void updateDoctor_Success() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "Dr. Updated Doctor",
                "specialization": "Neurology",
                "email": "updated@test.com",
                "phone": "0987654321"
            }
            """;

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(testDoctor);

        // When & Then
        mockMvc.perform(put("/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Update doctor - Not Found")
    void updateDoctor_NotFound() throws Exception {
        // Given
        String requestBody = """
            {
                "name": "Dr. Updated Doctor",
                "specialization": "Neurology"
            }
            """;

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(put("/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNotFound());

        verify(doctorRepository).findById(1L);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Delete doctor - Success")
    void deleteDoctor_Success() throws Exception {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isOk());

        verify(doctorRepository).existsById(1L);
        verify(doctorRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Delete doctor - Not Found")
    void deleteDoctor_NotFound() throws Exception {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(false);

        // When & Then
        mockMvc.perform(delete("/doctors/1"))
                .andExpect(status().isNotFound());

        verify(doctorRepository).existsById(1L);
        verify(doctorRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Get available doctors - Success")
    void getAvailableDoctors_Success() throws Exception {
        // Given
        testDoctor.setAvailable(true);
        when(doctorRepository.findByAvailableTrue()).thenReturn(Arrays.asList(testDoctor));

        // When & Then
        mockMvc.perform(get("/doctors/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].available").value(true));

        verify(doctorRepository).findByAvailableTrue();
    }

    @Test
    @DisplayName("Get available doctors - Empty list")
    void getAvailableDoctors_EmptyList() throws Exception {
        // Given
        when(doctorRepository.findByAvailableTrue()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/doctors/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(doctorRepository).findByAvailableTrue();
    }

    @Test
    @DisplayName("Login doctor - Success")
    void loginDoctor_Success() throws Exception {
        // Given
        LoginRequest loginRequest = new LoginRequest("doctor@test.com", "password123");
        LoginResponse loginResponse = new LoginResponse(true, 1L, "Dr. Test Doctor", "DOCTOR");
        
        when(doctorService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // When & Then
        mockMvc.perform(post("/doctors/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Test Doctor"))
                .andExpect(jsonPath("$.role").value("DOCTOR"));

        verify(doctorService).login(any(LoginRequest.class));
    }

    @Test
    @DisplayName("Get doctor appointments - Success")
    void getDoctorAppointments_Success() throws Exception {
        // Given
        java.util.Map<String, Object> appointment = new java.util.HashMap<>();
        appointment.put("id", 1L);
        appointment.put("patientName", "John Doe");
        
        when(doctorService.getAppointmentsByDoctor(1L)).thenReturn(Arrays.asList(appointment));

        // When & Then
        mockMvc.perform(get("/doctors/1/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].patientName").value("John Doe"));

        verify(doctorService).getAppointmentsByDoctor(1L);
    }

    @Test
    @DisplayName("Get doctors for appointments - Success")
    void getDoctorsForAppointments_Success() throws Exception {
        // Given
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));

        // When & Then
        mockMvc.perform(get("/appointments/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].doctorId").value(1))
                .andExpect(jsonPath("$[0].name").value("Dr. Test Doctor"));

        verify(doctorRepository).findAll();
    }

    @Test
    @DisplayName("Get all doctors - With photo")
    void getAllDoctors_WithPhoto() throws Exception {
        // Given
        testDoctor.setPhoto("test-photo-bytes".getBytes());
        testDoctor.setPhotoContentType("image/jpeg");
        when(doctorRepository.findAll()).thenReturn(Arrays.asList(testDoctor));

        // When & Then
        mockMvc.perform(get("/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].photo").exists());

        verify(doctorRepository).findAll();
    }

    @Test
    @DisplayName("Get doctor by ID - With photo")
    void getDoctorById_WithPhoto() throws Exception {
        // Given
        testDoctor.setPhoto("test-photo-bytes".getBytes());
        testDoctor.setPhotoContentType("image/png");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));

        // When & Then
        mockMvc.perform(get("/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photo").exists());

        verify(doctorRepository).findById(1L);
    }

    @Test
    @DisplayName("Create doctor with photo - Success")
    void createDoctorWithPhoto_Success() throws Exception {
        // Given
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes());

        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(1L);
        savedDoctor.setName("Dr. New Doctor");
        savedDoctor.setEmail("new@doctor.com");
        savedDoctor.setSpecialization("Cardiology");
        savedDoctor.setPhone("1234567890");
        savedDoctor.setAvailable(true);
        savedDoctor.setPhoto(photo.getBytes());
        savedDoctor.setPhotoContentType("image/jpeg");

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        // When & Then
        mockMvc.perform(multipart("/doctors/with-photo")
                        .file(photo)
                        .param("name", "Dr. New Doctor")
                        .param("email", "new@doctor.com")
                        .param("password", "password123")
                        .param("specialization", "Cardiology")
                        .param("phone", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. New Doctor"))
                .andExpect(jsonPath("$.email").value("new@doctor.com"));

        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Create doctor with photo - Without photo")
    void createDoctorWithPhoto_WithoutPhoto() throws Exception {
        // Given
        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(1L);
        savedDoctor.setName("Dr. New Doctor");
        savedDoctor.setEmail("new@doctor.com");
        savedDoctor.setSpecialization("Cardiology");
        savedDoctor.setPhone("1234567890");
        savedDoctor.setAvailable(true);

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        // When & Then (no photo parameter)
        mockMvc.perform(multipart("/doctors/with-photo")
                        .param("name", "Dr. New Doctor")
                        .param("email", "new@doctor.com")
                        .param("password", "password123")
                        .param("specialization", "Cardiology")
                        .param("phone", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Create doctor with photo - Empty photo")
    void createDoctorWithPhoto_EmptyPhoto() throws Exception {
        // Given
        MockMultipartFile emptyPhoto = new MockMultipartFile(
                "photo",
                "",
                "image/jpeg",
                new byte[0]);

        Doctor savedDoctor = new Doctor();
        savedDoctor.setId(1L);
        savedDoctor.setName("Dr. New Doctor");
        savedDoctor.setEmail("new@doctor.com");
        savedDoctor.setSpecialization("Cardiology");

        when(doctorRepository.save(any(Doctor.class))).thenReturn(savedDoctor);

        // When & Then
        mockMvc.perform(multipart("/doctors/with-photo")
                        .file(emptyPhoto)
                        .param("name", "Dr. New Doctor")
                        .param("email", "new@doctor.com")
                        .param("password", "password123")
                        .param("specialization", "Cardiology")
                        .param("phone", "1234567890"))
                .andExpect(status().isOk());

        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Update doctor photo - Success")
    void updateDoctorPhoto_Success() throws Exception {
        // Given
        MockMultipartFile newPhoto = new MockMultipartFile(
                "photo",
                "updated.jpg",
                "image/jpeg",
                "updated image content".getBytes());

        Doctor updatedDoctor = new Doctor();
        updatedDoctor.setId(1L);
        updatedDoctor.setPhoto(newPhoto.getBytes());
        updatedDoctor.setPhotoContentType("image/jpeg");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorRepository.save(any(Doctor.class))).thenReturn(updatedDoctor);

        // When & Then
        mockMvc.perform(multipart("/doctors/1/photo")
                        .file(newPhoto)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());

        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    @DisplayName("Update doctor photo - Doctor not found")
    void updateDoctorPhoto_NotFound() throws Exception {
        // Given
        MockMultipartFile newPhoto = new MockMultipartFile(
                "photo",
                "updated.jpg",
                "image/jpeg",
                "updated image content".getBytes());

        when(doctorRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(multipart("/doctors/1/photo")
                        .file(newPhoto)
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isNotFound());

        verify(doctorRepository).findById(1L);
        verify(doctorRepository, never()).save(any());
    }
}