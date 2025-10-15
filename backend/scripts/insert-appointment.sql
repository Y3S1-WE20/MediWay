-- Insert a test appointment for testing the booking flow
-- This script finds a patient by email and a doctor by email, then creates a PENDING appointment

-- Set patient and doctor emails (change these to match your test data)
SET @patient_email = 'tester1@gmail.com' COLLATE utf8mb4_unicode_ci;
SET @doctor_email = 'sarah.johnson@mediway.com' COLLATE utf8mb4_unicode_ci;

-- Set appointment details
SET @appointment_date = '2025-10-25';  -- Change to desired date (YYYY-MM-DD)
SET @appointment_time = '10:00:00';    -- Change to desired time (HH:MM:SS)
SET @reason = 'Regular checkup and consultation';
SET @consultation_fee = 150.00;

-- Insert the appointment
INSERT INTO appointments (
  appointment_id,
  patient_id,
  doctor_id,
  appointment_date,
  appointment_time,
  status,
  reason,
  notes,
  consultation_fee,
  created_at,
  updated_at
)
SELECT
  UNHEX(REPLACE(UUID(), '-', '')) AS appointment_id,
  u.user_id AS patient_id,
  d.doctor_id,
  @appointment_date AS appointment_date,
  @appointment_time AS appointment_time,
  'PENDING' AS status,
  @reason AS reason,
  NULL AS notes,
  @consultation_fee AS consultation_fee,
  NOW() AS created_at,
  NOW() AS updated_at
FROM users u
CROSS JOIN doctors d
WHERE u.email COLLATE utf8mb4_unicode_ci = @patient_email
  AND d.email COLLATE utf8mb4_unicode_ci = @doctor_email;

-- Verify the appointment was created
SELECT 
  BIN_TO_UUID(a.appointment_id) as appointment_id,
  BIN_TO_UUID(a.patient_id) as patient_id,
  u.email as patient_email,
  BIN_TO_UUID(a.doctor_id) as doctor_id,
  d.name as doctor_name,
  d.specialization,
  a.appointment_date,
  a.appointment_time,
  a.status,
  a.reason,
  a.consultation_fee,
  a.created_at
FROM appointments a
JOIN users u ON a.patient_id = u.user_id
JOIN doctors d ON a.doctor_id = d.doctor_id
WHERE u.email COLLATE utf8mb4_unicode_ci = @patient_email
ORDER BY a.created_at DESC
LIMIT 5;
