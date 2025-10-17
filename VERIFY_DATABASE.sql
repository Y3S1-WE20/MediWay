-- =====================================================
-- VERIFY DATABASE STATE
-- Run this to see what's actually in your database
-- =====================================================

-- Check which database you're using
SELECT DATABASE() AS current_database;

-- Check if mediwaydb exists
SHOW DATABASES LIKE 'mediwaydb';

-- Switch to mediwaydb
USE mediwaydb;

-- Show all tables
SHOW TABLES;

-- Check doctors table structure (should have doctor_id as CHAR(36))
DESCRIBE doctors;

-- Check payments table structure (should have approval_url column)
DESCRIBE payments;

-- Check appointments table structure
DESCRIBE appointments;

-- Check users table structure
DESCRIBE users;

-- Count doctors (should be 3 after FRESH_START.sql)
SELECT COUNT(*) AS doctor_count FROM doctors;

-- Show doctor IDs (should be proper UUIDs, NOT hex-encoded)
SELECT doctor_id, name, LENGTH(doctor_id) AS id_length FROM doctors;

-- Check for hex-encoded IDs (should be 0)
SELECT COUNT(*) AS hex_encoded_count FROM doctors WHERE doctor_id LIKE '35313439%';
