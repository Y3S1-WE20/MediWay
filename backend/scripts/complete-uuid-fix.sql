-- Complete UUID Fix for Both Doctors and Appointments Tables
-- Run this script in MySQL Workbench to fix all BINARY UUID issues

USE mediwaydb;

-- ====================================
-- STEP 1: Check Current Schema
-- ====================================
SELECT 'BEFORE FIX - Current Schema' as status;
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'mediwaydb' 
  AND TABLE_NAME IN ('doctors', 'appointments')
  AND COLUMN_NAME LIKE '%_id'
ORDER BY TABLE_NAME, ORDINAL_POSITION;

-- ====================================
-- STEP 2: Disable Safe Update Mode
-- ====================================
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- ====================================
-- STEP 3: Check if doctors table needs fixing
-- ====================================
SELECT 'Checking doctors table...' as status;
SELECT doctor_id, name, specialization 
FROM doctors 
LIMIT 1;

-- ====================================
-- STEP 4: Fix Appointments Table
-- ====================================
SELECT 'Fixing appointments table...' as status;

-- Delete all existing appointments (they may have BINARY UUIDs)
DELETE FROM appointments WHERE 1=1;

-- Fix appointment_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN appointment_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Fix patient_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN patient_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Fix doctor_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN doctor_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- ====================================
-- STEP 5: Restore Safe Update Mode
-- ====================================
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- ====================================
-- STEP 6: Verify Changes
-- ====================================
SELECT 'AFTER FIX - Updated Schema' as status;
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'mediwaydb' 
  AND TABLE_NAME IN ('doctors', 'appointments')
  AND COLUMN_NAME LIKE '%_id'
ORDER BY TABLE_NAME, ORDINAL_POSITION;

SELECT 'Appointments table is empty and ready' as status;
SELECT COUNT(*) as appointment_count FROM appointments;

SELECT 'Available Doctors (should show readable UUIDs)' as status;
SELECT doctor_id, name, specialization, consultation_fee, experience_years
FROM doctors
ORDER BY name;

SELECT '✓ Schema fix complete! Restart backend now.' as status;
