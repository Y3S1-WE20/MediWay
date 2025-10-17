-- =====================================================
-- FRESH START - Complete Database Rebuild
-- This will delete EVERYTHING and start clean
-- =====================================================

-- Drop the corrupted database completely
DROP DATABASE IF EXISTS mediwaydb;

-- Create fresh database
CREATE DATABASE mediwaydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the new database
USE mediwaydb;

-- =====================================================
-- CREATE ALL TABLES FROM SCRATCH
-- =====================================================

-- Users table
CREATE TABLE users (
    user_id CHAR(36) NOT NULL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    date_of_birth DATE,
    address VARCHAR(255),
    role ENUM('PATIENT', 'DOCTOR', 'ADMIN') NOT NULL DEFAULT 'PATIENT',
    qr_code VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Doctors table
CREATE TABLE doctors (
    doctor_id CHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    qualification VARCHAR(200),
    experience_years INT,
    consultation_fee DECIMAL(10,2),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    available_from TIME,
    available_to TIME,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_specialization (specialization),
    INDEX idx_available (available)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Appointments table
CREATE TABLE appointments (
    appointment_id CHAR(36) NOT NULL PRIMARY KEY,
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    notes TEXT,
    consultation_fee DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_appointment_date (appointment_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table
CREATE TABLE payments (
    payment_id CHAR(36) NOT NULL PRIMARY KEY,
    appointment_id CHAR(36),
    user_id CHAR(36) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_method ENUM('PAYPAL', 'CREDIT_CARD', 'DEBIT_CARD', 'CASH') NOT NULL,
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    transaction_id VARCHAR(255),
    description TEXT,
    payment_date TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_appointment_id (appointment_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Medical Records table
CREATE TABLE medical_records (
    record_id CHAR(36) NOT NULL PRIMARY KEY,
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    appointment_id CHAR(36),
    record_date DATE NOT NULL,
    diagnosis TEXT,
    symptoms TEXT,
    treatment TEXT,
    notes TEXT,
    vital_signs TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date DATE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_record_date (record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Prescriptions table
CREATE TABLE prescriptions (
    prescription_id CHAR(36) NOT NULL PRIMARY KEY,
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    appointment_id CHAR(36),
    medical_record_id CHAR(36),
    prescription_date DATE NOT NULL,
    medication_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    refills_allowed INT DEFAULT 0,
    refills_remaining INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Lab Results table
CREATE TABLE lab_results (
    result_id CHAR(36) NOT NULL PRIMARY KEY,
    patient_id CHAR(36) NOT NULL,
    doctor_id CHAR(36) NOT NULL,
    appointment_id CHAR(36),
    medical_record_id CHAR(36),
    test_name VARCHAR(200) NOT NULL,
    test_type VARCHAR(100) NOT NULL,
    test_date DATE NOT NULL,
    result_date DATE NOT NULL,
    result_value TEXT,
    result_unit VARCHAR(50),
    reference_range VARCHAR(100),
    status ENUM('NORMAL', 'ABNORMAL', 'CRITICAL', 'PENDING') NOT NULL DEFAULT 'NORMAL',
    notes TEXT,
    file_url VARCHAR(500),
    lab_technician VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_patient_id (patient_id),
    INDEX idx_doctor_id (doctor_id),
    INDEX idx_test_date (test_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INSERT CLEAN SAMPLE DATA
-- =====================================================

-- Insert 3 doctors with PROPER UUIDs
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available, available_from, available_to, qualification, experience_years, phone) 
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 
     'Dr. Sarah Johnson', 
     'sarah.johnson@mediway.com', 
     'Cardiology', 
     150.00, 
     TRUE, 
     '09:00:00', 
     '17:00:00',
     'MD, FACC - Board Certified Cardiologist',
     15,
     '+1-555-0101'),
     
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 
     'Dr. Michael Chen', 
     'michael.chen@mediway.com', 
     'Pediatrics', 
     120.00, 
     TRUE, 
     '08:00:00', 
     '16:00:00',
     'MD, FAAP - Board Certified Pediatrician',
     12,
     '+1-555-0102'),
     
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 
     'Dr. Emily Rodriguez', 
     'emily.rodriguez@mediway.com', 
     'Dermatology', 
     130.00, 
     TRUE, 
     '10:00:00', 
     '18:00:00',
     'MD, FAAD - Board Certified Dermatologist',
     10,
     '+1-555-0103');

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

SELECT '========== DATABASE CREATED SUCCESSFULLY ==========' AS '';

SELECT '========== TABLES (Should be 7) ==========' AS '';
SHOW TABLES;

SELECT '========== DOCTORS (Should be 3 with PROPER UUIDs) ==========' AS '';
SELECT 
    doctor_id,
    name,
    specialization,
    email,
    consultation_fee,
    LENGTH(doctor_id) AS id_length,
    CASE 
        WHEN doctor_id REGEXP '^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$' THEN '✓ VALID UUID'
        ELSE '✗ INVALID'
    END AS format_check
FROM doctors
ORDER BY name;

SELECT '========== CHECK FOR BAD IDs (Should be 0) ==========' AS '';
SELECT COUNT(*) AS bad_doctor_count 
FROM doctors 
WHERE doctor_id LIKE '35313439%';

SELECT '========== ALL TABLES EMPTY (Except doctors) ==========' AS '';
SELECT 
    (SELECT COUNT(*) FROM users) AS users_count,
    (SELECT COUNT(*) FROM appointments) AS appointments_count,
    (SELECT COUNT(*) FROM payments) AS payments_count,
    (SELECT COUNT(*) FROM medical_records) AS medical_records_count,
    (SELECT COUNT(*) FROM prescriptions) AS prescriptions_count,
    (SELECT COUNT(*) FROM lab_results) AS lab_results_count;

SELECT '========== SUCCESS! Database is ready to use! ==========' AS '';
SELECT 'Next step: Restart your backend and test!' AS instruction;
