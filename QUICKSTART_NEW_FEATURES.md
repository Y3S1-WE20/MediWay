# MediWay Quick Start Guide

## Immediate Actions Required

### 1. Fix the Doctor UUID Issue (CRITICAL)

**Problem:** Appointments fail with "Doctor not found" error due to UUID format mismatch.

**Solution:** Run this SQL script in MySQL Workbench:

```sql
-- Fix Doctor IDs to use proper UUID format
UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec81' WHERE name = 'Dr. Sarah Johnson';
UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec82' WHERE name = 'Dr. Michael Chen';
UPDATE doctors SET doctor_id = '51492852-aa4a-11f0-8da8-089798c3ec83' WHERE name = 'Dr. Emily Rodriguez';

-- Verify
SELECT doctor_id, name, email FROM doctors;
```

Alternatively, run the complete script: `backend/scripts/fix-doctor-uuid-final.sql`

### 2. Create New Database Tables

Run these SQL commands to add the new tables:

```sql
-- Add QR Code support to users table
ALTER TABLE users ADD COLUMN qr_code VARCHAR(500);

-- Medical Records Table
CREATE TABLE IF NOT EXISTS medical_records (
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
    INDEX idx_medical_record_doctor (doctor_id)
);

-- Prescriptions Table
CREATE TABLE IF NOT EXISTS prescriptions (
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
    status ENUM('ACTIVE', 'COMPLETED', 'CANCELLED', 'EXPIRED') DEFAULT 'ACTIVE',
    refills_allowed INT DEFAULT 0,
    refills_remaining INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_prescription_patient (patient_id),
    INDEX idx_prescription_doctor (doctor_id)
);

-- Lab Results Table
CREATE TABLE IF NOT EXISTS lab_results (
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
    status ENUM('NORMAL', 'ABNORMAL', 'CRITICAL', 'PENDING') DEFAULT 'NORMAL',
    notes TEXT,
    file_url VARCHAR(500),
    lab_technician VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lab_result_patient (patient_id),
    INDEX idx_lab_result_doctor (doctor_id)
);
```

### 3. Rebuild the Backend

```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean install
```

### 4. Start the Backend Server

```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run
```

Wait for the message: `Started MediWayBackendApplication in X seconds`

### 5. Start the Frontend (in a new terminal)

```powershell
cd F:\MediWay\frontend
npm run dev
```

## Testing the New Features

### Test 1: QR Code Generation (Patient Registration)

1. Go to http://localhost:5173/register
2. Register as a new PATIENT:
   - Full Name: Jane Doe
   - Email: jane.doe@example.com
   - Password: password123
   - Phone: +1234567890
   - Role: PATIENT
3. After registration, go to Profile page
4. You should see your QR code displayed
5. Click "Download QR" to save it

### Test 2: Appointment Booking

1. Login as patient (jane.doe@example.com)
2. Navigate to "Book Appointment"
3. Select a doctor (should now work without "Doctor not found" error)
4. Choose date and time
5. Fill in reason for visit
6. Submit appointment
7. Verify appointment appears in "My Appointments"

### Test 3: Medical Reports

1. Login as patient
2. Navigate to "Reports" page
3. You should see:
   - Summary with statistics
   - Medical Records (if any)
   - Prescriptions (if any)
   - Lab Results (if any)

## New API Endpoints Available

### Profile & QR Code
- `GET /api/profile` - Get user profile
- `GET /api/profile/qrcode` - Get QR code (patients only)
- `POST /api/profile/verify-qr` - Verify QR code
- `PUT /api/profile` - Update profile

### Medical Reports
- `GET /api/reports/summary` - Statistics summary
- `GET /api/reports/patient/comprehensive` - Complete medical data
- `GET /api/reports/medical-records` - All medical records
- `GET /api/reports/prescriptions` - All prescriptions
- `GET /api/reports/lab-results` - All lab results

### Doctor Operations (require DOCTOR role)
- `POST /api/reports/medical-records` - Create medical record
- `POST /api/reports/prescriptions` - Create prescription
- `POST /api/reports/lab-results` - Create lab result

## Verification Checklist

✅ Backend starts without errors
✅ Frontend loads at http://localhost:5173
✅ Can register new patient
✅ Can login successfully
✅ QR code appears on profile page
✅ Can select doctors in appointment booking
✅ Can create appointments
✅ Can view appointments list
✅ Reports page loads (even if empty)

## Common Issues & Solutions

### Issue: "Doctor not found with ID: 353..."
**Solution:** Run the SQL fix script to update doctor IDs

### Issue: Backend fails to start with hibernate errors
**Solution:** 
1. Stop backend
2. Delete `target/` folder
3. Run `.\mvnw.cmd clean install`
4. Start backend again

### Issue: QR code doesn't appear
**Solution:** 
1. Verify `qr_code` column exists in users table
2. Register a NEW patient (existing users need manual QR generation)
3. Check browser console for errors

### Issue: Reports page is empty
**Solution:** This is expected for new users - doctors need to create medical records first

## Next Steps

1. **For Doctors:** Login with doctor credentials to create medical records for patients
2. **For Patients:** View your medical history, prescriptions, and lab results
3. **For Admins:** Use admin dashboard to manage system

## Need Help?

Check these files:
- `docs/IMPLEMENTATION_GUIDE.md` - Detailed technical documentation
- Backend logs in console
- Frontend console (F12) for errors
- Database: Verify table structures match schema

## What Changed?

### Backend
- ✅ Added QR code generation with ZXing library
- ✅ Created MedicalRecord, Prescription, LabResult entities
- ✅ New ProfileController with QR code endpoints
- ✅ New ReportsController with comprehensive medical data APIs
- ✅ Updated AuthService to generate QR codes on registration

### Frontend
- ✅ Updated Profile.jsx to display QR codes
- ✅ Updated Reports.jsx to fetch real data
- ✅ Updated endpoints.js with new API routes
- ✅ Enhanced appointment booking error handling

### Database
- ✅ Added qr_code column to users table
- ✅ Created medical_records table
- ✅ Created prescriptions table
- ✅ Created lab_results table
- ✅ Fixed doctor UUID format issues

## Success Indicators

When everything is working:
1. Backend logs show no errors
2. Can book appointments without "Doctor not found"
3. QR code displays on patient profile
4. Reports page shows summary statistics
5. All API calls return 200/201 status codes

🎉 **You're all set! The new features are ready to use.**
