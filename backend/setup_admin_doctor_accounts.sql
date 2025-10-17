-- Clear existing users and insert proper admin and doctor accounts
-- Note: This uses plain text passwords for simplicity during development

DELETE FROM users WHERE email IN ('admin@mediway.com', 'doctor@mediway.com', 'doctor1@gmail.com');

-- Insert Admin User
INSERT INTO users (name, email, password, phone, role, created_at) VALUES 
('Admin User', 'admin@mediway.com', 'admin123', '555-9999', 'ADMIN', NOW());

-- Insert Doctor Users
INSERT INTO users (name, email, password, phone, role, created_at) VALUES 
('Dr. John Smith', 'doctor@mediway.com', 'doctor123', '555-1001', 'DOCTOR', NOW());

INSERT INTO users (name, email, password, phone, role, created_at) VALUES 
('Dr. Sarah Johnson', 'doctor1@gmail.com', 'doctor123', '555-1002', 'DOCTOR', NOW());

-- Insert Sample Patient (for testing)
INSERT INTO users (name, email, password, phone, role, created_at) VALUES 
('Patient Test', 'patient@test.com', 'patient123', '555-2001', 'PATIENT', NOW());

-- Display the inserted users
SELECT id, name, email, password, role, created_at FROM users WHERE role IN ('ADMIN', 'DOCTOR') ORDER BY role, id;