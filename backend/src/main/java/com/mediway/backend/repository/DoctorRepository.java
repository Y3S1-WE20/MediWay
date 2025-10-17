package com.mediway.backend.repository;

import com.mediway.backend.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    List<Doctor> findByAvailableTrue();
    List<Doctor> findBySpecialization(String specialization);
}
