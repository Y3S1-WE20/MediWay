package com.mediway.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mediway.backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

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
     * Find user by id
     * @param id user id
     * @return Optional of User
     */
    Optional<User> findById(Long id);
}
