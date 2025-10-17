-- Migration script for receipts table
-- This script creates the receipts table if it doesn't exist

DELIMITER $$

DROP PROCEDURE IF EXISTS add_receipts_table$$

CREATE PROCEDURE add_receipts_table()
BEGIN
    DECLARE table_count INT DEFAULT 0;
    
    -- Check if receipts table exists
    SELECT COUNT(*) INTO table_count 
    FROM information_schema.tables 
    WHERE table_schema = DATABASE() 
    AND table_name = 'receipts';
    
    -- Create receipts table if it doesn't exist
    IF table_count = 0 THEN
        CREATE TABLE receipts (
            id BIGINT AUTO_INCREMENT PRIMARY KEY,
            receipt_number VARCHAR(20) UNIQUE NOT NULL,
            payment_id BIGINT NOT NULL,
            user_id BIGINT NOT NULL,
            appointment_id BIGINT NOT NULL,
            amount DECIMAL(10, 2) NOT NULL,
            payment_method VARCHAR(50),
            transaction_id VARCHAR(100),
            patient_name VARCHAR(100) NOT NULL,
            patient_email VARCHAR(100),
            doctor_name VARCHAR(100),
            service_description VARCHAR(500),
            issue_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
            qr_code VARCHAR(200),
            INDEX idx_receipt_number (receipt_number),
            INDEX idx_payment_id (payment_id),
            INDEX idx_user_id (user_id),
            INDEX idx_appointment_id (appointment_id),
            INDEX idx_issue_date (issue_date)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
        
        SELECT 'Receipts table created successfully' as message;
    ELSE
        SELECT 'Receipts table already exists' as message;
    END IF;
    
END$$

DELIMITER ;

-- Execute the procedure
CALL add_receipts_table();

-- Clean up
DROP PROCEDURE add_receipts_table;