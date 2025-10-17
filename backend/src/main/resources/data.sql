-- Seed patient data for testing
-- Use INSERT IGNORE to avoid duplicate key errors if data already exists

INSERT IGNORE INTO users (name, email, password, phone, date_of_birth, gender, role, created_at)
VALUES 
  ('John Smith', 'john.smith@example.com', 'password123', '0771234567', '1990-05-15', 'Male', 'PATIENT', NOW()),
  ('Emily Johnson', 'emily.johnson@example.com', 'password123', '0772345678', '1985-08-22', 'Female', 'PATIENT', NOW()),
  ('Michael Brown', 'michael.brown@example.com', 'password123', '0773456789', '1992-11-30', 'Male', 'PATIENT', NOW()),
  ('Shiransha Fernando', 'shiransha@example.com', 'password123', '0774567890', '1988-03-10', 'Female', 'PATIENT', NOW()),
  ('David Wilson', 'david.wilson@example.com', 'password123', '0775678901', '1995-07-18', 'Male', 'PATIENT', NOW());

-- Seed doctor data for testing
INSERT IGNORE INTO doctors (name, specialization, email, phone, password, available, created_at)
VALUES 
  ('Dr. Sarah Johnson', 'Cardiology', 'sarah.johnson@mediway.com', '0776543210', 'doctor123', 1, NOW());

-- Seed appointment data for testing (assumes doctor_id=1 and patient_ids 1-5 exist)
-- Note: These will only insert if the IDs exist in users and doctors tables
INSERT IGNORE INTO appointments (patient_id, doctor_id, appointment_date, status, notes, created_at)
VALUES 
  (1, 1, '2025-10-23 13:21:00', 'COMPLETED', 'Regular checkup completed', NOW()),
  (1, 1, '2025-10-30 13:30:00', 'SCHEDULED', 'Follow-up appointment', NOW()),
  (1, 1, '2025-10-31 14:30:00', 'SCHEDULED', NULL, NOW()),
  (1, 1, '2025-10-23 09:00:00', 'COMPLETED', 'Morning consultation', NOW()),
  (4, 1, '2025-10-29 13:54:00', 'CANCELLED', 'Patient requested cancellation', NOW()),
  (4, 1, '2025-10-31 15:30:00', 'CANCELLED', 'Rescheduled to another date', NOW()),
  (5, 1, '2025-10-22 14:00:00', 'COMPLETED', 'Initial consultation', NOW());
