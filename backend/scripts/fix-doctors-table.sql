-- Fix doctors table structure and data
-- This script will check and fix the doctor_id column type issue

-- First, check current table structure
SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_SCHEMA = 'mediwaydb' AND TABLE_NAME = 'doctors'
ORDER BY ORDINAL_POSITION;

-- Drop all existing doctors (they might have wrong UUID format)
-- Disable safe update mode temporarily so DELETE without primary-key WHERE can run in Workbench/CLI
-- (Some MySQL clients enable SQL_SAFE_UPDATES which blocks DELETE without key-based WHERE)
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET SQL_SAFE_UPDATES = 0;

-- Delete all rows from doctors (we'll re-insert correct rows below)
-- Using WHERE 1=1 makes intent explicit; safe-updates is temporarily disabled above.
DELETE FROM doctors WHERE 1=1;

-- Restore previous SQL_SAFE_UPDATES setting
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- Check if appointments reference any doctors (should be none after delete)
SELECT COUNT(*) as appointment_count FROM appointments WHERE doctor_id IS NOT NULL;

-- Ensure doctor_id column can hold string UUIDs (VARCHAR(36)).
-- If it is currently BINARY(16) or smaller, modify it to VARCHAR(36).
-- This ALTER is safe when table is empty; if it's not empty, it will change the column type.
ALTER TABLE doctors
  MODIFY COLUMN doctor_id VARCHAR(36) CHARACTER SET utf8mb4 NOT NULL;

-- Ensure doctor_id is primary key (add if missing)
SELECT COUNT(*) INTO @has_pk FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
 WHERE tc.TABLE_SCHEMA = 'mediwaydb' AND tc.TABLE_NAME = 'doctors' AND tc.CONSTRAINT_TYPE = 'PRIMARY KEY';
SET @pk_sql = IF(@has_pk = 0, 'ALTER TABLE doctors ADD PRIMARY KEY (doctor_id);', 'SELECT "PK_EXISTS";');
PREPARE stmt FROM @pk_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Now insert doctors with proper UUID format that Hibernate can read
-- Hibernate's UUID type works with VARCHAR(36) by default (string format with dashes)
INSERT INTO doctors (
  doctor_id,
  name,
  specialization,
  email,
  phone,
  qualification,
  experience_years,
  consultation_fee,
  available,
  created_at,
  updated_at
) VALUES
-- Generate UUIDs as strings (Hibernate's default UUID format)
(
  UUID(),
  'Dr. Sarah Johnson',
  'Cardiology',
  'sarah.johnson@mediway.com',
  '+1-555-0101',
  'MBBS, MD (Cardiology)',
  15,
  150.00,
  true,
  NOW(),
  NOW()
),
(
  UUID(),
  'Dr. Michael Chen',
  'Pediatrics',
  'michael.chen@mediway.com',
  '+1-555-0102',
  'MBBS, DCH (Pediatrics)',
  10,
  120.00,
  true,
  NOW(),
  NOW()
),
(
  UUID(),
  'Dr. Emily Rodriguez',
  'Dermatology',
  'emily.rodriguez@mediway.com',
  '+1-555-0103',
  'MBBS, MD (Dermatology)',
  8,
  130.00,
  true,
  NOW(),
  NOW()
),
(
  UUID(),
  'Dr. James Wilson',
  'Orthopedics',
  'james.wilson@mediway.com',
  '+1-555-0104',
  'MBBS, MS (Orthopedics)',
  12,
  140.00,
  true,
  NOW(),
  NOW()
),
(
  UUID(),
  'Dr. Priya Patel',
  'General Medicine',
  'priya.patel@mediway.com',
  '+1-555-0105',
  'MBBS, MD (Internal Medicine)',
  7,
  100.00,
  true,
  NOW(),
  NOW()
);

-- Verify the insert
SELECT 
  doctor_id,
  name,
  specialization,
  consultation_fee,
  available
FROM doctors
ORDER BY name;
