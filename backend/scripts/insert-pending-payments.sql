-- Insert two pending (CREATED) payments for a user identified by email
-- Replace 'test.patient@mediway.com' with the actual email if needed

-- Use a collation that matches the database/table collation to avoid Illegal mix of collations (Error 1267)
-- Change this value to the target user's email. If your users.email column uses utf8mb4_0900_ai_ci (MySQL 8 default), set the collation accordingly.
SET @user_email = 'tester1@gmail.com' COLLATE utf8mb4_unicode_ci;

INSERT INTO payments (
  payment_id,
  user_id,
  appointment_id,
  amount,
  currency,
  status,
  payment_method,
  paypal_payment_id,
  payer_id,
  description,
  return_url,
  cancel_url,
  approval_url,
  created_at
)
SELECT
  -- use binary(16) format if the column is BINARY(16); this avoids "Data too long" when column expects 16 bytes
  UNHEX(REPLACE(UUID(), '-', '')) AS payment_id,
  u.user_id,
  NULL AS appointment_id,
  50.00 AS amount,
  'USD' AS currency,
  'CREATED' AS status,
  'PAYPAL' AS payment_method,
  NULL AS paypal_payment_id,
  NULL AS payer_id,
  'Test pending payment 1 - Consultation' AS description,
  CONCAT('http://localhost:5174/payment-success?note=1&user=', BIN_TO_UUID(u.user_id)) AS return_url,
  CONCAT('http://localhost:5174/payment-cancel?note=1&user=', BIN_TO_UUID(u.user_id)) AS cancel_url,
  NULL AS approval_url,
  NOW() AS created_at
FROM users u
WHERE u.email COLLATE utf8mb4_unicode_ci = @user_email;

-- Second payment
INSERT INTO payments (
  payment_id,
  user_id,
  appointment_id,
  amount,
  currency,
  status,
  payment_method,
  paypal_payment_id,
  payer_id,
  description,
  return_url,
  cancel_url,
  approval_url,
  created_at
)
SELECT
  -- use binary(16) format for payment_id
  UNHEX(REPLACE(UUID(), '-', '')) AS payment_id,
  u.user_id,
  NULL AS appointment_id,
  120.50 AS amount,
  'USD' AS currency,
  'CREATED' AS status,
  'PAYPAL' AS payment_method,
  NULL AS paypal_payment_id,
  NULL AS payer_id,
  'Test pending payment 2 - Lab tests' AS description,
  CONCAT('http://localhost:5174/payment-success?note=2&user=', BIN_TO_UUID(u.user_id)) AS return_url,
  CONCAT('http://localhost:5174/payment-cancel?note=2&user=', BIN_TO_UUID(u.user_id)) AS cancel_url,
  NULL AS approval_url,
  NOW() AS created_at
FROM users u
WHERE u.email COLLATE utf8mb4_unicode_ci = @user_email;

-- Verify inserted rows (select last 5 payments for that user)
-- Verify inserted rows (select last 5 payments for that user)
SELECT * FROM payments WHERE user_id = (
  SELECT user_id FROM users WHERE email COLLATE utf8mb4_unicode_ci = @user_email
) ORDER BY created_at DESC LIMIT 5;
