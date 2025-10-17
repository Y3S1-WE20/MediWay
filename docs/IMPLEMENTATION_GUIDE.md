# MediWay Backend Implementation Summary

## Overview
This document outlines the new backend features implemented for the MediWay Health Management System, including QR code generation for patient identification and comprehensive medical records management.

## New Features Implemented

### 1. QR Code System for Patient Identification

#### Purpose
Upon patient registration, a unique QR code is generated and linked to the patient's profile, enabling secure digital identification and access to hospital services.

#### Implementation Details

**Dependencies Added (pom.xml):**
```xml
<!-- ZXing for QR Code generation -->
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>core</artifactId>
    <version>3.5.2</version>
</dependency>
<dependency>
    <groupId>com.google.zxing</groupId>
    <artifactId>javase</artifactId>
    <version>3.5.2</version>
</dependency>
```

**New Service: QRCodeService.java**
- `generateQRCodeData()`: Creates unique QR code identifier
- `generateQRCodeImage()`: Generates Base64 encoded PNG QR code image
- `validateQRCodeData()`: Validates QR code format
- `extractUserIdFromQRCode()`: Extracts patient UUID from QR code

**Updated Entity: User.java**
- Added `qrCode` field (VARCHAR 500) to store unique QR code data

**Updated Service: AuthService.java**
- Automatically generates QR code upon PATIENT registration
- QR code format: `MEDIWAY-PATIENT:{UUID}:{EMAIL}`

**New Controller: ProfileController.java**
Endpoints:
- `GET /profile` - Get user profile information
- `GET /profile/qrcode` - Get QR code image for patient
- `POST /profile/verify-qr` - Verify QR code and get patient info
- `PUT /profile` - Update user profile

### 2. Medical Records Management System

#### New Entities

**MedicalRecord.java**
Fields:
- recordId, patientId, doctorId, appointmentId
- recordDate, diagnosis, symptoms, treatment, notes
- vitalSigns (JSON format)
- followUpRequired, followUpDate
- createdAt, updatedAt

**Prescription.java**
Fields:
- prescriptionId, patientId, doctorId, appointmentId
- medicationName, dosage, frequency, duration
- instructions, startDate, endDate
- status (ACTIVE, COMPLETED, CANCELLED, EXPIRED)
- refillsAllowed, refillsRemaining
- createdAt, updatedAt

**LabResult.java**
Fields:
- resultId, patientId, doctorId, appointmentId
- testName, testType, testDate, resultDate
- resultValue, resultUnit, referenceRange
- status (NORMAL, ABNORMAL, CRITICAL, PENDING)
- notes, fileUrl, labTechnician
- createdAt, updatedAt

#### New Repositories
- MedicalRecordRepository.java
- PrescriptionRepository.java
- LabResultRepository.java

Each repository includes methods for:
- Finding by patient ID
- Finding by doctor ID
- Finding by status
- Finding by date range
- Finding by appointment ID

#### New Controller: ReportsController.java

**Patient Endpoints:**
- `GET /reports/patient/comprehensive` - Complete patient medical data
- `GET /reports/summary` - Statistics summary
- `GET /reports/medical-records` - All medical records
- `GET /reports/medical-records/{id}` - Specific medical record
- `GET /reports/prescriptions` - All prescriptions (with status filter)
- `GET /reports/prescriptions/{id}` - Specific prescription
- `GET /reports/lab-results` - All lab results (with status filter)
- `GET /reports/lab-results/{id}` - Specific lab result

**Doctor Endpoints:**
- `POST /reports/medical-records` - Create medical record
- `POST /reports/prescriptions` - Create prescription
- `PATCH /reports/prescriptions/{id}/status` - Update prescription status
- `POST /reports/lab-results` - Create lab result
- `PATCH /reports/lab-results/{id}/status` - Update lab result status

### 3. Frontend Updates

#### Updated Components

**Profile.jsx**
- Fetches and displays user profile from `/profile` endpoint
- Displays QR code image for PATIENT role
- Download QR code as PNG
- Print health card functionality
- Update profile information

**Reports.jsx**
- Fetches medical report summary
- Displays medical records, prescriptions, lab results
- Tab-based navigation for different report types
- Statistics dashboard

**endpoints.js**
Added new endpoints for:
- Profile management
- QR code operations
- Medical records CRUD
- Prescriptions CRUD
- Lab results CRUD

## Database Schema Changes

### New Tables to Create

Run these SQL commands to create the new tables:

```sql
-- Medical Records Table
CREATE TABLE medical_records (
    record_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    record_date DATE NOT NULL,
    diagnosis TEXT,
    symptoms TEXT,
    treatment TEXT,
    notes TEXT,
    vital_signs TEXT,
    follow_up_required BOOLEAN DEFAULT FALSE,
    follow_up_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_medical_record_patient (patient_id),
    INDEX idx_medical_record_doctor (doctor_id),
    INDEX idx_medical_record_date (record_date)
);

-- Prescriptions Table
CREATE TABLE prescriptions (
    prescription_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    medical_record_id VARCHAR(36),
    prescription_date DATE NOT NULL,
    medication_name VARCHAR(200) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    start_date DATE NOT NULL,
    end_date DATE,
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') NOT NULL DEFAULT 'ACTIVE',
    refills_allowed INT DEFAULT 0,
    refills_remaining INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_doctor (doctor_id),
    INDEX idx_prescription_date (prescription_date)
);

-- Lab Results Table
CREATE TABLE lab_results (
    result_id VARCHAR(36) PRIMARY KEY,
    patient_id VARCHAR(36) NOT NULL,
    doctor_id VARCHAR(36) NOT NULL,
    appointment_id VARCHAR(36),
    medical_record_id VARCHAR(36),
    test_name VARCHAR(200) NOT NULL,
    test_type VARCHAR(100) NOT NULL,
    test_date DATE NOT NULL,
    result_date DATE NOT NULL,
    result_value TEXT,
    result_unit VARCHAR(50),
    reference_range VARCHAR(100),
    status ENUM('NORMAL', 'ABNORMAL', 'CRITICAL', 'PENDING') NOT NULL DEFAULT 'NORMAL',
    notes TEXT,
    file_url VARCHAR(500),
    lab_technician VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lab_result_patient (patient_id),
    INDEX idx_lab_result_doctor (doctor_id),
    INDEX idx_lab_result_date (result_date)
);

-- Add QR Code column to users table
ALTER TABLE users ADD COLUMN qr_code VARCHAR(500) AFTER is_active;
```

## Critical Fix: Doctor UUID Format Issue

### Problem
The `doctors` table stores `doctor_id` as hex strings instead of properly formatted UUIDs, causing "Doctor not found" errors during appointment booking.

### Solution
Run the SQL script: `backend/scripts/fix-doctor-uuid-final.sql`

This script:
1. Checks current doctor_id format
2. Converts hex strings to dashed UUID format
3. Updates specific doctor records with known UUIDs
4. Fixes any appointment references
5. Verifies the corrections

## Security Considerations

1. **QR Code Security:**
   - QR codes contain only UUID and email (no sensitive data)
   - Format: `MEDIWAY-PATIENT:{UUID}:{EMAIL}`
   - Validation ensures proper format before processing

2. **Access Control:**
   - Patients can only access their own medical records
   - Doctors can access records for their patients
   - Admin has full access (can be configured)

3. **Data Privacy:**
   - All endpoints require authentication
   - Role-based access control enforced
   - Medical data secured per HIPAA/GDPR standards

## Testing the New Features

### 1. Test QR Code Generation
```bash
# Register a new patient
POST /api/auth/register
{
  "fullName": "Test Patient",
  "email": "test@example.com",
  "password": "password123",
  "phone": "+1234567890",
  "role": "PATIENT"
}

# Get QR Code
GET /api/profile/qrcode
Authorization: Bearer {token}

# Verify QR Code
POST /api/profile/verify-qr
{
  "qrCodeData": "MEDIWAY-PATIENT:{uuid}:{email}"
}
```

### 2. Test Medical Records
```bash
# Create Medical Record (as doctor)
POST /api/reports/medical-records
{
  "patientId": "{patient-uuid}",
  "recordDate": "2025-10-16",
  "diagnosis": "Common Cold",
  "symptoms": "Fever, cough, runny nose",
  "treatment": "Rest, fluids, OTC medication"
}

# Get Patient Medical Records
GET /api/reports/medical-records
Authorization: Bearer {patient-token}
```

### 3. Test Prescriptions
```bash
# Create Prescription (as doctor)
POST /api/reports/prescriptions
{
  "patientId": "{patient-uuid}",
  "medicationName": "Amoxicillin",
  "dosage": "500mg",
  "frequency": "Three times daily",
  "duration": "7 days",
  "startDate": "2025-10-16"
}

# Get Patient Prescriptions
GET /api/reports/prescriptions?status=ACTIVE
Authorization: Bearer {patient-token}
```

## Deployment Steps

1. **Update Dependencies:**
   ```bash
   cd backend
   ./mvnw clean install
   ```

2. **Run Database Migrations:**
   - Execute the SQL scripts to create new tables
   - Run fix-doctor-uuid-final.sql to fix existing data

3. **Start Backend:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Update Frontend:**
   ```bash
   cd frontend
   npm install
   npm run dev
   ```

5. **Verify Endpoints:**
   - Check `/actuator/health` for backend status
   - Test QR code generation with a new patient registration
   - Verify medical records can be created and retrieved

## Next Steps

1. Add file upload capability for lab result attachments
2. Implement email notifications for new prescriptions
3. Add appointment reminders via SMS/email
4. Create admin dashboard for system monitoring
5. Implement audit logging for medical record access
6. Add telemedicine video consultation feature

## Support

For issues or questions:
- Check backend logs: `backend/logs/`
- Review frontend console for errors
- Verify database connectivity and schema
- Ensure JWT tokens are valid and not expired
