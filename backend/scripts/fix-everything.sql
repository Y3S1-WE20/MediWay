-- =================================================================
-- MediWay EMERGENCY FIX - Run this to fix all issues
-- =================================================================

USE mediwaydb;

-- Disable safe mode
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =================================================================
-- STEP 1: Clean up and recreate doctors table
-- =================================================================

-- Drop existing doctors table and recreate with proper structure
DROP TABLE IF EXISTS doctors;

CREATE TABLE doctors (
    doctor_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    specialization VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    qualification VARCHAR(200),
    experience_years INT,
    consultation_fee DECIMAL(10,2),
    available BOOLEAN DEFAULT TRUE,
    available_from TIME,
    available_to TIME,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert doctors with proper UUID format
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available, available_from, available_to)
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 'Dr. Sarah Johnson', 'sarah.johnson@mediway.com', 'Cardiology', 150.00, TRUE, '09:00:00', '17:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 'Dr. Michael Chen', 'michael.chen@mediway.com', 'Pediatrics', 120.00, TRUE, '08:00:00', '16:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 'Dr. Emily Rodriguez', 'emily.rodriguez@mediway.com', 'Dermatology', 130.00, TRUE, '10:00:00', '18:00:00');

-- =================================================================
-- STEP 2: Add QR code column to users if not exists
-- =================================================================

-- Check and add qr_code column
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'mediwaydb' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'qr_code'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE users ADD COLUMN qr_code VARCHAR(500)', 
    'SELECT ''Column qr_code already exists'''
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =================================================================
-- STEP 3: Create medical records tables if not exist
-- =================================================================

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
    INDEX idx_medical_record_doctor (doctor_id),
    INDEX idx_medical_record_date (record_date)
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
    INDEX idx_prescription_doctor (doctor_id),
    INDEX idx_prescription_status (status)
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
    INDEX idx_lab_result_doctor (doctor_id),
    INDEX idx_lab_result_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =================================================================
-- STEP 4: Clean up appointments with invalid doctor references
-- =================================================================

-- Delete appointments with bad doctor_id references
DELETE FROM appointments 
WHERE doctor_id NOT IN (SELECT doctor_id FROM doctors);

-- Re-enable constraints
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- =================================================================
-- VERIFICATION
-- =================================================================

SELECT '=== DOCTORS ===' as '';
SELECT doctor_id, name, email, specialization, consultation_fee, available FROM doctors;

SELECT '=== USERS WITH QR CODES ===' as '';
SELECT user_id, email, role, qr_code FROM users WHERE qr_code IS NOT NULL LIMIT 5;

SELECT '=== TABLES CREATED ===' as '';
SELECT TABLE_NAME, TABLE_ROWS 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'mediwaydb' 
AND TABLE_NAME IN ('doctors', 'users', 'appointments', 'medical_records', 'prescriptions', 'lab_results')
ORDER BY TABLE_NAME;

SELECT '=== APPOINTMENTS ===' as '';
SELECT COUNT(*) as total_appointments FROM appointments;

-- =================================================================
-- SUCCESS! Database is now properly configured.
-- Next steps:
-- 1. Stop your backend if it's running (Ctrl+C in terminal)
-- 2. Rebuild: cd F:\MediWay\backend && .\mvnw.cmd clean install
-- 3. Restart: .\mvnw.cmd spring-boot:run
-- 4. Test appointment booking - should work now!
-- =================================================================
