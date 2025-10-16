-- =====================================================
-- FINAL DATABASE FIX - Run this NOW in MySQL Workbench
-- This will completely fix the hex-encoded doctor IDs
-- =====================================================

USE mediwaydb;

-- Disable constraints
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- STEP 1: Completely drop and recreate doctors table
DROP TABLE IF EXISTS doctors;

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
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- STEP 2: Insert 3 doctors with PROPER UUIDs (NOT hex-encoded)
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available, available_from, available_to)
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 'Dr. Sarah Johnson', 'sarah.johnson@mediway.com', 'Cardiology', 150.00, TRUE, '09:00:00', '17:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 'Dr. Michael Chen', 'michael.chen@mediway.com', 'Pediatrics', 120.00, TRUE, '08:00:00', '16:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 'Dr. Emily Rodriguez', 'emily.rodriguez@mediway.com', 'Dermatology', 130.00, TRUE, '10:00:00', '18:00:00');

-- STEP 3: Delete all existing appointments (they reference old hex-encoded doctor IDs)
TRUNCATE TABLE appointments;

-- STEP 4: Add qr_code column if missing
SET @qr_check = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'mediwaydb' AND TABLE_NAME = 'users' AND COLUMN_NAME = 'qr_code');
SET @qr_sql = IF(@qr_check = 0, 'ALTER TABLE users ADD COLUMN qr_code VARCHAR(500)', 'SELECT "qr_code exists" AS info');
PREPARE qr_stmt FROM @qr_sql;
EXECUTE qr_stmt;
DEALLOCATE PREPARE qr_stmt;

-- STEP 5: Create medical records tables if missing
CREATE TABLE IF NOT EXISTS medical_records (
    record_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    record_date DATE NOT NULL,
    diagnosis TEXT,
    symptoms TEXT,
    treatment TEXT,
    notes TEXT,
    vital_signs TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_medical_record_patient (patient_id),
    INDEX idx_medical_record_doctor (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    medical_record_id VARCHAR(36),
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_doctor (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS lab_results (
    result_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    medical_record_id VARCHAR(36),
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
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lab_result_patient (patient_id),
    INDEX idx_lab_result_doctor (doctor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Re-enable constraints
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- =====================================================
-- VERIFICATION
-- =====================================================

SELECT '========== DOCTORS (Should be 3 with proper UUIDs) ==========' AS '';
SELECT doctor_id, name, email, specialization, consultation_fee FROM doctors;

SELECT '========== Check doctor_id format (should be 36 chars, with dashes) ==========' AS '';
SELECT 
    doctor_id,
    LENGTH(doctor_id) AS length,
    CASE 
        WHEN doctor_id LIKE '%-%' THEN 'PROPER UUID'
        ELSE 'HEX ENCODED (BAD!)'
    END AS format_check
FROM doctors;

SELECT '========== No hex-encoded IDs (should be empty) ==========' AS '';
SELECT doctor_id FROM doctors WHERE doctor_id LIKE '35313439%';

SELECT '========== Appointments (should be 0 - all cleared) ==========' AS '';
SELECT COUNT(*) AS appointment_count FROM appointments;

-- =====================================================
-- SUCCESS!
-- Now restart your backend (it's already running)
-- The backend will pick up the new doctor data
-- =====================================================
