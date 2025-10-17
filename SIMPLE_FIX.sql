-- ========================================================
-- SIMPLE FIX - Run this in MySQL Workbench
-- ========================================================

USE mediwaydb;

-- Temporarily disable constraints
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. BACKUP CURRENT DOCTORS (just in case)
CREATE TABLE IF NOT EXISTS doctors_backup AS SELECT * FROM doctors;

-- 2. DELETE ALL DOCTORS (we'll recreate them)
TRUNCATE TABLE doctors;

-- 3. CREATE FRESH DOCTORS WITH PROPER UUIDs
INSERT INTO doctors (doctor_id, name, email, specialization, consultation_fee, available, available_from, available_to, created_at, updated_at)
VALUES 
    ('51492852-aa4a-11f0-8da8-089798c3ec81', 'Dr. Sarah Johnson', 'sarah.johnson@mediway.com', 'Cardiology', 150.00, TRUE, '09:00:00', '17:00:00', NOW(), NOW()),
    ('51492852-aa4a-11f0-8da8-089798c3ec82', 'Dr. Michael Chen', 'michael.chen@mediway.com', 'Pediatrics', 120.00, TRUE, '08:00:00', '16:00:00', NOW(), NOW()),
    ('51492852-aa4a-11f0-8da8-089798c3ec83', 'Dr. Emily Rodriguez', 'emily.rodriguez@mediway.com', 'Dermatology', 130.00, TRUE, '10:00:00', '18:00:00', NOW(), NOW());

-- 4. CLEAN UP INVALID APPOINTMENTS
DELETE FROM appointments WHERE doctor_id NOT IN (SELECT doctor_id FROM doctors);

-- 5. ADD QR CODE COLUMN IF MISSING
SET @column_check = (
    SELECT COUNT(*) 
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = 'mediwaydb' 
    AND TABLE_NAME = 'users' 
    AND COLUMN_NAME = 'qr_code'
);

SET @sql_add_column = IF(@column_check = 0, 
    'ALTER TABLE users ADD COLUMN qr_code VARCHAR(500)', 
    'SELECT ''qr_code column already exists'''
);

PREPARE stmt FROM @sql_add_column;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Re-enable constraints
SET FOREIGN_KEY_CHECKS = 1;
SET SQL_SAFE_UPDATES = 1;

-- VERIFY RESULTS
SELECT '========== DOCTORS CREATED ==========' as '';
SELECT doctor_id, name, email, specialization, consultation_fee FROM doctors;

SELECT '========== DOCTOR COUNT ==========' as '';
SELECT COUNT(*) as total_doctors FROM doctors;

SELECT '========== CHECK FOR DUPLICATES ==========' as '';
SELECT doctor_id, COUNT(*) as count FROM doctors GROUP BY doctor_id HAVING COUNT(*) > 1;

-- SUCCESS MESSAGE
SELECT '✓ Fix complete! Now restart your backend.' as '';
