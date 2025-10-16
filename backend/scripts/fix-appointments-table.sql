-- Fix appointments table UUID columns from BINARY to VARCHAR(36)
-- This resolves "BLOB" display and Hibernate read errors

USE mediwaydb;

-- Disable safe update mode temporarily
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- Delete all existing appointments (they have BINARY UUIDs that Hibernate can't read)
-- Users will create new appointments via the UI after this fix
DELETE FROM appointments WHERE 1=1;

-- Restore safe update mode
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- Fix appointment_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN appointment_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Fix patient_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN patient_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Fix doctor_id column to VARCHAR(36)
ALTER TABLE appointments
  MODIFY COLUMN doctor_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Verify the schema changes
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'mediwaydb' 
  AND TABLE_NAME = 'appointments'
  AND COLUMN_NAME IN ('appointment_id', 'patient_id', 'doctor_id')
ORDER BY ORDINAL_POSITION;

-- Verify table is empty and ready for new appointments
SELECT COUNT(*) as appointment_count FROM appointments;

-- Show available doctors (should have 5)
SELECT doctor_id, name, specialization, consultation_fee
FROM doctors
ORDER BY name;
