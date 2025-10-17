-- ================================================
-- MediWay User Setup Script
-- Creates Admin and Doctor accounts for login
-- ================================================

USE mediway_db;

-- First, let's check what we have
SELECT 'Current Users:' as Info;
SELECT id, name, email, role, created_at FROM users;

-- Delete existing admin/doctor test accounts if they exist
DELETE FROM users WHERE email IN ('admin@mediway.com', 'dr.smith@mediway.com', 'dr.johnson@mediway.com', 'dr.williams@mediway.com');

-- Create Admin User
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES ('Admin', 'admin@mediway.com', 'Admin123', '0771052042', 'ADMIN', NOW());

-- Create Doctor Users (linked to doctors table)
-- Dr. Smith
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES ('Dr. Smith', 'dr.smith@mediway.com', 'Doctor123', '555-0001', 'DOCTOR', NOW());

-- Dr. Johnson  
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES ('Dr. Johnson', 'dr.johnson@mediway.com', 'Doctor123', '555-0002', 'DOCTOR', NOW());

-- Dr. Williams
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES ('Dr. Williams', 'dr.williams@mediway.com', 'Doctor123', '555-0003', 'DOCTOR', NOW());

-- Verify the setup
SELECT 'After Setup - All Users:' as Info;
SELECT id, name, email, role, phone, created_at FROM users ORDER BY role, id;

SELECT 'Doctors Table:' as Info;
SELECT id, name, email, specialization, available FROM doctors;

-- Show login credentials
SELECT '==================================' as '';
SELECT 'LOGIN CREDENTIALS' as '';
SELECT '==================================' as '';
SELECT 'ADMIN LOGIN:' as '';
SELECT 'Email: admin@mediway.com' as '';
SELECT 'Password: Admin123' as '';
SELECT '' as '';
SELECT 'DOCTOR LOGINS:' as '';
SELECT 'Dr. Smith - Email: dr.smith@mediway.com, Password: Doctor123' as '';
SELECT 'Dr. Johnson - Email: dr.johnson@mediway.com, Password: Doctor123' as '';
SELECT 'Dr. Williams - Email: dr.williams@mediway.com, Password: Doctor123' as '';
SELECT '' as '';
SELECT 'PATIENT LOGIN:' as '';
SELECT 'Email: tester1@gmail.com, Password: 123456' as '';
SELECT '==================================' as '';
