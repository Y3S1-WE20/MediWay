-- Clinical module DDL (MySQL 8+). Uses UUID as BINARY(16) for keys.
-- Run after the base `medical_records` table exists.

-- Helper: ensure consistent charset/engine
SET NAMES utf8mb4;

-- 1) Diagnoses
CREATE TABLE IF NOT EXISTS diagnoses (
  diagnosis_id       BINARY(16)      NOT NULL,
  record_id          BINARY(16)      NOT NULL,
  diagnosis_code     VARCHAR(64)     NOT NULL, -- ICD-10 code
  description        TEXT            NOT NULL,
  severity           ENUM('MILD','MODERATE','SEVERE','CRITICAL') NOT NULL DEFAULT 'MILD',
  status             ENUM('ACTIVE','RESOLVED','RECURRENT')       NOT NULL DEFAULT 'ACTIVE',
  created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT pk_diagnoses PRIMARY KEY (diagnosis_id),
  CONSTRAINT fk_diagnoses_record
    FOREIGN KEY (record_id) REFERENCES medical_records (record_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT chk_diagnoses_code_nonempty CHECK (diagnosis_code <> '')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_diagnoses_record ON diagnoses (record_id);
CREATE INDEX idx_diagnoses_code   ON diagnoses (diagnosis_code);

-- 2) Treatments
CREATE TABLE IF NOT EXISTS treatments (
  treatment_id       BINARY(16)      NOT NULL,
  record_id          BINARY(16)      NOT NULL,
  treatment_name     VARCHAR(128)    NOT NULL,
  description        TEXT            NOT NULL,
  duration           VARCHAR(64)     NULL,
  frequency          VARCHAR(64)     NULL,
  created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT pk_treatments PRIMARY KEY (treatment_id),
  CONSTRAINT fk_treatments_record
    FOREIGN KEY (record_id) REFERENCES medical_records (record_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT chk_treatment_name_nonempty CHECK (treatment_name <> '')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_treatments_record ON treatments (record_id);

-- 3) Prescriptions
CREATE TABLE IF NOT EXISTS prescriptions (
  prescription_id    BINARY(16)      NOT NULL,
  record_id          BINARY(16)      NOT NULL,
  medication_name    VARCHAR(128)    NOT NULL,
  dosage             VARCHAR(64)     NOT NULL,
  frequency          VARCHAR(64)     NOT NULL,
  start_date         DATE            NULL,
  end_date           DATE            NULL,
  refills_remaining  INT UNSIGNED    NOT NULL DEFAULT 0,
  created_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at         TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT pk_prescriptions PRIMARY KEY (prescription_id),
  CONSTRAINT fk_prescriptions_record
    FOREIGN KEY (record_id) REFERENCES medical_records (record_id)
    ON UPDATE CASCADE ON DELETE CASCADE,
  CONSTRAINT chk_prescription_dates CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date),
  CONSTRAINT chk_refills_nonnegative CHECK (refills_remaining >= 0),
  CONSTRAINT chk_medication_nonempty CHECK (medication_name <> '')
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE INDEX idx_prescriptions_record     ON prescriptions (record_id);
CREATE INDEX idx_prescriptions_medication ON prescriptions (medication_name);

-- Notes:
-- * Keys are BINARY(16) to align with Hibernate UUID mapping.
-- * ON DELETE CASCADE propagates removal of a medical record to child clinical rows.
-- * CHECK constraints require MySQL 8.0.16+.
-- * Consider adding application-level validation for ICD-10 patterns if needed.



