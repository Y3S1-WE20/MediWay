-- MediWay Database Migration: Add Profile Enhancement Fields
-- Execute this script in MySQL Workbench or command line
-- Database: mediwaydb

USE mediwaydb;

-- This script makes the migration idempotent by creating and calling
-- small temporary stored procedures which add each column only if it
-- does not already exist. It is safe to re-run multiple times.

-- Ensure a consistent delimiter for procedures
DELIMITER $$

-- gender
DROP PROCEDURE IF EXISTS add_gender_column$$
CREATE PROCEDURE add_gender_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'gender'
	) THEN
		ALTER TABLE users ADD COLUMN gender VARCHAR(10) AFTER date_of_birth;
	END IF;
END$$
CALL add_gender_column();
DROP PROCEDURE IF EXISTS add_gender_column;

-- blood_type
DROP PROCEDURE IF EXISTS add_blood_type_column$$
CREATE PROCEDURE add_blood_type_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'blood_type'
	) THEN
		ALTER TABLE users ADD COLUMN blood_type VARCHAR(5) AFTER gender;
	END IF;
END$$
CALL add_blood_type_column();
DROP PROCEDURE IF EXISTS add_blood_type_column;

-- profile_picture
DROP PROCEDURE IF EXISTS add_profile_picture_column$$
CREATE PROCEDURE add_profile_picture_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'profile_picture'
	) THEN
		ALTER TABLE users ADD COLUMN profile_picture VARCHAR(500) AFTER blood_type;
	END IF;
END$$
CALL add_profile_picture_column();
DROP PROCEDURE IF EXISTS add_profile_picture_column;

-- address
DROP PROCEDURE IF EXISTS add_address_column$$
CREATE PROCEDURE add_address_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'address'
	) THEN
		ALTER TABLE users ADD COLUMN address VARCHAR(500) AFTER profile_picture;
	END IF;
END$$
CALL add_address_column();
DROP PROCEDURE IF EXISTS add_address_column;

-- emergency_contact
DROP PROCEDURE IF EXISTS add_emergency_contact_column$$
CREATE PROCEDURE add_emergency_contact_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'emergency_contact'
	) THEN
		ALTER TABLE users ADD COLUMN emergency_contact VARCHAR(100) AFTER address;
	END IF;
END$$
CALL add_emergency_contact_column();
DROP PROCEDURE IF EXISTS add_emergency_contact_column;

-- emergency_phone
DROP PROCEDURE IF EXISTS add_emergency_phone_column$$
CREATE PROCEDURE add_emergency_phone_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'emergency_phone'
	) THEN
		ALTER TABLE users ADD COLUMN emergency_phone VARCHAR(20) AFTER emergency_contact;
	END IF;
END$$
CALL add_emergency_phone_column();
DROP PROCEDURE IF EXISTS add_emergency_phone_column;

-- allergies
DROP PROCEDURE IF EXISTS add_allergies_column$$
CREATE PROCEDURE add_allergies_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'allergies'
	) THEN
		ALTER TABLE users ADD COLUMN allergies TEXT AFTER emergency_phone;
	END IF;
END$$
CALL add_allergies_column();
DROP PROCEDURE IF EXISTS add_allergies_column;

-- medications
DROP PROCEDURE IF EXISTS add_medications_column$$
CREATE PROCEDURE add_medications_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'medications'
	) THEN
		ALTER TABLE users ADD COLUMN medications TEXT AFTER allergies;
	END IF;
END$$
CALL add_medications_column();
DROP PROCEDURE IF EXISTS add_medications_column;

-- qr_code
DROP PROCEDURE IF EXISTS add_qr_code_column$$
CREATE PROCEDURE add_qr_code_column()
BEGIN
	IF NOT EXISTS (
		SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
		WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'users' AND COLUMN_NAME = 'qr_code'
	) THEN
		ALTER TABLE users ADD COLUMN qr_code VARCHAR(100) AFTER medications;
	END IF;
END$$
CALL add_qr_code_column();
DROP PROCEDURE IF EXISTS add_qr_code_column;

-- Restore default delimiter
DELIMITER ;

-- Verify the changes
DESCRIBE users;

-- Display success message
SELECT 'Migration completed successfully!' AS Status;
SELECT 'New columns added to users table:' AS Info;
SELECT 'gender, blood_type, profile_picture, address, emergency_contact, emergency_phone, allergies, medications, qr_code' AS NewColumns;

-- Verify the changes
DESCRIBE users;

-- Display success message
SELECT 'Migration completed successfully!' AS Status;
SELECT 'New columns added to users table:' AS Info;
SELECT 'gender, blood_type, profile_picture, address, emergency_contact, emergency_phone, allergies, medications, qr_code' AS NewColumns;
