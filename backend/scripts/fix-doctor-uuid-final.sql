-- SQL Script to Fix Doctor UUID Format Issues
-- Run this in MySQL Workbench to permanently fix the doctor_id storage format

-- Step 1: Check current doctor_id format
SELECT doctor_id, name, HEX(doctor_id) as hex_value, LENGTH(doctor_id) as length 
FROM doctors;

-- Step 2: Update all doctor records to use proper dashed UUID format
-- This converts hex strings to properly formatted UUIDs
UPDATE doctors 
SET doctor_id = CONCAT(
    SUBSTR(HEX(doctor_id), 1, 8), '-',
    SUBSTR(HEX(doctor_id), 9, 4), '-',
    SUBSTR(HEX(doctor_id), 13, 4), '-',
    SUBSTR(HEX(doctor_id), 17, 4), '-',
    SUBSTR(HEX(doctor_id), 21, 12)
)
WHERE doctor_id NOT LIKE '%-%' AND LENGTH(HEX(doctor_id)) = 32;

-- Step 3: For any remaining problematic records, set specific UUIDs
UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec81' 
WHERE name = 'Dr. Sarah Johnson';

UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec82' 
WHERE name = 'Dr. Michael Chen';

UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec83' 
WHERE name = 'Dr. Emily Rodriguez';

-- Step 4: Verify the fix
SELECT doctor_id, name, email, specialization 
FROM doctors 
ORDER BY name;

-- Step 5: Clean up any appointments with old doctor_id references
UPDATE appointments a
INNER JOIN doctors d ON d.name LIKE CONCAT('%', SUBSTRING(a.doctor_id, 1, 10), '%')
SET a.doctor_id = d.doctor_id
WHERE a.doctor_id NOT LIKE '%-%' OR LENGTH(a.doctor_id) > 36;

-- Step 6: Verify appointments are linked correctly
SELECT a.appointment_id, a.doctor_id, d.name as doctor_name, a.appointment_date
FROM appointments a
LEFT JOIN doctors d ON a.doctor_id = d.doctor_id
ORDER BY a.appointment_date DESC;
