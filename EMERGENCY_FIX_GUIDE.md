# 🚨 EMERGENCY FIX GUIDE - MediWay

## Problem Summary
- ❌ Doctor not found errors (UUID format mismatch)
- ❌ 404 errors on `/api/appointments`
- ❌ Reports page blank
- ❌ Safe update mode blocking SQL updates

## 🔧 Complete Fix (Follow in Order)

### STEP 1: Stop Everything
```powershell
# Stop backend (press Ctrl+C in the java terminal)
# Stop frontend (press Ctrl+C in the esbuild terminal)
```

### STEP 2: Fix Database
**Open MySQL Workbench** and run this file:
```
F:\MediWay\backend\scripts\fix-everything.sql
```

**OR** use command line:
```powershell
# Navigate to MediWay directory
cd F:\MediWay

# Run the fix script (adjust path to mysql.exe if needed)
& "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u mediway_user -p mediwaydb < "backend\scripts\fix-everything.sql"
# Enter password: admin
```

This script will:
- ✅ Drop and recreate doctors table with proper UUIDs
- ✅ Add 3 doctors with correct UUID format
- ✅ Add qr_code column to users table
- ✅ Create medical_records, prescriptions, lab_results tables
- ✅ Clean up invalid appointments
- ✅ Show verification results

### STEP 3: Rebuild Backend
```powershell
cd F:\MediWay\backend
.\mvnw.cmd clean install
```

### STEP 4: Start Backend
```powershell
# In the same terminal (F:\MediWay\backend)
.\mvnw.cmd spring-boot:run
```

**Wait for this message:**
```
Started MediWayBackendApplication in X.XXX seconds
```

### STEP 5: Start Frontend
```powershell
# In a new terminal
cd F:\MediWay\frontend
npm run dev
```

## 🧪 Testing Steps

### Test 1: Authentication
1. Open http://localhost:5173
2. Register new patient account
3. Login with credentials
4. ✅ Should see dashboard

### Test 2: View Doctors
1. Go to "Book Appointment" page
2. ✅ Should see 3 doctors:
   - Dr. Sarah Johnson (Cardiology)
   - Dr. Michael Chen (Pediatrics)
   - Dr. Emily Rodriguez (Dermatology)

### Test 3: Book Appointment
1. Select a doctor
2. Choose date and time
3. Fill appointment reason
4. Click "Book Appointment"
5. ✅ Should see success message
6. ✅ Should appear in "My Appointments"

### Test 4: QR Code
1. Go to Profile page
2. ✅ Should see your QR code (for patients)
3. ✅ Can download QR code as PNG

### Test 5: Reports
1. Go to Reports page
2. ✅ Should see "No records found" (for new accounts)
3. ✅ No errors in console

## 🔍 Verify Database After Fix

Run this query in MySQL Workbench:
```sql
-- Should show 3 doctors with proper UUIDs
SELECT doctor_id, name, email, specialization 
FROM mediwaydb.doctors;

-- Expected output:
-- 51492852-aa4a-11f0-8da8-089798c3ec81 | Dr. Sarah Johnson | ...
-- 51492852-aa4a-11f0-8da8-089798c3ec82 | Dr. Michael Chen | ...
-- 51492852-aa4a-11f0-8da8-089798c3ec83 | Dr. Emily Rodriguez | ...
```

## 🐛 If Still Getting Errors

### Error: "Doctor not found"
**Check backend logs** for the doctor_id it's looking for:
```powershell
# In backend terminal, look for lines like:
Doctor not found with ID: XXXXX
```

**Then verify in database:**
```sql
SELECT * FROM mediwaydb.doctors WHERE doctor_id = 'XXXXX';
```

### Error: 404 on /api/appointments
**Verify backend is running:**
1. Check terminal shows "Started MediWayBackendApplication"
2. Test: http://localhost:8080/api/appointments/doctors
3. Should return JSON with doctors list

**Verify context path:**
```powershell
# Check this file has: server.servlet.context-path=/api
cat backend\src\main\resources\application.properties | Select-String "context-path"
```

### Error: "Column 'qr_code' doesn't exist"
**Run this in MySQL:**
```sql
ALTER TABLE mediwaydb.users ADD COLUMN qr_code VARCHAR(500);
```

## 📊 Expected API Endpoints

After fix, these should work:

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/login` | POST | Login |
| `/api/auth/register` | POST | Register |
| `/api/appointments` | POST | Book appointment |
| `/api/appointments/my` | GET | Get my appointments |
| `/api/appointments/doctors` | GET | Get all doctors |
| `/api/profile` | GET | Get profile |
| `/api/profile/qrcode` | GET | Get QR code |
| `/api/reports/summary` | GET | Get reports summary |

## ✅ Success Indicators

You'll know it's working when:
- ✅ No errors in browser console
- ✅ Can see doctors list
- ✅ Can book appointments
- ✅ Appointments appear in "My Appointments"
- ✅ QR code shows in Profile
- ✅ No "Doctor not found" errors

## 🆘 Still Stuck?

**Check backend logs in detail:**
```powershell
cd F:\MediWay\backend
.\mvnw.cmd spring-boot:run > backend-logs.txt 2>&1
# Then open backend-logs.txt to see full error details
```

**Check database connection:**
```sql
-- In MySQL Workbench
SELECT @@version;
SHOW DATABASES;
USE mediwaydb;
SHOW TABLES;
```

**Verify JWT token:**
1. Open browser DevTools (F12)
2. Go to Application > Local Storage
3. Look for `mediway_token`
4. Should be a long JWT string

---

**Created:** October 17, 2025  
**Last Updated:** After implementing QR code system and medical records
