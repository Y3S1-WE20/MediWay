-- =================================================================
-- MediWay Complete Database Setup Script
-- Run this script to set up all new tables and fix existing issues
-- =================================================================

-- Step 1: Add QR Code support to existing users table
-- =================================================================
-- Check if column exists before adding
SET @column_exists = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'mediwaydb' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'qr_code'
);

SET @sql = IF(@column_exists = 0, 
    'ALTER TABLE users ADD COLUMN qr_code VARCHAR(500) COMMENT ''Stores unique QR code data for patient identification''', 
    'SELECT ''Column qr_code already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Step 2: Create Medical Records Table
-- =================================================================
CREATE TABLE IF NOT EXISTS medical_records (
    record_id VARCHAR(36) PRIMARY KEY COMMENT 'Unique medical record identifier',
    patient_id VARCHAR(36) NOT NULL COMMENT 'Reference to patient in users table',
    doctor_id VARCHAR(36) NOT NULL COMMENT 'Reference to doctor in users table',
    appointment_id VARCHAR(36) COMMENT 'Reference to related appointment',
    record_date DATE NOT NULL COMMENT 'Date of medical record',
    diagnosis TEXT COMMENT 'Medical diagnosis',
    symptoms TEXT COMMENT 'Patient symptoms',
    treatment TEXT COMMENT 'Prescribed treatment',
    notes TEXT COMMENT 'Additional medical notes',
    vital_signs TEXT COMMENT 'Vital signs in JSON format',
    follow_up_required BOOLEAN DEFAULT FALSE COMMENT 'Whether follow-up is needed',
    follow_up_date DATE COMMENT 'Scheduled follow-up date',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_medical_record_patient (patient_id),
    INDEX idx_medical_record_doctor (doctor_id),
    INDEX idx_medical_record_date (record_date),
    INDEX idx_medical_record_appointment (appointment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Stores patient medical records and history';

-- Step 3: Create Prescriptions Table
-- =================================================================
CREATE TABLE IF NOT EXISTS prescriptions (
    prescription_id VARCHAR(36) PRIMARY KEY COMMENT 'Unique prescription identifier',
    patient_id VARCHAR(36) NOT NULL COMMENT 'Reference to patient in users table',
    doctor_id VARCHAR(36) NOT NULL COMMENT 'Reference to prescribing doctor',
    appointment_id VARCHAR(36) COMMENT 'Reference to related appointment',
    medical_record_id VARCHAR(36) COMMENT 'Reference to related medical record',
    prescription_date DATE NOT NULL COMMENT 'Date prescription was issued',
    medication_name VARCHAR(200) NOT NULL COMMENT 'Name of prescribed medication',
    dosage VARCHAR(100) NOT NULL COMMENT 'Dosage amount (e.g., 500mg)',
    frequency VARCHAR(100) NOT NULL COMMENT 'How often to take (e.g., twice daily)',
    duration VARCHAR(100) NOT NULL COMMENT 'Treatment duration (e.g., 7 days)',
    instructions TEXT COMMENT 'Additional instructions for patient',
    start_date DATE NOT NULL COMMENT 'When to start medication',
    end_date DATE COMMENT 'When to stop medication',
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    refills_allowed INT DEFAULT 0 COMMENT 'Number of refills allowed',
    refills_remaining INT DEFAULT 0 COMMENT 'Number of refills remaining',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_doctor (doctor_id),
    INDEX idx_prescription_date (prescription_date),
    INDEX idx_prescription_status (status),
    INDEX idx_prescription_appointment (appointment_id),
    INDEX idx_prescription_record (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Stores patient prescriptions';

-- Step 4: Create Lab Results Table
-- =================================================================
CREATE TABLE IF NOT EXISTS lab_results (
    result_id VARCHAR(36) PRIMARY KEY COMMENT 'Unique lab result identifier',
    patient_id VARCHAR(36) NOT NULL COMMENT 'Reference to patient in users table',
    doctor_id VARCHAR(36) NOT NULL COMMENT 'Reference to ordering doctor',
    appointment_id VARCHAR(36) COMMENT 'Reference to related appointment',
    medical_record_id VARCHAR(36) COMMENT 'Reference to related medical record',
    test_name VARCHAR(200) NOT NULL COMMENT 'Name of the test',
    test_type VARCHAR(100) NOT NULL COMMENT 'Type of test (Blood, X-Ray, MRI, etc)',
    test_date DATE NOT NULL COMMENT 'When test was performed',
    result_date DATE NOT NULL COMMENT 'When results were available',
    result_value TEXT COMMENT 'Test result value',
    result_unit VARCHAR(50) COMMENT 'Unit of measurement (mg/dL, mmol/L)',
    reference_range VARCHAR(100) COMMENT 'Normal range for this test',
    status ENUM('NORMAL', 'ABNORMAL', 'CRITICAL', 'PENDING') NOT NULL DEFAULT 'NORMAL',
    notes TEXT COMMENT 'Additional notes about results',
    file_url VARCHAR(500) COMMENT 'URL to uploaded result file/image',
    lab_technician VARCHAR(100) COMMENT 'Name of lab technician',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lab_result_patient (patient_id),
    INDEX idx_lab_result_doctor (doctor_id),
    INDEX idx_lab_result_date (result_date),
    INDEX idx_lab_result_status (status),
    INDEX idx_lab_result_appointment (appointment_id),
    INDEX idx_lab_result_record (medical_record_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci 
COMMENT='Stores patient laboratory test results';

-- Step 5: Fix Doctor UUID Format Issues
-- =================================================================
-- Disable safe update mode temporarily
SET SQL_SAFE_UPDATES = 0;

-- First, let's see what we have
SELECT 'Current Doctor IDs:' as info;
SELECT doctor_id, HEX(doctor_id) as hex_representation, LENGTH(doctor_id) as length, name, email 
FROM doctors;

-- Delete any existing doctors with malformed IDs
DELETE FROM doctors WHERE LENGTH(doctor_id) != 36 OR doctor_id NOT LIKE '%-%';

-- Insert doctors with proper UUID format
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available_from, available_to, created_at, updated_at)
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 'Dr. Sarah Johnson', 'sarah.johnson@mediway.com', 'Cardiology', 150.00, '09:00:00', '17:00:00', NOW(), NOW()),
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 'Dr. Michael Chen', 'michael.chen@mediway.com', 'Pediatrics', 120.00, '08:00:00', '16:00:00', NOW(), NOW()),
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 'Dr. Emily Rodriguez', 'emily.rodriguez@mediway.com', 'Dermatology', 130.00, '10:00:00', '18:00:00', NOW(), NOW())
ON DUPLICATE KEY UPDATE
    doctor_id = VALUES(doctor_id),
    name = VALUES(name),
    email = VALUES(email),
    specialization = VALUES(specialization),
    consultation_fee = VALUES(consultation_fee),
    available_from = VALUES(available_from),
    available_to = VALUES(available_to),
    updated_at = NOW();

-- Clean up any appointments with bad doctor references
DELETE FROM appointments WHERE doctor_id NOT IN (SELECT doctor_id FROM doctors);

-- Re-enable safe update mode
SET SQL_SAFE_UPDATES = 1;

-- Verify the fix
SELECT 'Fixed Doctor IDs:' as info;
SELECT doctor_id, LENGTH(doctor_id) as length, name, email, specialization 
FROM doctors;

-- Step 6: Insert Sample Data (Optional - for testing)
-- =================================================================

-- Sample Medical Record
INSERT INTO medical_records (
    record_id, patient_id, doctor_id, record_date,
    diagnosis, symptoms, treatment, vital_signs,
    follow_up_required, follow_up_date
) VALUES (
    UUID(),
    (SELECT user_id FROM users WHERE email = 'tester2@gmail.com' LIMIT 1),
    '51492852-aa4a-11f0-8da8-089798c3ec81',
    CURDATE(),
    'Common Cold',
    'Fever, runny nose, sore throat, cough',
    'Rest, plenty of fluids, OTC pain relievers',
    '{"temperature": "38.2°C", "bloodPressure": "120/80", "heartRate": "78 bpm"}',
    true,
    DATE_ADD(CURDATE(), INTERVAL 7 DAY)
);

-- Sample Prescription
INSERT INTO prescriptions (
    prescription_id, patient_id, doctor_id, prescription_date,
    medication_name, dosage, frequency, duration,
    instructions, start_date, status,
    refills_allowed, refills_remaining
) VALUES (
    UUID(),
    (SELECT user_id FROM users WHERE email = 'tester2@gmail.com' LIMIT 1),
    '51492852-aa4a-11f0-8da8-089798c3ec81',
    CURDATE(),
    'Amoxicillin',
    '500mg',
    'Three times daily',
    '7 days',
    'Take with food. Complete full course even if symptoms improve.',
    CURDATE(),
    'ACTIVE',
    0,
    0
);

-- Sample Lab Result
INSERT INTO lab_results (
    result_id, patient_id, doctor_id, test_date, result_date,
    test_name, test_type, result_value, result_unit,
    reference_range, status, notes
) VALUES (
    UUID(),
    (SELECT user_id FROM users WHERE email = 'tester2@gmail.com' LIMIT 1),
    '51492852-aa4a-11f0-8da8-089798c3ec81',
    DATE_SUB(CURDATE(), INTERVAL 2 DAY),
    DATE_SUB(CURDATE(), INTERVAL 1 DAY),
    'Complete Blood Count (CBC)',
    'Blood Test',
    '4.8 million cells/mcL',
    'cells/mcL',
    '4.5-5.9 million',
    'NORMAL',
    'All values within normal range'
);

-- Step 7: Verification Queries
-- =================================================================

-- Verify all tables exist
SELECT TABLE_NAME, TABLE_ROWS, CREATE_TIME 
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'mediwaydb' 
AND TABLE_NAME IN ('users', 'doctors', 'medical_records', 'prescriptions', 'lab_results')
ORDER BY TABLE_NAME;

-- Verify doctor IDs are fixed
SELECT doctor_id, name, email, specialization, consultation_fee 
FROM doctors 
ORDER BY name;

-- Verify users have qr_code column
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH 
FROM information_schema.COLUMNS 
WHERE TABLE_SCHEMA = 'mediwaydb' 
AND TABLE_NAME = 'users' 
AND COLUMN_NAME = 'qr_code';

-- Check sample data (if inserted)
SELECT 
    (SELECT COUNT(*) FROM medical_records) as medical_records_count,
    (SELECT COUNT(*) FROM prescriptions) as prescriptions_count,
    (SELECT COUNT(*) FROM lab_results) as lab_results_count;

-- =================================================================
-- Setup Complete!
-- =================================================================
-- Next steps:
-- 1. Restart your backend: .\mvnw.cmd spring-boot:run
-- 2. Test patient registration (QR code should be generated)
-- 3. Test appointment booking (should work without errors)
-- 4. Check Profile page for QR code display
-- 5. Visit Reports page to see medical data
-- =================================================================
