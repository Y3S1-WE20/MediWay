-- Insert sample medical records for testing
-- Note: These UUIDs should match existing users and doctors in the system

-- First, let's insert some sample users if they don't exist
INSERT INTO users (user_id, full_name, email, password_hash, phone, role, is_active, created_at, updated_at) 
VALUES 
('123e4567-e89b-12d3-a456-426614174001', 'John Doe', 'john.doe@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhUz2J4lDd1V4D1V4D1V4D1V4', '+1234567890', 'PATIENT', true, NOW(), NOW()),
('123e4567-e89b-12d3-a456-426614174002', 'Dr. Jane Smith', 'dr.smith@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKyVhUz2J4lDd1V4D1V4D1V4D1V4', '+1234567891', 'DOCTOR', true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Insert sample doctors if they don't exist
INSERT INTO doctors (doctor_id, name, specialization, email, phone, qualification, experience_years, consultation_fee, available, created_at, updated_at)
VALUES 
('123e4567-e89b-12d3-a456-426614174002', 'Dr. Jane Smith', 'Cardiology', 'dr.smith@example.com', '+1234567891', 'MD Cardiology', 10, 150.00, true, NOW(), NOW())
ON DUPLICATE KEY UPDATE updated_at = NOW();

-- Insert sample medical records
INSERT INTO medical_records (record_id, patient_id, doctor_id, diagnosis, medications, notes, created_at, updated_at)
VALUES 
('123e4567-e89b-12d3-a456-426614174010', '123e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174002', 'Hypertension', 'Lisinopril 10mg daily, Amlodipine 5mg daily', 'Patient shows good response to current medication. Blood pressure well controlled.', NOW(), NOW()),
('123e4567-e89b-12d3-a456-426614174011', '123e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174002', 'Type 2 Diabetes', 'Metformin 500mg twice daily, Glipizide 5mg daily', 'Blood sugar levels improving. Continue current treatment. Follow up in 3 months.', NOW(), NOW()),
('123e4567-e89b-12d3-a456-426614174012', '123e4567-e89b-12d3-a456-426614174001', '123e4567-e89b-12d3-a456-426614174002', 'Annual Checkup', 'Multivitamin daily', 'Overall health is good. Continue healthy lifestyle and regular exercise.', NOW(), NOW());

-- Verify the data
SELECT 
    mr.record_id,
    u.full_name as patient_name,
    d.name as doctor_name,
    mr.diagnosis,
    mr.medications,
    mr.notes,
    mr.created_at
FROM medical_records mr
JOIN users u ON mr.patient_id = u.user_id
JOIN doctors d ON mr.doctor_id = d.doctor_id
ORDER BY mr.created_at DESC;
