-- Seed Patients (users) and Doctors for local testing
-- Make sure you're using the correct schema:  USE mediwaydb;

-- Note: User passwords below are bcrypt for the text 'password'
-- $2a$10$E9n3y8p5F2qf4i8m0bQdUO5n8G0Fz1Hk2oQm2x9pVb3tq3kQyq3lW
SET @now = NOW();

-- Patients (users table; role = PATIENT)
INSERT INTO users (user_id, full_name, email, password_hash, phone, role, is_active, created_at, updated_at)
VALUES
  (UUID_TO_BIN(UUID()), 'John Patient', 'john.patient@example.com', '$2a$10$E9n3y8p5F2qf4i8m0bQdUO5n8G0Fz1Hk2oQm2x9pVb3tq3kQyq3lW', '+10000000001', 'PATIENT', TRUE, @now, @now),
  (UUID_TO_BIN(UUID()), 'Jane Patient', 'jane.patient@example.com', '$2a$10$E9n3y8p5F2qf4i8m0bQdUO5n8G0Fz1Hk2oQm2x9pVb3tq3kQyq3lW', '+10000000002', 'PATIENT', TRUE, @now, @now);

-- Doctors
INSERT INTO doctors (doctor_id, name, specialization, email, phone, qualification, experience_years, consultation_fee, available, created_at, updated_at)
VALUES
  (UUID_TO_BIN(UUID()), 'Dr. Alice Smith', 'General', 'alice.smith@hospital.example', '+11000000001', 'MBBS', 5, 0.00, TRUE, @now, @now),
  (UUID_TO_BIN(UUID()), 'Dr. Bob Kumar', 'Cardiology', 'bob.kumar@hospital.example', '+11000000002', 'MD (Cardiology)', 10, 150.00, TRUE, @now, @now);

-- Verify
SELECT BIN_TO_UUID(user_id) AS user_id, full_name, email, role FROM users WHERE role='PATIENT';
SELECT BIN_TO_UUID(doctor_id) AS doctor_id, name, email, specialization FROM doctors;



