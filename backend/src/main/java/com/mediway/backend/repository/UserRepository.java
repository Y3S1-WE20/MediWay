package com.mediway.backend.repository;

import com.mediway.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find user by email
     * @param email user email
     * @return Optional of User
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email
     * @param email user email
     * @return true if exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find active user by email
     * @param email user email
     * @param isActive active status
     * @return Optional of User
     */
    Optional<User> findByEmailAndIsActive(String email, Boolean isActive);
}
