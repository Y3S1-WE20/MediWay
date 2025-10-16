-- =====================================================
-- ULTIMATE FIX - This will solve everything
-- Run this ENTIRE script in MySQL Workbench
-- =====================================================

USE mediwaydb;

-- Step 1: Disable constraints
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- Step 2: COMPLETELY DROP AND RECREATE DOCTORS TABLE
-- This ensures no hex encoding or UUID issues
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

-- Step 3: Insert ONLY 3 doctors with EXACT UUIDs
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available, available_from, available_to)
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 'Dr. Sarah Johnson', 'sarah.johnson@mediway.com', 'Cardiology', 150.00, TRUE, '09:00:00', '17:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 'Dr. Michael Chen', 'michael.chen@mediway.com', 'Pediatrics', 120.00, TRUE, '08:00:00', '16:00:00'),
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 'Dr. Emily Rodriguez', 'emily.rodriguez@mediway.com', 'Dermatology', 130.00, TRUE, '10:00:00', '18:00:00');

-- Step 4: Clean up appointments table
-- Remove all appointments (they reference old doctors)
TRUNCATE TABLE appointments;

-- Step 5: Add qr_code column if missing
SET @qr_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = 'mediwaydb' AND TABLE_NAME = 'users' AND COLUMN_NAME = 'qr_code');
SET @qr_sql = IF(@qr_exists = 0, 'ALTER TABLE users ADD COLUMN qr_code VARCHAR(500)', 'SELECT "qr_code exists"');
PREPARE qr_stmt FROM @qr_sql;
EXECUTE qr_stmt;
DEALLOCATE PREPARE qr_stmt;

-- Step 6: Re-enable constraints
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- =====================================================
-- VERIFICATION
-- =====================================================

SELECT '========== DOCTORS TABLE ==========';
SELECT doctor_id, name, email, specialization, consultation_fee FROM doctors;

SELECT '========== DOCTOR COUNT ==========';
SELECT COUNT(*) as total_doctors FROM doctors;

SELECT '========== CHECK FOR DUPLICATES ==========';
SELECT doctor_id, COUNT(*) as count FROM doctors GROUP BY doctor_id HAVING COUNT(*) > 1;

SELECT '========== HEX CHECK (Should show readable UUIDs) ==========';
SELECT doctor_id, HEX(doctor_id) as hex_value, LENGTH(doctor_id) as length FROM doctors;

SELECT '========== APPOINTMENTS COUNT ==========';
SELECT COUNT(*) as total_appointments FROM appointments;

-- =====================================================
-- SUCCESS! Now do these steps:
-- 1. Close MySQL Workbench
-- 2. Stop backend (Ctrl+C in java terminal)
-- 3. Delete: F:\MediWay\backend\target folder
-- 4. Rebuild: .\mvnw.cmd clean install
-- 5. Start: .\mvnw.cmd spring-boot:run
-- =====================================================
