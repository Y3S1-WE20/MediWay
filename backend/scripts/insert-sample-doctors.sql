-- Insert sample doctors for testing appointment booking
-- Run this script after starting the backend to populate the doctors table

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
(
  UNHEX(REPLACE(UUID(), '-', '')),
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
  UNHEX(REPLACE(UUID(), '-', '')),
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
  UNHEX(REPLACE(UUID(), '-', '')),
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
  UNHEX(REPLACE(UUID(), '-', '')),
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
  UNHEX(REPLACE(UUID(), '-', '')),
  'Dr. Priya Patel',
  'General Medicine',
  'priya.patel@mediway.com',
  '+1-555-0105',
  'MBBS, MD (General Medicine)',
  7,
  100.00,
  true,
  NOW(),
  NOW()
);

-- Verify inserted doctors
SELECT 
  BIN_TO_UUID(doctor_id) as doctor_id,
  name,
  specialization,
  consultation_fee,
  experience_years
FROM doctors
ORDER BY created_at DESC;
