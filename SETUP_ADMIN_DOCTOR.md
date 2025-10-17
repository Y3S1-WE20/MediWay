# ================================================
# QUICK FIX: Setup Admin and Doctor Users
# ================================================
# 
# Run these commands in MySQL Workbench or any MySQL client:

USE mediway_db;

# Delete existing test accounts
DELETE FROM users WHERE email IN ('admin@mediway.com', 'dr.smith@mediway.com', 'dr.johnson@mediway.com', 'dr.williams@mediway.com');

# Create Admin User
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES ('Admin', 'admin@mediway.com', 'Admin123', '0771052042', 'ADMIN', NOW());

# Create Doctor Users
INSERT INTO users (name, email, password, phone, role, created_at) 
VALUES 
  ('Dr. Smith', 'dr.smith@mediway.com', 'Doctor123', '555-0001', 'DOCTOR', NOW()),
  ('Dr. Johnson', 'dr.johnson@mediway.com', 'Doctor123', '555-0002', 'DOCTOR', NOW()),
  ('Dr. Williams', 'dr.williams@mediway.com', 'Doctor123', '555-0003', 'DOCTOR', NOW());

# Verify
SELECT id, name, email, role, phone FROM users WHERE role IN ('ADMIN', 'DOCTOR');

# ================================================
# LOGIN CREDENTIALS:
# ================================================
# ADMIN:
#   Email: admin@mediway.com
#   Password: Admin123
#
# DOCTORS:
#   Dr. Smith - dr.smith@mediway.com / Doctor123
#   Dr. Johnson - dr.johnson@mediway.com / Doctor123  
#   Dr. Williams - dr.williams@mediway.com / Doctor123
#
# PATIENT:
#   Email: tester1@gmail.com
#   Password: 123456
# ================================================
